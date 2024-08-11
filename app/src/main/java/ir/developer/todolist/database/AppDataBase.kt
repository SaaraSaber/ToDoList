package ir.developer.todolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ir.developer.todolist.database.dao.TabDao
import ir.developer.todolist.database.dao.TaskDao
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.datamodel.TaskModel

@Database(
    entities = [TabModel::class, TaskModel::class],
    version = 3,
    exportSchema = true
)

abstract class AppDataBase : RoomDatabase() {
    abstract fun tab(): TabDao
    abstract fun task(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): AppDataBase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "game_database"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}