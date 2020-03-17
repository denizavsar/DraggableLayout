package com.deniz.draggablelibrary.callbacks

import android.animation.Animator

/**
 * Created by deniz on 10.03.2020
 */
abstract class SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {}

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationStart(animation: Animator?) {}

    override fun onAnimationEnd(animation: Animator?) {}
}