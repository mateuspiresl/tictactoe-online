package me.mateuspires.tictactoe.data.model

import com.google.gson.annotations.SerializedName

data class IntroMessage(
        @SerializedName("name")
        val name: String
)
