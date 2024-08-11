package ir.developer.todolist.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.developer.todolist.datamodel.TabModel
import ir.developer.todolist.datamodel.TaskModel

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun readTasks(): List<TaskModel>

    @Insert
    fun insertTask(taskInsert: TaskModel): Long

    @Update
    fun updateTask(taskUpdate: TaskModel): Int

    @Delete
    fun deleteTask(taskDelete: TaskModel)
}