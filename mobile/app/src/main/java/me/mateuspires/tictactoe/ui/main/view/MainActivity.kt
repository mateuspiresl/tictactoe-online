package me.mateuspires.tictactoe.ui.main.view

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.eventservice.SocketIOEventService
import me.mateuspires.tictactoe.ui.main.MainContract
import me.mateuspires.tictactoe.ui.main.presenter.BoardCell
import me.mateuspires.tictactoe.ui.main.presenter.MainPresenter
import me.mateuspires.tictactoe.util.loadAnimation


class MainActivity : AppCompatActivity(), MainContract.View, View.OnClickListener,
        BoardAdapter.OnCellClickListener {

    private var presenter: MainContract.Presenter? = null
    private var boardAdapter: BoardAdapter? = null
    private var opponentName: String = ""
    private var handler: Handler? = null
    private var infoHash: Int? = null

    companion object {
        private const val TAG = "TTT.MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_menu_local.setOnClickListener(this)
        fab_menu_online.setOnClickListener(this)
        bt_disconnect.setOnClickListener(this)

        presenter = MainPresenter(this, SocketIOEventService())
        boardAdapter = BoardAdapter(this, this)
        handler = Handler()

        rv_board.adapter = boardAdapter
        rv_board.layoutManager = GridLayoutManager(this, 3)

        presenter?.startNewGame(false)
    }

    override fun onDestroy() {
        presenter?.destroy()
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fab_menu_local -> presenter?.startNewGame(false)
            R.id.fab_menu_online -> presenter?.startNewGame(true)
            R.id.bt_disconnect -> presenter?.disconnect()
        }

        fam_menu.collapse()
    }

    override fun onCellClick(position: Int) {
        Log.d(TAG, "Move $position")
        presenter?.move(position)
    }

    override fun showConnecting() {
        // Should be "Connecting to the server..."
        showInfo("Waiting for an opponent...")

        setMenuVisibility(false)
    }

    override fun showWaitingForOpponent() {
        // Ignored because this message is shown on connecting state
        // showInfo("Waiting for an opponent...")
    }

    override fun setOpponentName(name: String) {
        opponentName = name
    }

    override fun startGame(selfTurn: Boolean) {
        Log.d(TAG, "Start game $selfTurn")

        boardAdapter?.clear()
        showInfo(if (selfTurn) "Your turn." else "$opponentName's turn.")
    }

    override fun updateBoard(board: Array<BoardCell>) {
        Log.d(TAG, "Update board")
        boardAdapter?.update(board)
    }

    override fun setTurn(selfTurn: Boolean) {
        Log.d(TAG, "Set turn $selfTurn")
        showInfo(if (selfTurn) "Your turn." else "$opponentName's turn.")
    }

    override fun showWinner(self: Boolean) {
        Log.d(TAG, "Winner $self")

        if (self) {
            showInfo("You won!", getColor(R.color.info))
        } else {
            showInfo("$opponentName won!", getColor(R.color.warning))
        }

        setMenuVisibility(true)
    }

    override fun showTie() {
        Log.d(TAG, "Tie")
        showInfo("Tie!")
        setMenuVisibility(true)
    }

    override fun notifyDisconnection(possibleFail: Boolean) {
        if (possibleFail) {
            showInfo("Game ended due to a disconnection.", getColor(R.color.warning))
        } else {
            showInfo("Connection canceled.", getColor(R.color.status))
        }

        setMenuVisibility(true)
    }

    private fun showInfo(text: String, color: Int = Color.WHITE, fromCallback: Boolean = false) {
        val hash = text.hashCode()

        if (!fromCallback || infoHash == hash) {
            infoHash = hash

            if (ll_header.visibility == View.INVISIBLE) {
                tv_info.text = text
                ll_header.setBackgroundColor(color)
                ll_header.visibility = View.VISIBLE
                ll_header.loadAnimation(R.anim.slide_up_show)
            } else {
                ll_header.loadAnimation(R.anim.slide_up_hide) {
                    if (infoHash == hash) {
                        ll_header.visibility = View.INVISIBLE
                        handler?.postDelayed({ showInfo(text, color, true) }, 60)
                    }
                }
            }
        }
    }

    private fun setMenuVisibility(visible: Boolean) {
        if (visible && ll_menu_container.visibility == View.INVISIBLE) {
            ll_menu_container.loadAnimation(R.anim.slide_right_show) {
                ll_menu_container.visibility = View.VISIBLE
            }

            bt_disconnect.loadAnimation(R.anim.slide_left_hide) {
                bt_disconnect.visibility = View.INVISIBLE
            }
        } else if (!visible && ll_menu_container.visibility == View.VISIBLE) {
            ll_menu_container.loadAnimation(R.anim.slide_right_hide) {
                ll_menu_container.visibility = View.INVISIBLE
            }

            bt_disconnect.visibility = View.VISIBLE
            bt_disconnect.loadAnimation(R.anim.slide_left_show)
        }
    }
}
