package com.deniz.draggablelibrary.utils

/**
 * Created by deniz on 9.03.2020
 */
object DraggableUtil {
    fun map(
        isReverse: Boolean,
        minStart: Double,
        maxStart: Double,
        minResult: Double,
        maxResult: Double,
        desiredValue: Double
    ): Int {
        var mappedValue =
            (desiredValue - minStart) / (maxStart - minStart) * (maxResult - minResult) + minResult

        if (mappedValue > maxResult) mappedValue =
            maxResult else if (mappedValue < minResult) mappedValue = minResult
        if (isReverse) mappedValue = (maxResult.toInt() - mappedValue.toInt()).toDouble()

        return mappedValue.toInt()
    }

    fun getFinishAnimationOffset(): Pair<Float, Float> {
        /*
        var deltaX = mTouchDeltaX
        var xCount = 0

        var deltaY = mTouchDeltaY
        var yCount = 0

        while (abs(deltaX) < mWindowWidth) {
            xCount++
            deltaX += mTouchDeltaX
        }

        while (abs(deltaY) < mWindowHeight) {
            yCount++
            deltaY += mTouchDeltaY
        }

        val finalMultiplier = if (xCount >= yCount) xCount else yCount

        return Pair(mTouchDeltaX * finalMultiplier, mTouchDeltaY * finalMultiplier)

         */
        return Pair(0F, 0F)
    }
}