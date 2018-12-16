package me.mateuspires.tictactoe.data.models

import com.google.gson.annotations.SerializedName

object ImageSearch {

    @Suppress("ArrayInDataClass")
    data class Result(
            @SerializedName("value")
            val items: Array<Item>
    )

    data class Item(
            @SerializedName("thumbnail")
            val url: String,

            @SerializedName("thumbnailHeight")
            val height: Int,

            @SerializedName("thumbnailWidth")
            val width: Int
    ) {
        var selected: Boolean = false
    }
}
