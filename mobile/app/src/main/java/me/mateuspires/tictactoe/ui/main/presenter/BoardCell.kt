package me.mateuspires.tictactoe.ui.main.presenter

enum class BoardCell {
    EMPTY,
    SELF,
    OPPONENT,
    SELF_PATTERN,
    OPPONENT_PATTERN;

    companion object {
        fun parse(selfId: Int, cell: Int?): BoardCell {
            return when (cell) {
                null -> EMPTY
                selfId -> SELF
                else -> OPPONENT
            }
        }
    }
}