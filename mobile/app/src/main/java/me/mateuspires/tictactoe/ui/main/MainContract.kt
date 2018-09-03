package me.mateuspires.tictactoe.ui.main

import me.mateuspires.tictactoe.ui.main.presenter.BoardCell

interface MainContract {

    interface View {

        /**
         * Notifies the player is connecting to the server.
         */
        fun showConnecting()

        /**
         * Notifies the player is waiting for an opponent.
         */
        fun showWaitingForOpponent()

        /**
         * Set the opponent's name.
         */
        fun setOpponentName(name: String)

        /**
         * Notifies the game started.
         */
        fun startGame(selfTurn: Boolean)

        /**
         * Notifies the current state of the board.
         * @param board The board array.
         */
        fun updateBoard(board: Array<BoardCell>)

        /**
         * Notifies if the player is in turn.
         * @param selfTurn If true, it's the player's turn.
         */
        fun setTurn(selfTurn: Boolean)

        /**
         * Notifies the winner of the game.
         * @param self If true, the player won the game, otherwise the opponent did.
         */
        fun showWinner(self: Boolean)

        /**
         * Notifies the game ended with a tie.
         */
        fun showTie()

        /**
         * Notifies the game ended caused by a disconnection from one of the players.
         * @param possibleFail If true, the disconnection may be caused by a fail.
         */
        fun notifyDisconnection(possibleFail: Boolean)
    }

    interface Presenter {

        /**
         * Sets the name of the player for online games.
         * @param name The name.
         */
        fun setName(name: String)

        /**
         * Starts a new game.
         * @param online If true, a multiplayer game will be created, otherwise a local game will
         *      be.
         */
        fun startNewGame(online: Boolean)

        /**
         * Makes a move to the position of the board.
         * @param position The position to move to.
         */
        fun move(position: Int)

        /**
         * Cancels the connection after a start new online game.
         */
        fun disconnect()

        /**
         * Destroys the presenter.
         */
        fun destroy()
    }
}