package me.mateuspires.tictactoe.ui.main.view

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_board_cell.view.*
import kotlinx.android.synthetic.main.layout_game_status.view.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.data.models.PlayersImages
import me.mateuspires.tictactoe.game.BoardCell
import me.mateuspires.tictactoe.game.Status
import me.mateuspires.tictactoe.ui.main.MainContract
import me.mateuspires.tictactoe.util.loadAnimation

class BoardAdapter(
        private val context: Context,
        private val playersImages: PlayersImages,
        private val listener: OnCellClickListener
) : RecyclerView.Adapter<BoardCellViewHolder>(), MainContract.BoardView {

    private val board: Array<BoardCell> = Array(9) { BoardCell.EMPTY }
    private var status: Status = Status.X_TURN
    private var statusHolder: BoardCellViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardCellViewHolder {
        // Status view
        return if (viewType == -1) {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_game_status,
                    parent, false) as CardView
            BoardCellViewHolder(view, context, R.anim.scale_in, R.anim.scale_out)
        // Bard cell view
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_board_cell,
                    parent, false)
            val container = view as BoardCellView
            val content = view.iv_content
            val cell = BoardCell.fromOrdinal(viewType)

            if (cell != BoardCell.EMPTY) {
                container.isClickable = false
                container.isFocusable = false

                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (cell) {
                    BoardCell.X ->
                        loadPlayerImage(content, playersImages.xUrl, R.drawable.ic_cross_gray_24dp)

                    BoardCell.O ->
                        loadPlayerImage(content, playersImages.yUrl, R.drawable.ic_circle_gray_24dp)
                }
            }

            BoardCellViewHolder(view, context, R.anim.scale_in, R.anim.scale_out)
        }
    }

    override fun getItemCount(): Int {
        return board.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) -1 else board[position - 1].ordinal
    }

    override fun onBindViewHolder(holder: BoardCellViewHolder, position: Int) {
        if (position == 0) {
            // Is it the best way to trigger an event on the status view when in the context of a
            // board cell click event?
            statusHolder = holder

            holder.view.setBackgroundColor(context.getColor(status.color))
            holder.view.tv_content.text = status.text
            holder.view.loadAnimation(R.anim.slide_up_show)
        } else {
            if (board[position - 1] == BoardCell.EMPTY) {
                holder.view.setOnClickListener { onCellClick(holder, position) }
            } else {
                holder.view.setOnClickListener(null)
            }

            holder.view.loadAnimation(R.anim.scale_in)
        }
    }

    /**
     * Updated the status and the board cells.
     * Animations are triggered only when there is real change.
     */
    override fun update(status: Status, board: Array<BoardCell>) {
        if (this.status != status) {
            this.status = status
            notifyItemChanged(0)
        }

        for (index in this.board.indices) {
            if (this.board[index] != board[index]) {
                this.board[index] = board[index]
                notifyItemChanged(index + 1)
            }
        }
    }

    private fun loadPlayerImage(view: ImageView, url: String?, drawable: Int) {
        if (url == null) {
            view.setImageResource(drawable)
        } else {
            Glide.with(context).load(url).apply(RequestOptions().centerCrop()).into(view)
        }
    }

    /**
     * Checks if the listener implementer allows clicks and, if does, animate and call the click
     * event.
     * @param holder The holder of the cell.
     * @param position The position.
     */
    private fun onCellClick(holder: BoardCellViewHolder, position: Int) {
        if (listener.cellClickIsAllowed()) {
            statusHolder?.view?.loadAnimation(R.anim.slide_up_hide)

            holder.view.loadAnimation(R.anim.scale_out, {}, {
                if (!listener.onCellClick(position - 1)) {
                    holder.view.loadAnimation(R.anim.scale_in)
                }
            })
        }
    }

    interface OnCellClickListener {

        /**
         * Checks if the cell clicks should be handled.
         * @return If true, the {@link #onCellClick} will be called.
         */
        fun cellClickIsAllowed(): Boolean

        /**
         * Notifies a cell click.
         * @return If false, the cell will be animated back to its position.
         */
        fun onCellClick(position: Int): Boolean
    }
}
