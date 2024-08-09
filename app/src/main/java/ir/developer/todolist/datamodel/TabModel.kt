package ir.developer.todolist.datamodel

import androidx.room.Entity

@Entity(tableName = "tab")
data class TabModel(
    val id: Int,
    val name: String
)
