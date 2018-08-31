package me.mateuspires.tictactoe.data.model

import com.google.gson.annotations.SerializedName

@Suppress("ArrayInDataClass")
data class StateMessage(
        @SerializedName("board")
        val boardAsArrays: Array<Array<Int?>>,

        @SerializedName("turn")
        val turn: Int
) {
    fun getBoard(): Array<Int?> {
        return arrayOf(*boardAsArrays[0], *boardAsArrays[1], *boardAsArrays[2])
    }
}