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

    fun checkAttributes(
        minCornerRadius: Float,
        maxCornerRadius: Float,
        angle: Double,
        minBackgroundOpacity: Float,
        maxBackgroundOpacity: Float,
        transparentBackground: Boolean
    ) {
        if (minCornerRadius < 0F) {
            throw IllegalArgumentException(
                "Minimum corner radius must be equal or higher than 0.0 degree"
            )
        }

        if (minCornerRadius > maxCornerRadius) {
            throw IllegalArgumentException(
                "Minimum corner radius must be equal or lower than maximum corner radius"
            )
        }

        if (angle > 90.0 || angle < 0.0) {
            throw IllegalArgumentException(
                "Angle must be between 0.0 and 90.0 degrees"
            )
        }

        if (!transparentBackground) {
            if (minBackgroundOpacity < 0F) {
                throw IllegalArgumentException(
                    "Minimum background opacity must be equal or higher than 0(zero)"
                )
            }
            if (maxBackgroundOpacity > 255F) {
                throw IllegalArgumentException(
                    "Maximum background opacity must be equal or lower than 255"
                )
            }
            if (minBackgroundOpacity > maxBackgroundOpacity) {
                throw IllegalArgumentException(
                    "Minimum background opacity must be equal or lower than maximum background opacity"
                )
            }
        }
    }
}