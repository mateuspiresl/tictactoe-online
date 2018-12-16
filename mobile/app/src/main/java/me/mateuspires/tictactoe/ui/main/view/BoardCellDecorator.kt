package me.mateuspires.tictactoe.ui.main.view

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class BoardCellDecorator(private val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view) - 1

        if (position >= 0) {
            val column = position % 3

            when (column) {
                0 -> {
                    outRect.left = space
                    outRect.right = space / 2
                }
                1 -> {
                    outRect.left = space / 2
                    outRect.right = space / 2
                }
                2 -> {
                    outRect.left = space / 2
                    outRect.right = space
                }
            }

            outRect.bottom = space
        }
    }
}
