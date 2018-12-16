package me.mateuspires.tictactoe.ui.main

import me.mateuspires.tictactoe.data.models.PlayersImages
import me.mateuspires.tictactoe.game.BoardCell
import me.mateuspires.tictactoe.game.Status

interface MainContract {

    interface Presenter {

        /**
         * Returns the players images.
         * @return The players images.
         */
        fun getPlayersImages(): PlayersImages

        /**
         * Starts a new game.
         */
        fun startNewGame()

        /**
         * Makes a move to the position of the board.
         * @param position The position to move to.
         * @return True if the movement will be handled, otherwise false.
         */
        fun move(position: Int): Boolean

        /**
         * Returns the playing state.
         * @return True if there is a game been played, otherwise false.
         */
        fun isPlaying(): Boolean
    }

    interface View {

        /**
         * Notifies which player is in turn and the current state of the board.
         * @param xTurn If true, it's the X player's turn.
         * @param board The board state.
         */
        fun onUpdate(xTurn: Boolean, board: Array<BoardCell>)

        /**
         * Notifies the winner of the game and the current state of the board.
         * @param xPlayer If true, the X player won the game, otherwise the O did.
         * @param board The board state.
         */
        fun onWin(xPlayer: Boolean, board: Array<BoardCell>)

        /**
         * Notifies the game ended with a tie and the current state of the board.
         * @param board The board state.
         */
        fun onTie(board: Array<BoardCell>)
    }

    interface BoardView {

        /**
         * Receives the status and the board state.
         * @param status The new status.
         * @board board The updated board.
         */
        fun update(status: Status, board: Array<BoardCell>)
    }
}