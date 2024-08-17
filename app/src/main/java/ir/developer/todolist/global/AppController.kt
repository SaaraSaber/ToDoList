package ir.developer.todolist.global

import android.app.Application
import android.content.Context
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import ir.developer.todolist.database.AppDataBase
import ir.developer.todolist.datamodel.CompletedTaskModel
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.sharedPref.SharedPreferencesGame


class AppController : Application() {
    private lateinit var dataBase: AppDataBase

    override fun onCreate() {
        super.onCreate()

        //font
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/yakanbakhmedium.ttf")
                            .build()
                    )
                )
                .build()
        )

        if (!checkEnterToAppForFirst()) {
            saveEnterToAppForFirst()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
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