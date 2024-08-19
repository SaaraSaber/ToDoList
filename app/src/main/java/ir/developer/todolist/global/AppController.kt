package ir.developer.todolist.global

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.datamodel.CompletedTaskModel
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.sharedPref.SharedPreferencesGame


class AppController : Application() {
    private lateinit var dataBase: AppDataBase

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        if (!checkEnterToAppForFirst()) {
            saveEnterToAppForFirst()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "alarm_id",
            "alarm_name",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

    }

    private lateinit var sharedPreferencesGame: SharedPreferencesGame

    private fun saveEnterToAppForFirst() {
        sharedPreferencesGame = SharedPreferencesGame(this)
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