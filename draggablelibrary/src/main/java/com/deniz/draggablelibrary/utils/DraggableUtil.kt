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
}