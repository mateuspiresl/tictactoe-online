package me.mateuspires.tictactoe.ui.main.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.eventservice.SocketIOEventService
import me.mateuspires.tictactoe.ui.main.MainContract
import me.mateuspires.tictactoe.ui.main.presenter.BoardCell
import me.mateuspires.tictactoe.ui.main.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View, View.OnClickListener, BoardAdapter.OnCellClickListener {

    companion object {
        private const val TAG: String = "MainActivity"
    }

    private val presenter: MainContract.Presenter = MainPresenter(this, SocketIOEventService())
    private val boardAdapter: BoardAdapter = BoardAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.bt_new_local_game).setOnClickListener(this)
        findViewById<Button>(R.id.bt_new_online_game).setOnClickListener(this)

        val recyclerView = this.rv_board
        recyclerView.adapter = boardAdapter

        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager

        presenter.startNewGame(false)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.bt_new_local_game -> presenter.startNewGame(false)
            R.id.bt_new_online_game -> presenter.startNewGame(true)
        }
    }

    override fun onCellClick(position: Int) {
        Log.d(TAG, "onCellClick $position")
        presenter.move(position)
    }

    override fun showConnecting() {
        Log.d(TAG, "showConnecting")
    }

    override fun showWaitingForOpponent() {
        Log.d(TAG, "showWaitingForOpponent")
    }

    override fun setOpponentName(name: String) {
        Log.d(TAG, "setOpponentName: $name")
    }

    override fun clearBoard() {
        Log.d(TAG, "clearBoard")
    }

    override fun updateState(selfTurn: Boolean, board: Array<BoardCell>) {
        Log.d(TAG, "updateBoard: $selfTurn $board")
        boardAdapter.update(board)
    }

    override fun showWinner(self: Boolean) {
        Log.d(TAG, "showWinner: $self")
    }

    override fun showDisconnectedWarning() {
        Log.d(TAG, "showDisconnectedWarning")
    }
}
