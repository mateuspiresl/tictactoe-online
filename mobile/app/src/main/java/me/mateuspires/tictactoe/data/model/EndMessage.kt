package me.mateuspires.tictactoe.data.model

import com.google.gson.annotations.SerializedName

@Suppress("ArrayInDataClass")
data class EndMessage(
        @SerializedName("board")
        val board: Array<Array<Int?>>,

        @SerializedName("winner")
        val winner: Int?
)
