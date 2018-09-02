package me.mateuspires.tictactoe.ui.main.presenter

enum class BoardCell {
    EMPTY,
    SELF,
    OPPONENT,
    SELF_PATTERN,
    OPPONENT_PATTERN;

    companion object {
        private val ordinals = values()

        fun parse(selfId: Int, cell: Int?): BoardCell {
            return when (cell) {
                null -> EMPTY
                selfId -> SELF
                else -> OPPONENT
            }
        }

        fun fromOrdinal(ordinal: Int): BoardCell {
            return ordinals[ordinal]
        }
    }
}