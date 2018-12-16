package me.mateuspires.tictactoe.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.data.persistence.PlayersImagesRepository
import me.mateuspires.tictactoe.ui.customizer.view.CustomizerActivity
import me.mateuspires.tictactoe.game.BoardCell
import me.mateuspires.tictactoe.ui.main.MainContract
import me.mateuspires.tictactoe.game.Status
import me.mateuspires.tictactoe.ui.main.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View, BoardAdapter.OnCellClickListener {

    companion object {
        private const val TAG = "TTT.MainActivity"
        private const val CUSTOMIZATION_REQUEST = 0
    }

    private var presenter: MainContract.Presenter? = null
    private var boardView: MainContract.BoardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this, PlayersImagesRepository(this))

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.board_cell_spacing)
        rv_board.addItemDecoration(BoardCellDecorator(spacingInPixels))
        rv_board.setHasFixedSize(true)
        rv_board.layoutManager = GridLayoutManager(this, 3).let {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) 3 else 1
                }
            }

            it
        }

        (rv_board.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        loadRecyclerView()

        bt_start.setOnClickListener { presenter?.startNewGame() }
        bt_customize.setOnClickListener {
            startActivityForResult(Intent(this, CustomizerActivity::class.java),
                    CUSTOMIZATION_REQUEST)
        }
    }

    override fun onStart() {
        presenter?.startNewGame()
        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            loadRecyclerView()
        }
    }

    override fun cellClickIsAllowed(): Boolean {
        return presenter?.isPlaying() ?: false
    }

    override fun onCellClick(position: Int): Boolean {
        Log.d(TAG, "Move $position")
        return presenter?.move(position) ?: false
    }

    override fun onUpdate(xTurn: Boolean, board: Array<BoardCell>) {
        Log.d(TAG, "Update $xTurn")
        boardView?.update(if (xTurn) Status.X_TURN else Status.O_TURN, board)
    }

    override fun onWin(xPlayer: Boolean, board: Array<BoardCell>) {
        Log.d(TAG, "Win $xPlayer")
        boardView?.update(if (xPlayer) Status.X_WINS else Status.O_WINS, board)
    }

    override fun onTie(board: Array<BoardCell>) {
        Log.d(TAG, "Tie")
        boardView?.update(Status.TIED, board)
    }

    private fun loadRecyclerView() {
        boardView = presenter?.getPlayersImages()?.let {
            BoardAdapter(this, it, this)
        }

        rv_board.adapter = boardView as BoardAdapter
        rv_board.startLayoutAnimation()

        presenter?.startNewGame()
    }
}
