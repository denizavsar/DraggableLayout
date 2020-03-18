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
import androidx.core.content.ContextCompat
import com.deniz.draggablelibrary.callbacks.SimpleAnimatorListener
import com.deniz.draggablelibrary.utils.DraggableUtil
import com.deniz.draggablelibrary.utils.isPositive
import java.util.*
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

private const val RESET_ANIMATION_DURATION = 200L
private const val TOUCH_OFFSET = 40.0

class DraggableStoryFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private var activity: Activity = (context as Activity)

    private var params: MarginLayoutParams? = null

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

    private var mDragListener: DragListener? = null

    private var mDragLock = false

    private var _mMinCornerRadius = 0F
    private var _mMaxCornerRadius = 0F
    private var _mDraggableAngle = 0.0
    private var _mBackgroundColor = ""
    private var _mBackgroundColorOpacityMax = 255F
    private var _mBackgroundColorOpacityMin = 70F
    private var _mMarginEnabled = true
    private var _mTransparentBackground = false

    private var _mExitAnimation = R.anim.draggable_exit_animation

    private var _mCornersFlag = 0
    private var _mDirectionsFlag = 0

    private var mUserLock = false

    private var mTouchTimeStart = 0L
    private var mIsFinishing = false

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
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrentTouchX = event.rawX
                mCurrentTouchY = event.rawY

                mTouchDeltaX = mCurrentTouchX - mFirstTouchX
                mTouchDeltaY = mCurrentTouchY - mFirstTouchY

                if (shouldHandleTouchEvent() && !mDragLock) {
                    mFirstTouchX = mCurrentTouchX
                    mFirstTouchY = mCurrentTouchY

                    mDragListener?.onDragStarted(mFirstTouchX, mFirstTouchY)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mUserLock) return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mFirstTouchX = event.rawX
                mFirstTouchY = event.rawY

                mDragLock = false
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrentTouchX = event.rawX
                mCurrentTouchY = event.rawY

                mTouchDeltaX = mCurrentTouchX - mFirstTouchX
                mTouchDeltaY = mCurrentTouchY - mFirstTouchY

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
                    if (moveTop() || moveBottom()) {
                        translationY = if (mTouchDeltaY > 0 && moveBottom()) {
                            mTouchDeltaY
                        } else if (mTouchDeltaY < 0 && moveTop()) {
                            mTouchDeltaY
                        } else {
                            0F
                        }
                    }
                } else {
                    translationX = mTouchDeltaX
                    translationY = mTouchDeltaY
                }

                mDragListener?.onDrag(mTouchDeltaX, mTouchDeltaY)

                requestLayout()
            }
            MotionEvent.ACTION_UP -> {
                val velocity =
                    calculateDistance().toInt() / (System.currentTimeMillis() - mTouchTimeStart)

                if (velocity > 0L) {
                    finish()
                } else {
                    resetUI()
                }
            }
        }

        return true
    }

    private fun shouldHandleTouchEvent(): Boolean {
        if (abs(mTouchDeltaX) <= TOUCH_OFFSET && abs(mTouchDeltaY) <= TOUCH_OFFSET) return false

        var angle = atan2(mTouchDeltaY, mTouchDeltaX) * 180 / Math.PI

        if (!angle.isPositive()) angle = 180 + (180 + angle)

        val bottomRight = 90.0 - (_mDraggableAngle / 2)
        val bottomLeft = 90.0 + (_mDraggableAngle / 2)

        val leftBottom = 180.0 - (_mDraggableAngle / 2) + 0.01
        val leftTop = 180.0 + (_mDraggableAngle / 2) - 0.01

        val topLeft = 270.0 - (_mDraggableAngle / 2)
        val topRight = 270.0 + (_mDraggableAngle / 2)

        val rightTop = 360 - (_mDraggableAngle / 2)
        val rightBottom = (_mDraggableAngle / 2) - 1.0

        when {
            angle in bottomRight..bottomLeft -> {
                if (moveBottom()) return true
            }
            angle in leftBottom..leftTop -> {
                if (moveLeft()) return true
            }
            angle in topLeft..topRight -> {
                if (moveTop()) return true
            }
            angle > rightTop || angle < rightBottom -> {
                if (moveRight()) return true
            }
        }

        mDragLock = true
        return false
    }

    private fun resetUI() {
        val animatorStartValue =
            if (params != null && params!!.leftMargin != 0 && params!!.rightMargin != 0)
                params!!.leftMargin.toFloat()
            else
                RESET_ANIMATION_DURATION.toFloat()

        with(ValueAnimator.ofFloat(animatorStartValue, 0F)) {
            duration = RESET_ANIMATION_DURATION
            addUpdateListener {
                val value = it.animatedValue as Float
                if (_mMarginEnabled && params?.leftMargin != 0) {
                    params?.leftMargin = value.toInt()
                    params?.rightMargin = value.toInt()
                    if (params != null) layoutParams = params
                }
                requestLayout()
            }
            start()
        }

        if (translationY != 0F || translationX != 0F) {
            animate()
                .translationX(0F)
                .translationY(0F)
                .setDuration(RESET_ANIMATION_DURATION)
                .setListener(object : SimpleAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        mDragListener?.onDragFinished()
                        requestLayout()
                    }
                })
                .start()
        } else {
            mDragListener?.onDragFinished()
        }
    }

    private fun finish() {
        mIsFinishing = true

        activity.window.decorView.setBackgroundColor(Color.parseColor("#00FFFFFF"))
        activity.finish()
        activity.overridePendingTransition(0, _mExitAnimation)
    }

    private fun handleBackgroundColor(distance: Double) {
        if (_mTransparentBackground) {
            activity.window.decorView.setBackgroundColor(
                Color.parseColor("#00000000")
            )
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

            if (mappedColor < _mBackgroundColorOpacityMin) mappedColor =
                _mBackgroundColorOpacityMin.toInt()

            activity.window.decorView.setBackgroundColor(
                Color.parseColor(("#%02X$_mBackgroundColor").format(mappedColor))
            )
        }
    }

    private fun handleMargins(distance: Double) {
        if (!_mMarginEnabled) return

        val mappedValue =
            DraggableUtil.map(
                false,
                0.0,
                mWindowHeight,
                0.0,
                mWindowWidth / 5.0,
                distance
            )

        params?.rightMargin = mappedValue
        params?.leftMargin = mappedValue

        if (params != null) layoutParams = params
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

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            params = layoutParams as MarginLayoutParams

            mWindowHeight = context.resources.displayMetrics.heightPixels.toDouble()
            mWindowWidth = context.resources.displayMetrics.widthPixels.toDouble()

            mWindowMaxDistance = sqrt(mWindowHeight.pow(2) + mWindowWidth.pow(2))

            mBasePointX = x
            mBasePointY = y

            val distance = calculateDistance()

            handleMargins(distance)
            handleBackgroundColor(distance)
            requestLayout()
        }
    }

    private fun calculateDistance(): Double {
        val positionArray = IntArray(2)
        getLocationOnScreen(positionArray)

        val distanceX = mBasePointX - positionArray[0].toDouble()
        val distanceY = mBasePointY - positionArray[1].toDouble()

        return sqrt(distanceX.pow(2) + distanceY.pow(2))
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (mIsFinishing) {
            super.dispatchDraw(canvas)
            return
        }

        val distance = calculateDistance()

        handleMargins(distance)
        handleBackgroundColor(distance)

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

    private fun setupAttributes(attrs: AttributeSet?) {
        val styledAttr =
            context.obtainStyledAttributes(attrs, R.styleable.DraggableStoryFrameLayout)

        _mMinCornerRadius =
            styledAttr.getDimension(
                R.styleable.DraggableStoryFrameLayout_draggableMinCornerRadius,
                _mMinCornerRadius
            )

        _mMaxCornerRadius =
            styledAttr.getDimension(
                R.styleable.DraggableStoryFrameLayout_draggableMaxCornerRadius,
                _mMaxCornerRadius
            )

        _mDirectionsFlag =
            styledAttr.getInteger(
                R.styleable.DraggableStoryFrameLayout_draggableDirections,
                _mDirectionsFlag
            )

        _mCornersFlag =
            styledAttr.getInteger(
                R.styleable.DraggableStoryFrameLayout_draggableCorners,
                _mCornersFlag
            )

        _mDraggableAngle =
            styledAttr.getFloat(R.styleable.DraggableStoryFrameLayout_draggableDetectionAngle, 90F)
                .toDouble()

        _mBackgroundColor =
            Integer.toHexString(
                styledAttr.getColor(
                    R.styleable.DraggableStoryFrameLayout_draggableBackgroundColor,
                    0
                )
            )

        _mBackgroundColorOpacityMin =
            styledAttr.getFloat(
                R.styleable.DraggableStoryFrameLayout_draggableBackgroundOpacityMin,
                _mBackgroundColorOpacityMin
            )

        _mBackgroundColorOpacityMax =
            styledAttr.getFloat(
                R.styleable.DraggableStoryFrameLayout_draggableBackgroundOpacityMax,
                _mBackgroundColorOpacityMax
            )

        _mMarginEnabled =
            styledAttr.getBoolean(
                R.styleable.DraggableStoryFrameLayout_draggableMarginEnabled,
                _mMarginEnabled
            )

        _mTransparentBackground =
            styledAttr.getBoolean(
                R.styleable.DraggableStoryFrameLayout_draggableTransparentBackground,
                _mTransparentBackground
            )

        _mExitAnimation =
            styledAttr.getResourceId(
                R.styleable.DraggableStoryFrameLayout_draggableExitAnimation,
                _mExitAnimation
            )

        _mBackgroundColor =
            if (_mBackgroundColor == "0") "000000"
            else _mBackgroundColor.toUpperCase(Locale.ROOT).substring(2)

        if (_mDraggableAngle > 90.0) {
            Log.wtf(
                "Draggable",
                "Angle must be equal or lower than 90.0 degree! Angle set to 90.0"
            )
            _mDraggableAngle = 90.0
        }

        if (_mBackgroundColorOpacityMin < 0.0) {
            Log.wtf(
                "Draggable",
                "Background opacity min must be equal or higher than 0! Background opacity min set to 0"
            )
            _mBackgroundColorOpacityMin = 0F
        }

        if (_mBackgroundColorOpacityMax > 255.0) {
            Log.wtf(
                "Draggable",
                "Background opacity max must be equal or lower than 255! Background opacity max set to 255"
            )
            _mBackgroundColorOpacityMax = 255F
        }

        if (_mMinCornerRadius != 0F && _mMaxCornerRadius == 0F)
            _mMaxCornerRadius = _mMinCornerRadius

        styledAttr.recycle()
    }

    fun setDragListener(listener: DragListener) {
        this.mDragListener = listener
    }

    fun enableDrag() {
        mUserLock = false
    }

    fun disableDrag() {
        mUserLock = true
    }

    interface DragListener {
        fun onDragStarted(rawX: Float, rawY: Float)
        fun onDrag(touchDeltaX: Float, touchDeltaY: Float)
        fun onDragFinished()
    }

    private fun topLeftCorner() = _mCornersFlag or 1 == _mCornersFlag
    private fun topRightCorner() = _mCornersFlag or 2 == _mCornersFlag
    private fun bottomRightCorner() = _mCornersFlag or 4 == _mCornersFlag
    private fun bottomLeftCorner() = _mCornersFlag or 8 == _mCornersFlag
    private fun allCorners() = _mCornersFlag or 15 == _mCornersFlag

    private fun moveTop() = _mDirectionsFlag or 1 == _mDirectionsFlag
    private fun moveBottom() = _mDirectionsFlag or 2 == _mDirectionsFlag
    private fun moveLeft() = _mDirectionsFlag or 4 == _mDirectionsFlag
    private fun moveRight() = _mDirectionsFlag or 8 == _mDirectionsFlag
    private fun moveAll() = _mDirectionsFlag or 15 == _mDirectionsFlag

    private fun log(s: String) {
        Log.d("DENIZDD", s)
    }
}