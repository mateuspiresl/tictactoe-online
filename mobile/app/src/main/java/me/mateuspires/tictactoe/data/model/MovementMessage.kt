package me.mateuspires.tictactoe.data.model

import com.google.gson.annotations.SerializedName

data class MovementMessage(
        @SerializedName("line")
        val line: Int,

        @SerializedName("column")
        val column: Int
)
