package me.mateuspires.tictactoe.game

class Game {
    companion object {
        // Winning patterns where the center is not included
        private val BORDER_WINNING_PATTERNS = arrayOf(
                intArrayOf(1, 1, 1, 0, 0, 0, 0, 0, 0), // Horizontal top
                intArrayOf(0, 0, 0, 0, 0, 0, 1, 1, 1), // Horizontal bottom
                intArrayOf(1, 0, 0, 1, 0, 0, 1, 0, 0), // Vertical left
                intArrayOf(0, 0, 1, 0, 0, 1, 0, 0, 1)  // Vertical right
        )

        // Winning patterns that has the center included
        private val CENTER_WINNING_PATTERNS = arrayOf(
                intArrayOf(0, 0, 0, 1, 1, 1, 0, 0, 0), // Horizontal middle
                intArrayOf(0, 1, 0, 0, 1, 0, 0, 1, 0), // Vertical middle
                intArrayOf(1, 0, 0, 0, 1, 0, 0, 0, 1), // Main diagonal
                intArrayOf(0, 0, 1, 0, 1, 0, 1, 0, 0)  // Counter diagonal
        )
    }

    private var board: Array<BoardCell> = Array(9) { BoardCell.EMPTY }
    private var xTurn: Boolean = true
    private var movementCount: Int = 0

    /**
     * Returns true if the current turn is of X.
     * @return True if the current turn is of X.
     */
    fun isXTurn(): Boolean {
        return xTurn
    }

    /**
     * Returns true if the board is full.
     * @return True if the board is full.
     */
    fun isBoardFull(): Boolean {
        return movementCount == 9
    }

    /**
     * Returns the board.
     * @return The board.
     */
    fun getBoard(): Array<BoardCell> {
        return board
    }

    /**
     * Clears the board and set the turn to X.
     */
    fun clear() {
        board = Array(9) { BoardCell.EMPTY }
        xTurn = true
        movementCount = 0
    }

    /**
     * Makes a movement from the current player and checks if there is a winner.
     * @return True if the movement led to a victory.
     */
    fun move(position: Int): Boolean {
        if (board[position] == BoardCell.EMPTY) {
            board[position] = if (xTurn) BoardCell.X else BoardCell.O
            movementCount += 1

            return if (matchesWinningPattern(position)) true else {
                xTurn = !xTurn
                false
            }
        }

        return false
    }

    /**
     * Matches the current board state to a winning pattern.
     * @param lastMovement The last movement position.
     * @return The pattern if there was a match, otherwise null.
     */
    private fun matchesWinningPattern(lastMovement: Int): Boolean {
        return when {
        // Can not have a winning pattern if there is not at least 5 marked cells
            movementCount < 5 -> false

        // If the center is not marked by the player, check only the patterns where the
        // center is not included
            board[4] != board[lastMovement] -> matchesWinningPattern(lastMovement,
                    *BORDER_WINNING_PATTERNS)

        // If there is no more than 3 marks for the player, check only the patterns where the
        // center is included
            movementCount < 7 -> matchesWinningPattern(lastMovement, *CENTER_WINNING_PATTERNS)

        // Otherwise check all the patterns
            else -> matchesWinningPattern(lastMovement, *CENTER_WINNING_PATTERNS,
                    *BORDER_WINNING_PATTERNS)
        }
    }

    /**
     * Matches the current board state to a winning pattern in a specific list of patterns.
     * @param position The last movement position.
     * @param patterns The patterns to check.
     * @return The pattern if there was a match, otherwise null.
     */
    private fun matchesWinningPattern(position: Int, vararg patterns: IntArray): Boolean {
        return patterns.any { pattern ->
            // Ignores the patterns that does not contain the last movement position
            // This is considered because if a winning pattern has been made, the last movement
            // position is certainly within it
            pattern[position] == 1
                    // For every cell, it isn't marked or the is marked by the player
                    && pattern.indices.all { pattern[it] != 1 || board[it] == board[position] }
        }
    }
}