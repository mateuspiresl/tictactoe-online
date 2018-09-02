package me.mateuspires.tictactoe.util

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

internal fun View.loadAnimation(anim: Int, onEnd: () -> Unit = {}) {
    this.loadAnimation(anim, null, onEnd)
}

internal fun View.loadAnimation(anim: Int, interpolator: Int? = null, onEnd: () -> Unit = {}) {
    this.startAnimation(AnimationUtils.loadAnimation(this.context, anim).let {
        if (interpolator != null) it.setInterpolator(this.context, interpolator)
        it.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) { onEnd() }
        })
        it
    })
}