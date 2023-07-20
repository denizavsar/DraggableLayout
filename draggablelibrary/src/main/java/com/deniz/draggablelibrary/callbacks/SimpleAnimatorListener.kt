package com.deniz.draggablelibrary.callbacks

import android.animation.Animator

/**
 * Created by deniz on 10.03.2020
 */
abstract class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(p0: Animator) {}

    override fun onAnimationCancel(p0: Animator) {}

    override fun onAnimationStart(p0: Animator) {}

    override fun onAnimationEnd(p0: Animator) {}
}