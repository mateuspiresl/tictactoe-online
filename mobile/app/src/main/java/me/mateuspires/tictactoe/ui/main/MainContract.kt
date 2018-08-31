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
         * Clears the board.
         */
        fun clearBoard()

        /**
         * Notifies if the player is in turn and the current state of the board.
         * @param selfTurn If true, it's the player's turn.
         * @param board The board array.
         */
        fun updateState(selfTurn: Boolean, board: Array<BoardCell>)

        /**
         * Notifies the winner of the game.
         * @param self If true, the player won the game, otherwise the opponent did.
         */
        fun showWinner(self: Boolean)

        /**
         * Notifies the game ended caused by a disconnection from one of the players.
         */
        fun showDisconnectedWarning()
    }

    interface Presenter {
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
    }
}