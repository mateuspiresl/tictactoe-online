package me.mateuspires.tictactoe.data.persistence

import android.content.Context
import me.mateuspires.tictactoe.data.Repository
import me.mateuspires.tictactoe.data.models.PlayersImages

class PlayersImagesRepository(context: Context): Repository<PlayersImages> {

    companion object {
        const val PREFERENCES_NAME = "players_images"
        const val FIELD_X_URL = "x_url"
        const val FIELD_Y_URL = "y_url"
    }

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private var data: PlayersImages? = null

    override fun load(): PlayersImages {
        return data ?: PlayersImages(
                preferences.getString(FIELD_X_URL, null),
                preferences.getString(FIELD_Y_URL, null)
        )
    }

    override fun save(data: PlayersImages) {
        this.data = data

        preferences.edit()
                .putString(FIELD_X_URL, data.xUrl)
                .putString(FIELD_Y_URL, data.yUrl)
                .apply()
    }
}
