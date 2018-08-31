package me.mateuspires.tictactoe.ui.main.view

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.board_cell.view.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.ui.main.presenter.BoardCell

class BoardAdapter(
        private val context: Context,
        private val listener: OnCellClickListener
) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    private var board: Array<BoardCell> = Array(0) { BoardCell.EMPTY }

    fun update(newBoard: Array<BoardCell>) {
        board = newBoard
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.board_cell, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return board.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (board[position] == BoardCell.EMPTY) {
            holder.content.visibility = View.GONE
            holder.container.isClickable = true
            holder.container.isFocusable = true
            holder.container.setOnClickListener {
                listener.onCellClick(position)
            }
            holder.container.setBackgroundColor(Color.WHITE)
        } else {
            holder.content.visibility = View.VISIBLE
            holder.container.isClickable = false
            holder.container.isFocusable = false
            holder.container.setOnClickListener(null)

            if (board[position] == BoardCell.SELF || board[position] == BoardCell.SELF_PATTERN) {
                holder.content.setImageResource(R.drawable.ic_cross_gray_24dp)

                if (board[position] == BoardCell.SELF_PATTERN) {
                    holder.content.setColorFilter(Color.WHITE,
                            android.graphics.PorterDuff.Mode.SRC_IN)
                    holder.container.setBackgroundColor(Color.parseColor("#005EFF"))
                }
            } else {
                holder.content.setImageResource(R.drawable.ic_circle_gray_24dp)

                if (board[position] == BoardCell.OPPONENT_PATTERN) {
                    holder.content.setColorFilter(Color.WHITE,
                            android.graphics.PorterDuff.Mode.SRC_IN)
                    holder.container.setBackgroundColor(Color.parseColor("#FF3700"))
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: BoardCellView = view as BoardCellView
        val content: ImageView = view.iv_content
    }

    interface OnCellClickListener {
        fun onCellClick(position: Int)
    }
}
