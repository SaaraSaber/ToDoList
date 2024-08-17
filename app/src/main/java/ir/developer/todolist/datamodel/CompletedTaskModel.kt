package ir.developer.todolist.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_task")
data class CompletedTaskModel(
    @PrimaryKey
    val id: Int,
    val allTask: Int,
    val completedTask: Int,
    val notDoneTask: Int
)
