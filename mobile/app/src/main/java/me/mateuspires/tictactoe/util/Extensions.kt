package me.mateuspires.tictactoe.util

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

internal fun View.loadAnimation(anim: Int, onStart: (animation: Animation) -> Unit = {},
                                onEnd: (animation: Animation) -> Unit = {}) {
    this.loadAnimation(anim, null, onStart, onEnd)
}

internal fun View.loadAnimation(anim: Int, interpolator: Int? = null,
                                onStart: (animation: Animation) -> Unit = {},
                                onEnd: (animation: Animation) -> Unit = {}) {
    AnimationUtils.loadAnimation(this.context, anim)?.let {
        if (interpolator != null) it.setInterpolator(this.context, interpolator)
        startAnimation(it, onStart, onEnd)
    }
}

internal fun View.startAnimation(animation: Animation,
                                onStart: (animation: Animation) -> Unit,
                                onEnd: (animation: Animation) -> Unit = {}) {
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) { animation?.let { onStart(it) } }
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            animation?.let {
                onEnd(it)
                animation.setAnimationListener(null)
            }
        }
    })

    startAnimation(animation)
}
