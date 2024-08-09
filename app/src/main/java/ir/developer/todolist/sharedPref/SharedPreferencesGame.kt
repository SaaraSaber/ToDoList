package ir.developer.todolist.sharedPref

import android.content.Context
import android.content.SharedPreferences
import ir.developer.todolist.global.Utils


class SharedPreferencesGame(private val context: Context) {
    private lateinit var sharedPreferences: SharedPreferences

    fun saveStatusFirst(status: Boolean) {
        sharedPreferences = context.getSharedPreferences(
            "SaveStatusFirst",
            Context.MODE_PRIVATE
        )
        val edSharedPreferences = sharedPreferences.edit()
        edSharedPreferences.putBoolean(Utils.STATUS_LOGIN, status)
        edSharedPreferences.apply()
    }

    fun readStatusFirst(): Boolean {
        sharedPreferences = context.getSharedPreferences(
            "SaveStatusFirst",
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(Utils.STATUS_LOGIN, false)
    }
}