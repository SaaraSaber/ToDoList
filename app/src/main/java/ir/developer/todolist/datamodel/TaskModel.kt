package ir.developer.todolist.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskModel(
    @PrimaryKey
    val id: Int,
    val task: String,
    val category: String,
    val isDoneTask: Boolean
)