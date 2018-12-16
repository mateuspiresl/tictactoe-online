package me.mateuspires.tictactoe.data.models

import com.google.gson.annotations.SerializedName

data class PlayersImages(
        @SerializedName("x_url")
        val xUrl: String?,

        @SerializedName("y_url")
        val yUrl: String?
)