package me.mateuspires.tictactoe.ui.main.view

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import me.mateuspires.tictactoe.util.startAnimation

class BoardCellViewHolder(
        val view: CardView,
        context: Context,
        animIn: Int,
        animOut: Int
) : RecyclerView.ViewHolder(view) {

    private val animationIn = AnimationUtils.loadAnimation(context, animIn)
    private val animationOut = AnimationUtils.loadAnimation(context, animOut)

    fun animateIn(onStart: (animation: Animation) -> Unit = {},
                  onEnd: (animation: Animation) -> Unit = {}) {
        view.startAnimation(animationIn, onStart, onEnd)
    }

    fun animateOut(onStart: (animation: Animation) -> Unit = {},
                   onEnd: (animation: Animation) -> Unit = {}) {
        view.startAnimation(animationOut, onStart, onEnd)
    }
}