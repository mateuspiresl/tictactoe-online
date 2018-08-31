package me.mateuspires.tictactoe.data.model

import com.google.gson.annotations.SerializedName

data class CloseMessage(
        @SerializedName("id")
        val id: Int
)