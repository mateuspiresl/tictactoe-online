package me.mateuspires.tictactoe.data.model

import com.google.gson.annotations.SerializedName

@Suppress("ArrayInDataClass")
data class StartMessage(
        @SerializedName("id")
        val id: Int,

        @SerializedName("opponent")
        val opponent: String,

        @SerializedName("board")
        val board: Array<Array<Int?>>,

        @SerializedName("turn")
        val turn: Int
)
