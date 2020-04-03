package com.deniz.draggablelibrary.utils

import android.content.res.Resources

fun Int.toPX(): Float {
    return this * Resources.getSystem().displayMetrics.density
}
