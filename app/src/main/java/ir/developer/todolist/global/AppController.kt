package ir.developer.todolist.global

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.datamodel.CompletedTaskModel
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.sharedPref.SharedPreferencesGame


class AppController : Application() {
    private lateinit var dataBase: AppDataBase
    private lateinit var sharedPreferencesGame: SharedPreferencesGame


    override fun onCreate() {
        super.onCreate()

        sharedPreferencesGame = SharedPreferencesGame(this)

        setTheme()

        if (!checkEnterToAppForFirst()) {
            saveEnterToAppForFirst()
        }
    }

    private fun setTheme() {
        val night = sharedPreferencesGame.readStateTheme()
        if (night) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun saveEnterToAppForFirst() {
        sharedPreferencesGame.saveStatusFirst(true)
        dataBase = AppDataBase.getDatabase(this)
        insertDataToDbTab()
        insertDataToDbCompletedTask()
    }

    private fun insertDataToDbCompletedTask() {
//        dataBase = AppDataBase.getDatabase(this)
        dataBase.completedTask()
            .insertTask(CompletedTaskModel(id = 1, allTask = 0, completedTask = 0, notDoneTask = 0))
    }

    private fun checkEnterToAppForFirst(): Boolean {
        sharedPreferencesGame = SharedPreferencesGame(this)
        val result = sharedPreferencesGame.readStatusFirst()
        return result
    }

    private fun insertDataToDbTab() {
//        dataBase = AppDataBase.getDatabase(this)
        dataBase.tab().insertTab(TabModel(id = 1, name = "همه", isSelected = true))
        dataBase.tab().insertTab(TabModel(id = 2, name = "شخصی"))
        dataBase.tab().insertTab(TabModel(id = 3, name = "کار"))
    }
}