package me.mateuspires.tictactoe.data

import android.content.SharedPreferences
import com.google.gson.Gson

open class SharedPreferencesRepository<T>(
        private val database: String,
        private val instance: SharedPreferences,
        private val classType: Class<T>
): Repository<T> {

    override fun load(): T {
        return Gson().fromJson(instance.getString(database, "{}"), classType)
    }

    override fun save(data: T) {
        instance.edit().putString(database, Gson().toJson(data, classType)).apply()
    }
}
