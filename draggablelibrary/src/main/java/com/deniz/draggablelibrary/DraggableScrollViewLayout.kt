package com.deniz.draggablelibrary

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.deniz.draggablelibrary.callbacks.SimpleAnimatorListener
import com.deniz.draggablelibrary.utils.DraggableUtil
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

private const val RESET_ANIMATION_DURATION = 200L
private const val TOUCH_OFFSET = 20.0

class DraggableScrollViewLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private var activity: Activity = (context as Activity)

    private var scrollView: ScrollView? = null

    private var mWindowMaxDistance = 0.0
    private var mWindowHeight = 0.0
    private var mWindowWidth = 0.0

    private var mFirstTouchX = -1F
    private var mFirstTouchY = -1F

    private var mTouchDeltaX = -1F
    private var mTouchDeltaY = -1F

    private var mCurrentTouchX = -1F
    private var mCurrentTouchY = -1F

    private var mBasePointX = 0F
    private var mBasePointY = 0F

    private var mScrollListener: ScrollListener? = null
    private var mDragListener: DragListener? = null

    private var mDragLock = false

    private var _mMinCornerRadius = 0F
    private var _mMaxCornerRadius = 0F
    private var _mDraggableAngle = 0.0
    private var _mBackgroundColor = ""
    private var _mBackgroundColorOpacityMax = 255F
    private var _mBackgroundColorOpacityMin = 70F
    private var _mScaleFactor = 0.6
    private var _mScaleEnabled = true
    private var _mTransparentBackground = false
    private var _mFinishOffset = 0F

    private var _mExitAnimation = R.anim.draggable_exit_animation

    private var _mCornersFlag = 0
    private var _mDirectionsFlag = 0

    private var mUserLock = false

    private var mTouchTimeStart = 0L
    private var mIsFinishing = false

    private var mLastScrollOffset = -1
    private var mModeChangeXOffset = 0F

    private var mMode = MODE.SCROLL

    init {
        setupAttributes(attrs)

        background = ContextCompat.getDrawable(context, R.color.transparent)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (mUserLock) return super.onInterceptTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchTimeStart = System.currentTimeMillis()

                mFirstTouchX = event.rawX
                mFirstTouchY = event.rawY

                mDragLock = false

                mModeChangeXOffset = 0F

                mLastScrollOffset = if (scrollView != null) scrollView!!.scrollY else 0
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrentTouchX = event.rawX
                mCurrentTouchY = event.rawY

                mTouchDeltaX = mCurrentTouchX - mFirstTouchX
                mTouchDeltaY = mCurrentTouchY - mFirstTouchY

                if (abs(mTouchDeltaX) < TOUCH_OFFSET && abs(mTouchDeltaY) < TOUCH_OFFSET) return false

                if (abs(mTouchDeltaY) > abs(mTouchDeltaX)) {
                    mFirstTouchX = mCurrentTouchX
                    mFirstTouchY = mCurrentTouchY

                    mMode =
                        if (mTouchDeltaY > 0 && mLastScrollOffset == 0) {
                            mDragListener?.onDragStarted(mCurrentTouchX, mCurrentTouchY)

                            MODE.DRAG
                        } else {
                            mScrollListener?.onScrollStarted(mCurrentTouchX, mCurrentTouchY)

                            MODE.SCROLL
                        }

                    return true
                }
            }
        }

        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mUserLock) return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchTimeStart = System.currentTimeMillis()

                mFirstTouchX = event.rawX
                mFirstTouchY = event.rawY

                mDragLock = false

                mModeChangeXOffset = 0F

                mLastScrollOffset = if (scrollView != null) scrollView!!.scrollY else 0
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrentTouchX = event.rawX
                mCurrentTouchY = event.rawY

                mTouchDeltaX = mCurrentTouchX - mFirstTouchX
                mTouchDeltaY = mCurrentTouchY - mFirstTouchY

                when (mMode) {
                    MODE.SCROLL -> {
                        scrollView?.scrollTo(0, mLastScrollOffset + -mTouchDeltaY.toInt())

                        mScrollListener?.onScroll(scrollView!!.scrollX, scrollView!!.scrollY)

                        if (scrollView?.scrollY == 0 && mTouchDeltaY > 0) {
                            mFirstTouchX = event.rawX - mModeChangeXOffset
                            mFirstTouchY = event.rawY

                            mModeChangeXOffset = 0F

                            mMode = MODE.DRAG

                            mScrollListener?.onScrollFinished()
                            mDragListener?.onDragStarted(mCurrentTouchX, mCurrentTouchY)
                        }
                    }
                    MODE.DRAG -> {
                        if (mTouchDeltaY <= 0F) {
                            translationY = 0F

                            if (scrollView != null && scrollView!!.canScrollVertically(1)) {
                                mLastScrollOffset = 0

                                mModeChangeXOffset = mTouchDeltaX

                                mMode = MODE.SCROLL

                                mDragListener?.onDragFinished()
                                mScrollListener?.onScrollStarted(mCurrentTouchX, mCurrentTouchY)
                            } else if (moveLeft() || moveRight()) {
                                translationX = if (mTouchDeltaX > 0 && moveRight()) {
                                    mTouchDeltaX
                                } else if (mTouchDeltaX < 0 && moveLeft()) {
                                    mTouchDeltaX
                                } else {
                                    0F
                                }

                                mDragListener?.onDrag(
                                    mCurrentTouchX,
                                    mCurrentTouchY,
                                    mTouchDeltaX,
                                    mTouchDeltaY
                                )
                            }
                        } else {
                            if (!moveAll()) {
                                if (moveLeft() || moveRight()) {
                                    translationX = if (mTouchDeltaX > 0 && moveRight()) {
                                        mTouchDeltaX
                                    } else if (mTouchDeltaX < 0 && moveLeft()) {
                                        mTouchDeltaX
                                    } else {
                                        0F
                                    }
                                }
                                if (moveBottom()) {
                                    translationY = if (mTouchDeltaY > 0 && moveBottom()) {
                                        mTouchDeltaY
                                    } else {
                                        0F
                                    }
                                }
                            } else {
                                translationX = mTouchDeltaX
                                translationY = mTouchDeltaY
                            }

                            mDragListener?.onDrag(
                                mCurrentTouchX,
                                mCurrentTouchY,
                                mTouchDeltaX,
                                mTouchDeltaY
                            )
                        }
                    }
                }

                requestLayout()
            }
            MotionEvent.ACTION_UP -> {
                if (_mFinishOffset > 0F) {
                    if (mTouchDeltaY >= _mFinishOffset) {
                        finish()
                    } else {
                        resetUI()
                    }
                } else {
                    val distance = calculateDistance().toInt()

                    if (distance == 0) {
                        resetUI()
                    } else {
                        val time = max(1, System.currentTimeMillis() - mTouchTimeStart)
                        val velocity = distance / time

                        if (velocity > 0L) {
                            finish()
                        } else {
                            resetUI()
                        }
                    }
                }
            }
        }

        return true
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)

        if (hasWindowFocus) {
            mWindowHeight = context.resources.displayMetrics.heightPixels.toDouble()
            mWindowWidth = context.resources.displayMetrics.widthPixels.toDouble()

            mWindowMaxDistance = sqrt(mWindowHeight.pow(2) + mWindowWidth.pow(2))

            mBasePointX = x
            mBasePointY = y

            for (i in 0 until childCount) {
                val child = getChildAt(i)

                if (child is ScrollView) {
                    scrollView = child
                    break
                }
            }

            handleScale(0.0)
            handleBackgroundColor(0.0)

            requestLayout()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val distance = calculateDistance()

        if (!mIsFinishing) {
            handleScale(distance)
            handleBackgroundColor(distance)
        }

        if (_mMaxCornerRadius == 0F) {
            super.dispatchDraw(canvas)
            return
        }

        val count = canvas.save()
        val path = Path()
        val rect = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        val arrayRadius = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        val corners = handleCornerRadius(distance)

        arrayRadius[0] = corners["topLeft"]!!
        arrayRadius[1] = corners["topLeft"]!!

        arrayRadius[2] = corners["topRight"]!!
        arrayRadius[3] = corners["topRight"]!!

        arrayRadius[4] = corners["bottomLeft"]!!
        arrayRadius[5] = corners["bottomLeft"]!!

        arrayRadius[6] = corners["bottomRight"]!!
        arrayRadius[7] = corners["bottomRight"]!!

        path.addRoundRect(rect, arrayRadius, Path.Direction.CW)
        canvas.clipPath(path)

        super.dispatchDraw(canvas)

        canvas.restoreToCount(count)
    }

    private fun calculateDistance(): Double {
        val distanceX = mBasePointX - x.toDouble()
        val distanceY = mBasePointY - y.toDouble()

        return sqrt(distanceX.pow(2) + distanceY.pow(2))
    }

    private fun handleCornerRadius(distance: Double): MutableMap<String, Float> {
        val mappedValue = DraggableUtil.map(
            false,
            0.0,
            mWindowHeight / 2,
            _mMinCornerRadius.toDouble(),
            _mMaxCornerRadius.toDouble(),
            if (distance > mWindowHeight / 2) mWindowHeight / 2 else distance
        ).toFloat()

        val corners = mutableMapOf<String, Float>()

        if (allCorners()) {
            corners["topLeft"] = mappedValue
            corners["topRight"] = mappedValue
            corners["bottomLeft"] = mappedValue
            corners["bottomRight"] = mappedValue
        } else {
            corners["topLeft"] = if (topLeftCorner()) mappedValue else 0F
            corners["topRight"] = if (topRightCorner()) mappedValue else 0F
            corners["bottomLeft"] = if (bottomLeftCorner()) mappedValue else 0F
            corners["bottomRight"] = if (bottomRightCorner()) mappedValue else 0F
        }

        return corners
    }

    private fun handleBackgroundColor(distance: Double) {
        if (_mTransparentBackground) {
            activity.window.decorView.setBackgroundColor(Color.parseColor("#00000000"))
        } else {
            var mappedColor =
                DraggableUtil.map(
                    true,
                    0.0,
                    mWindowMaxDistance,
                    0.0,
                    _mBackgroundColorOpacityMax.toDouble(),
                    distance
                )

            if (mappedColor < _mBackgroundColorOpacityMin)
                mappedColor = _mBackgroundColorOpacityMin.toInt()

            activity.window.decorView.setBackgroundColor(
                Color.parseColor(("#%02X$_mBackgroundColor").format(mappedColor))
            )
        }
    }

    private fun handleScale(distance: Double) {
        if (!_mScaleEnabled) return

        val mappedValue = DraggableUtil.mapScaleFactor(
            distance,
            0.0,
            mWindowMaxDistance,
            _mScaleFactor,
            1.0
        )

        scaleX = mappedValue
        scaleY = mappedValue
    }

    private fun resetUI() {
        if (translationY != 0F || translationX != 0F) {
            with(ValueAnimator.ofFloat(scaleX, 1.0F)) {
                duration = RESET_ANIMATION_DURATION
                addUpdateListener { requestLayout() }
                start()
            }

            animate()
                .translationX(0F)
                .translationY(0F)
                .scaleX(1F)
                .scaleY(1F)
                .setDuration(RESET_ANIMATION_DURATION)
                .setListener(object : SimpleAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        finishListeners()
                    }
                })
                .start()
        } else {
            finishListeners()
        }
    }

    private fun finish() {
        mIsFinishing = true

        activity.window.decorView.setBackgroundColor(Color.parseColor("#00FFFFFF"))
        activity.finish()
        activity.overridePendingTransition(0, _mExitAnimation)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val styledAttr =
            context.obtainStyledAttributes(attrs, R.styleable.DraggableScrollViewLayout)

        _mMinCornerRadius =
            styledAttr.getDimension(
                R.styleable.DraggableScrollViewLayout_draggableMinCornerRadius,
                _mMinCornerRadius
            )

        _mMaxCornerRadius =
            styledAttr.getDimension(
                R.styleable.DraggableScrollViewLayout_draggableMaxCornerRadius,
                _mMaxCornerRadius
            )

        _mDirectionsFlag =
            styledAttr.getInteger(
                R.styleable.DraggableScrollViewLayout_draggableDirections,
                _mDirectionsFlag
            )

        _mCornersFlag =
            styledAttr.getInteger(
                R.styleable.DraggableScrollViewLayout_draggableCorners,
                _mCornersFlag
            )

        _mDraggableAngle =
            styledAttr.getFloat(R.styleable.DraggableScrollViewLayout_draggableDetectionAngle, 90F)
                .toDouble()

        _mBackgroundColor =
            Integer.toHexString(
                styledAttr.getColor(
                    R.styleable.DraggableScrollViewLayout_draggableBackgroundColor,
                    0
                )
            )

        _mBackgroundColorOpacityMin =
            styledAttr.getFloat(
                R.styleable.DraggableScrollViewLayout_draggableBackgroundOpacityMin,
                _mBackgroundColorOpacityMin
            )

        _mBackgroundColorOpacityMax =
            styledAttr.getFloat(
                R.styleable.DraggableScrollViewLayout_draggableBackgroundOpacityMax,
                _mBackgroundColorOpacityMax
            )

        _mTransparentBackground =
            styledAttr.getBoolean(
                R.styleable.DraggableScrollViewLayout_draggableTransparentBackground,
                _mTransparentBackground
            )

        _mScaleFactor =
            styledAttr.getFloat(
                R.styleable.DraggableScrollViewLayout_draggableScaleFactor,
                _mScaleFactor.toFloat()
            ).toDouble()

        _mScaleEnabled =
            styledAttr.getBoolean(
                R.styleable.DraggableScrollViewLayout_draggableScaleEnabled,
                _mScaleEnabled
            )

        _mExitAnimation =
            styledAttr.getResourceId(
                R.styleable.DraggableScrollViewLayout_draggableExitAnimation,
                _mExitAnimation
            )

        _mFinishOffset =
            styledAttr.getDimension(
                R.styleable.DraggableScrollViewLayout_draggableFinishOffset,
                _mFinishOffset
            )

        _mBackgroundColor =
            if (_mBackgroundColor == "0") "000000"
            else _mBackgroundColor.toUpperCase(Locale.ROOT).substring(2)

        if (_mMinCornerRadius != 0F && _mMaxCornerRadius == 0F)
            _mMaxCornerRadius = _mMinCornerRadius

        DraggableUtil.checkAttributes(
            _mMinCornerRadius,
            _mMaxCornerRadius,
            _mDraggableAngle,
            _mBackgroundColorOpacityMin,
            _mBackgroundColorOpacityMax,
            _mTransparentBackground,
            _mScaleFactor
        )

        styledAttr.recycle()
    }

    fun setDragListener(listener: DragListener) {
        this.mDragListener = listener
    }

    fun setScrollListener(listener: ScrollListener) {
        this.mScrollListener = listener
    }

    private fun finishListeners() {
        if (mMode == MODE.DRAG) mDragListener?.onDragFinished()
        else mScrollListener?.onScrollFinished()
    }

    fun enableDrag() {
        mUserLock = false
    }

    fun disableDrag() {
        mUserLock = true
    }

    interface ScrollListener {
        fun onScrollStarted(rawX: Float, rawY: Float)
        fun onScroll(scrollX: Int, scrollY: Int)
        fun onScrollFinished()
    }

    interface DragListener {
        fun onDragStarted(rawX: Float, rawY: Float)
        fun onDrag(rawX: Float, rawY: Float, touchDeltaX: Float, touchDeltaY: Float)
        fun onDragFinished()
    }

    private enum class MODE {
        SCROLL,
        DRAG
    }

    private fun topLeftCorner() = _mCornersFlag or 1 == _mCornersFlag
    private fun topRightCorner() = _mCornersFlag or 2 == _mCornersFlag
    private fun bottomRightCorner() = _mCornersFlag or 4 == _mCornersFlag
    private fun bottomLeftCorner() = _mCornersFlag or 8 == _mCornersFlag
    private fun allCorners() = _mCornersFlag or 15 == _mCornersFlag

    private fun moveBottom() = _mDirectionsFlag or 2 == _mDirectionsFlag
    private fun moveLeft() = _mDirectionsFlag or 4 == _mDirectionsFlag
    private fun moveRight() = _mDirectionsFlag or 8 == _mDirectionsFlag
    private fun moveAll() = _mDirectionsFlag or 15 == _mDirectionsFlag

    private fun log(s: String) {
        Log.d("Draggable", s)
    }
}