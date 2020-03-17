package com.deniz.draggablelibrary.utils

/**
 * Created by deniz on 12.03.2020
 */

fun Double.isPositive(): Boolean {
    return this > 0.0
}

fun Double.isEven(): Boolean {
    return this % 2.0 == 0.0
}

fun Double.isOdd(): Boolean {
    return this % 2.0 == 1.0
}