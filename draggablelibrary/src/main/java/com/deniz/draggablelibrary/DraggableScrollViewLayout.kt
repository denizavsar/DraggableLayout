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
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.deniz.draggablelibrary.utils.DraggableUtil
import kotlin.math.abs


class DraggableScrollViewLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val mResetAnimationDuration = 200L
    private val mTouchOffset = 20.0

    private var mActivityFinishOffset = 400.0

    private var activity: Activity = (context as Activity)

    private var scrollView: ScrollView? = null

    private lateinit var params: MarginLayoutParams

    private var mWindowHeight = 0.0
    private var mWindowWidth = 0

    private var mFirstTouchX = -1F
    private var mFirstTouchY = -1F

    private var mCurrentTouchX = -1F
    private var mCurrentTouchY = -1F

    private var mTouchDeltaX = -1F
    private var mTouchDeltaY = -1F

    private var mLastScrollOffset = -1
    private var mModeChangeXOffset = 0F

    private var mMode = MODE.SCROLL

    private var mMinCornerRadius = 0F
    private var mMaxCornerRadius = 0F

    init {
        setupAttributes(attrs)

        background = ContextCompat.getDrawable(context, android.R.color.transparent)

        handleBackgroundColor(0)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mFirstTouchX = event.rawX
                mFirstTouchY = event.rawY

                mLastScrollOffset = if (scrollView != null) scrollView!!.scrollY else 0
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrentTouchX = event.rawX
                mCurrentTouchY = event.rawY

                mTouchDeltaX = mCurrentTouchX - mFirstTouchX
                mTouchDeltaY = mCurrentTouchY - mFirstTouchY

                if (abs(mTouchDeltaY) > mTouchOffset) {
                    if (!isSwipe()) {
                        mMode =
                            if (mTouchDeltaY > 0 && mLastScrollOffset == 0) MODE.DRAG else MODE.SCROLL

                        mFirstTouchX = event.rawX
                        mFirstTouchY = event.rawY
                        return true
                    }
                }
            }
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mTouchDeltaX = event.rawX - mFirstTouchX
                mTouchDeltaY = event.rawY - mFirstTouchY

                when (mMode) {
                    MODE.SCROLL -> {
                        scrollView?.scrollTo(0, mLastScrollOffset + -mTouchDeltaY.toInt())

                        if (scrollView?.scrollY == 0 && mTouchDeltaY > 0) {
                            mFirstTouchX = event.rawX - mModeChangeXOffset
                            mFirstTouchY = event.rawY

                            mModeChangeXOffset = 0F

                            mMode = MODE.DRAG
                        }
                    }
                    MODE.DRAG -> {
                        if (mTouchDeltaY <= 0F) {
                            translationY = 0F

                            if (scrollView != null && scrollView!!.canScrollVertically(1)) {
                                mLastScrollOffset = 0

                                mModeChangeXOffset = mTouchDeltaX

                                mMode = MODE.SCROLL
                            } else {
                                translationX = mTouchDeltaX
                            }
                        } else {
                            translationX = mTouchDeltaX
                            translationY = mTouchDeltaY

                            handleMargins(mTouchDeltaY.toDouble())
                            handleBackgroundColor(mTouchDeltaY.toInt())

                            requestLayout()
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mMode == MODE.DRAG) {
                    if (mTouchDeltaY > mActivityFinishOffset) {
                        activity.window.decorView.setBackgroundColor(Color.parseColor("#00FFFFFF"))
                        activity.finish()
                    } else {
                        resetUI()
                    }
                } else {
                    resetUI()
                }

                mModeChangeXOffset = 0F
            }
        }
        return super.onTouchEvent(event)
    }

    private fun resetUI() {
        if (params.leftMargin != 0) {
            with(ValueAnimator.ofFloat(params.leftMargin.toFloat(), 0F)) {
                duration = mResetAnimationDuration
                addUpdateListener {
                    val value = it.animatedValue as Float
                    params.leftMargin = value.toInt()
                    params.rightMargin = value.toInt()

                    layoutParams = params
                }
                start()
            }
        }

        if (translationX != 0F || translationY != 0F) {
            animate()
                .translationX(0F)
                .translationY(0F)
                .setDuration(mResetAnimationDuration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        handleBackgroundColor(0)
                        requestLayout()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                .start()
        }
    }

    private fun handleBackgroundColor(difference: Int) {
        activity.window.decorView.setBackgroundColor(getCustomColor(difference))
    }

    private fun handleMargins(difference: Double) {
        val mappedValue = mapMargin(difference)

        params.rightMargin = mappedValue
        params.leftMargin = mappedValue

        layoutParams = params
    }

    private fun getCustomColor(margin: Int): Int {
        val hex = "#%02X141414".format(mapAlphaColor(margin.toDouble()))
        return Color.parseColor(hex)
    }

    private fun mapCornerRadius(difference: Double): Float {
        val diff = if (difference > mWindowHeight / 2) mWindowHeight / 2 else difference
        return DraggableUtil.map(
            false,
            0.0,
            mWindowHeight / 2,
            mMinCornerRadius.toDouble(),
            mMaxCornerRadius.toDouble(),
            diff
        ).toFloat()
    }

    private fun mapMargin(difference: Double): Int {
        return DraggableUtil.map(false, 0.0, mWindowHeight, 0.0, mWindowWidth / 8.0, difference)
    }

    private fun mapAlphaColor(alphaMargin: Double): Int {
        var mappedColor = DraggableUtil.map(true, 0.0, mWindowHeight, 0.0, 255.0, alphaMargin)
        if (mappedColor < 70) mappedColor = 70

        return mappedColor
    }

    private fun isSwipe(): Boolean {
        val xPercentage = (100 * abs(mTouchDeltaX)) / mWindowWidth.toDouble()
        val yPercentage = (100 * abs(mTouchDeltaY)) / mWindowHeight

        // IF X Percentage exceed 5.0 and Y Percentage still below 1.0 = Swipe
        return !(yPercentage > 1.0 && xPercentage < 5.0)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            params = layoutParams as MarginLayoutParams

            mWindowHeight = context.resources.displayMetrics.heightPixels.toDouble()
            mWindowWidth = context.resources.displayMetrics.widthPixels

            mActivityFinishOffset = mWindowHeight / 5

            for (i in 0 until childCount) {
                val child = getChildAt(i)

                if (child is ScrollView) {
                    scrollView = child
                    break
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val cornerRadius = mapCornerRadius(y.toDouble())

        val count = canvas.save()
        val path = Path()
        val rect = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
        val arrayRadius = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        arrayRadius[0] = cornerRadius
        arrayRadius[1] = cornerRadius

        arrayRadius[2] = cornerRadius
        arrayRadius[3] = cornerRadius

        arrayRadius[4] = cornerRadius
        arrayRadius[5] = cornerRadius

        arrayRadius[6] = cornerRadius
        arrayRadius[7] = cornerRadius

        path.addRoundRect(rect, arrayRadius, Path.Direction.CW)
        canvas.clipPath(path)

        super.dispatchDraw(canvas)

        canvas.restoreToCount(count)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val styledAttr =
            context.obtainStyledAttributes(attrs, R.styleable.DraggableScrollViewLayout)

        mMinCornerRadius =
            styledAttr.getDimension(
                R.styleable.DraggableScrollViewLayout_draggableMinCornerRadius,
                0F
            )
        mMaxCornerRadius =
            styledAttr.getDimension(
                R.styleable.DraggableScrollViewLayout_draggableMaxCornerRadius,
                0F
            )

        styledAttr.recycle()
    }

    private enum class MODE {
        SCROLL,
        DRAG
    }
}