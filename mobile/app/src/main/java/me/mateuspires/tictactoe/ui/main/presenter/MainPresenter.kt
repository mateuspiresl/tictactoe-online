package me.mateuspires.tictactoe.ui.main.presenter

import me.mateuspires.tictactoe.data.model.IntroMessage
import me.mateuspires.tictactoe.data.model.MovementMessage
import me.mateuspires.tictactoe.eventservice.EventService
import me.mateuspires.tictactoe.ui.main.MainContract

class MainPresenter(
        private val view: MainContract.View,
        private val service: EventService
) : MainContract.Presenter {

    companion object {
        private val WINNING_PATTERNS: Array<IntArray> = arrayOf(
            intArrayOf(1, 1, 1, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 1, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 1, 1, 1, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 1, 1, 1),
            intArrayOf(1, 0, 0, 1, 0, 0, 1, 0, 0),
            intArrayOf(0, 1, 0, 0, 1, 0, 0, 1, 0),
            intArrayOf(0, 0, 1, 0, 0, 1, 0, 0, 1),
            intArrayOf(1, 0, 0, 0, 1, 0, 0, 0, 1),
            intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 0)
        )
    }

    private var playing: Boolean = false
    private var loading: Boolean = false
    private var connected: Boolean = false
    private var waitingForResponse = false
    private var online: Boolean = false
    private var self: Player = Player(0, "Player 1")
    private var opponent: Player = Player(1, "Player 2")
    private var selfTurn: Boolean = true
    private var board: Array<BoardCell>? = null

    init {
        service.getConnectionObservable().subscribe { connected ->
            if (connected != null) {
                if (connected) {
                    service.sendIntro(IntroMessage(self.name))
                } else {
                    setDisconnected()
                }

                this.connected = connected
            }
        }

        service.getWaitingObservable().subscribe { waiting ->
            if (waiting == true) {
                view.showWaitingForOpponent()
            }
        }

        service.getStartObservable().subscribe { message ->
            if (message != null) {
                waitingForResponse = false
                playing = true
                loading = false
                self.id = message.id
                opponent = Player(if (message.id == 0) 1 else 0, message.opponent)
                selfTurn = message.turn == self.id

                view.setOpponentName(opponent.name)
                view.startGame(selfTurn)
            }
        }

        service.getStateObservable().subscribe { message ->
            if (message != null) {
                waitingForResponse = false
                updateState(message.board, message.turn)
            }
        }

        service.getEndObservable().subscribe { message ->
            if (message != null) {
                waitingForResponse = false

                val parsedBoard = parseBoard(message.board)
                val cell = if (message.winner == self.id) BoardCell.SELF else BoardCell.OPPONENT
                val winningPattern = matchWinningPattern(cell, parsedBoard)

                if (winningPattern != null) {
                    markWinningCells(parsedBoard, winningPattern)
                }

                playing = false
                board = parsedBoard

                view.updateBoard(parsedBoard)
                view.setTurn(false)

                when {
                    message.winner == null -> view.showTie()
                    message.winner == self.id -> view.showWinner(true)
                    else -> view.showWinner(false)
                }

                connected = false
                service.disconnect()
            }
        }

        service.getCloseObservable().subscribe { message ->
            if (message != null) {
                setDisconnected()
            }
        }
    }

    override fun setName(name: String) {
        self.name = name
    }

    /**
     * Starts a new game.
     * @param online If true, it will start an online game, otherwise it will start a local game.
     */
    override fun startNewGame(online: Boolean) {
        this.online = online

        if (online) {
            if (!loading) {
                playing = false
                loading = true
                connected = false

                view.showConnecting()

                service.disconnect()
                service.connect()
            }
        } else {
            playing = true
            loading = false
            self.id = 0
            opponent = Player(1, "Player 2")
            selfTurn = true
            board = arrayOf(
                    BoardCell.EMPTY, BoardCell.EMPTY, BoardCell.EMPTY,
                    BoardCell.EMPTY, BoardCell.EMPTY, BoardCell.EMPTY,
                    BoardCell.EMPTY, BoardCell.EMPTY, BoardCell.EMPTY)

            view.setOpponentName(opponent.name)
            view.startGame(true)
        }
    }

    /**
     * Makes a movement.
     * @param position The position to mark.
     */
    override fun move(position: Int) {
        if (playing && !loading) {
            if (online) {
                if (selfTurn && !waitingForResponse) {
                    waitingForResponse = true
                    service.sendMovement(MovementMessage(position / 3, position % 3))
                }
            } else {
                board = board?.let {
                    val board = it.copyOf()
                    val cell = if (selfTurn) BoardCell.SELF else BoardCell.OPPONENT

                    board[position] = cell

                    val winningPattern: IntArray? = matchWinningPattern(cell, board)

                    when {
                        winningPattern != null -> {
                            markWinningCells(board, winningPattern)

                            playing = false
                            view.updateBoard(board)
                            view.showWinner(selfTurn)
                        }

                        board.any { c -> c == BoardCell.EMPTY } -> {
                            selfTurn = !selfTurn
                            view.updateBoard(board)
                            view.setTurn(selfTurn)
                        }

                        else -> {
                            playing = false
                            view.updateBoard(board)
                            view.showTie()
                        }
                    }

                    board
                }
            }
        }
    }

    override fun disconnect() {
        connected = false
        service.disconnect()
        setDisconnected(false)
    }

    override fun destroy() {
        disconnect()
    }

    private fun setDisconnected(possibleFail: Boolean = true) {
        waitingForResponse = false

        if (loading || playing) {
            playing = false
            loading = false

            view.notifyDisconnection(possibleFail)
        } else if (connected) {
            view.notifyDisconnection(false)
        }
    }

    /**
     * Update the board and turn.
     * @param rawBoard The raw board as it comes from the server.
     * @param turn The player id in the turn.
     */
    private fun updateState(rawBoard: Array<Array<Int?>>, turn: Int) {
        board = parseBoard(rawBoard)
        selfTurn = turn == self.id

        view.updateBoard(board!!)
        view.setTurn(selfTurn)
    }

    /**
     * Parse the board from array of array of integers (Array<Array<Int?>>), which is the server
     * representation, to Array<BoardCell>.
     * @return The board as an array of BoardCell.
     */
    private fun parseBoard(board: Array<Array<Int?>>): Array<BoardCell> {
        return Array(9) { index -> BoardCell.parse(self.id, board[index / 3][index % 3]) }
    }

    /**
     * Matches the current board state to a winning pattern.
     * @param self The player to consider.
     * @param board The board.
     * @return The pattern if there was a match, otherwise null.
     */
    private fun matchWinningPattern(self: BoardCell, board: Array<BoardCell>): IntArray? {
        for (pattern in WINNING_PATTERNS) {
            var succeeded = true

            for (i in pattern.indices) {
                if (pattern[i] == 1) {
                    if (board[i] != self) {
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

    /**
     * Marks the winning cells of the board based on a winning pattern.
     * @param board The board.
     * @param pattern The winning pattern.
     */
    private fun markWinningCells(board: Array<BoardCell>, pattern: IntArray) {
        board.forEachIndexed { index, _ ->
            if (pattern[index] == 1) {
                board[index] = if (selfTurn) BoardCell.SELF_PATTERN else BoardCell.OPPONENT_PATTERN
            }
        }
    }
}