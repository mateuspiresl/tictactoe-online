package me.mateuspires.tictactoe.ui.main.view

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import kotlinx.android.synthetic.main.board_cell.view.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.ui.main.presenter.BoardCell


class BoardAdapter(
        private val context: Context,
        private val listener: OnCellClickListener
) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    private val changeSet: MutableSet<Int> = mutableSetOf()
    private var recyclerView: RecyclerView? = null
    private var board: Array<BoardCell> = Array(0) { BoardCell.EMPTY }

    fun clear() {
        board = Array(9) { BoardCell.EMPTY }
        changeSet.clear()

        notifyDataSetChanged()
        recyclerView?.startLayoutAnimation()
    }

    fun update(newBoard: Array<BoardCell>) {
        changeSet.clear()

        for (index in board.indices) {
            if (board[index] != newBoard[index]) {
                changeSet.add(index)
            }
        }

        board = newBoard
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.board_cell, parent, false)
        val container = view as BoardCellView
        val content: ImageView = view.iv_content
        val cell = BoardCell.fromOrdinal(viewType)

        if (cell != BoardCell.EMPTY) {
            container.isClickable = false
            container.isFocusable = false

            if (cell == BoardCell.SELF_PATTERN || cell == BoardCell.OPPONENT_PATTERN) {
                content.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN)
                container.setBackgroundColor(Color.parseColor("#565656"))
            }

            when (cell) {
                BoardCell.SELF, BoardCell.SELF_PATTERN ->
                    content.setImageResource(R.drawable.ic_cross_gray_24dp)

                BoardCell.OPPONENT, BoardCell.OPPONENT_PATTERN ->
                    content.setImageResource(R.drawable.ic_circle_gray_24dp)

                else -> { }
            }
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return board.size
    }

    override fun getItemViewType(position: Int): Int {
        return board[position].ordinal
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (board[position] == BoardCell.EMPTY) {
            holder.view.setOnClickListener { listener.onCellClick(position) }
        } else {
            holder.view.setOnClickListener(null)

            if (changeSet.contains(position)) {
                changeSet.remove(position)
                holder.view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_in))
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface OnCellClickListener {
        fun onCellClick(position: Int)
    }
}
