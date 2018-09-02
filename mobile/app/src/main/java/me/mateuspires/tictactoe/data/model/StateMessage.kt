package me.mateuspires.tictactoe.data.model

import com.google.gson.annotations.SerializedName

@Suppress("ArrayInDataClass")
data class StateMessage(
        @SerializedName("board")
        val board: Array<Array<Int?>>,

        @SerializedName("turn")
        val turn: Int
)
