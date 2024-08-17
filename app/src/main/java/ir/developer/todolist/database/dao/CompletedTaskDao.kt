package ir.developer.todolist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.developer.todolist.datamodel.CompletedTaskModel

@Dao
interface CompletedTaskDao {

    @Query("SELECT * FROM completed_task")
    fun readTasks(): CompletedTaskModel

    @Insert
    fun insertTask(taskInsert: CompletedTaskModel): Long

    @Update
    fun updateTask(taskUpdate: CompletedTaskModel): Int

}