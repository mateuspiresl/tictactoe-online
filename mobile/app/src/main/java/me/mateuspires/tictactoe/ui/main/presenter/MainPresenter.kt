package me.mateuspires.tictactoe.ui.main.presenter

import android.util.Log
import me.mateuspires.tictactoe.data.model.MovementMessage
import me.mateuspires.tictactoe.eventservice.EventService
import me.mateuspires.tictactoe.ui.main.MainContract

class MainPresenter(
        private val view: MainContract.View,
        private val service: EventService
) : MainContract.Presenter {

    companion object {
        private val WINNING_PATTERNS: Array<String> = arrayOf(
            "111000000",
            "000111000",
            "000000111",
            "100100100",
            "010010010",
            "001001001",
            "100010001",
            "001010100"
        )
    }

    private var playing: Boolean = false
    private var online: Boolean = false
    private var selfId: Int = 0
    private var opponentId: Int = 1
    private var selfTurn: Boolean = true
    private var board: Array<BoardCell>? = null

    init {
        service.getConnectionObservable().subscribe { connected ->
            if (!connected) {
                playing = false
                view.showDisconnectedWarning()
            }
        }

        service.getWaitingObservable().subscribe { waiting ->
            if (waiting) {
                view.showWaitingForOpponent()
            }
        }

        service.getStartObservable().subscribe { message ->
            if (message != null) {
                selfId = message.id
                view.setOpponentName(message.opponent)
                view.updateState(message.turn == selfId, parseBoard(message.getBoard()))
            }
        }

        service.getStateObservable().subscribe { message ->
            if (message != null) {
                view.updateState(message.turn == selfId, parseBoard(message.getBoard()))
            }
        }

        service.getWinnerObservable().subscribe { message ->
            if (message != null) {
                playing = false
                view.showWinner(message.id == selfId)
            }
        }

        service.getCloseObservable().subscribe { message ->
            if (message != null) {
                playing = false
                view.showDisconnectedWarning()
            }
        }
    }

    /**
     * Starts a new game.
     * @param online If true, it will start an online game.
     */
    override fun startNewGame(online: Boolean) {
        playing = true
        this.online = online

        board = arrayOf(
                BoardCell.EMPTY, BoardCell.EMPTY, BoardCell.EMPTY,
                BoardCell.EMPTY, BoardCell.EMPTY, BoardCell.EMPTY,
                BoardCell.EMPTY, BoardCell.EMPTY, BoardCell.EMPTY)

        if (online) {
            if (playing) service.disconnect()
            service.connect()
            view.showConnecting()
        } else {
            opponentId = 1
            selfId = 0
            selfTurn = true
            view.updateState(true, board!!)
        }
    }

    /**
     * Makes a movement.
     */
    override fun move(position: Int) {
        if (playing) {
            if (online) {
                service.sendMovement(MovementMessage(position / 3, position % 3))
            } else {
                val cellType = if (selfTurn) BoardCell.SELF else BoardCell.OPPONENT
                board!![position] = cellType

                val winningPattern: String? = matchWinningPattern(cellType)
                Log.d("===== win patt", "$winningPattern")

                if (winningPattern != null) {
                    val winningBoard: List<BoardCell> = board!!.mapIndexed { index, cell ->
                        if (winningPattern[index] == '1') {
                            if (selfTurn) BoardCell.SELF_PATTERN
                            else BoardCell.OPPONENT_PATTERN
                        } else {
                            cell
                        }
                    }

                    playing = false
                    board = winningBoard.toTypedArray()
                    view.updateState(selfTurn, board!!)
                    view.showWinner(selfTurn)
                } else {
                    selfTurn = !selfTurn
                    view.updateState(selfTurn, board!!)
                }

            }
        }
    }

    private fun parseBoard(board: Array<Int?>): Array<BoardCell> {
        return (board.map { cell -> BoardCell.parse(selfId, cell) }).toTypedArray()
    }

    private fun matchWinningPattern(self: BoardCell): String? {
        for (pattern in WINNING_PATTERNS) {
            var succeeded = true

            for (i in pattern.indices) {
                if (pattern[i] == '1') {
                    if (board!![i] != self) {
                        succeeded = false
                        break
                    }
                }
            }

            if (succeeded) {
                return pattern
            }
        }

        return null
    }
}