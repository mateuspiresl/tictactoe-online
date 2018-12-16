package me.mateuspires.tictactoe.game

import me.mateuspires.tictactoe.R

enum class Status(val text: String, val color: Int) {
    DEFAULT("", R.color.gray),
    X_TURN("X's TURN", R.color.gray),
    O_TURN("O's TURN", R.color.gray),
    X_WINS("X WINS!", R.color.info),
    O_WINS("O WINS!", R.color.warning),
    TIED("TIED", R.color.gray)
}
