package ir.developer.todolist.datamodel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tab")
data class TabModel(
    @PrimaryKey
    val id: Int,
    val name: String,
    var isSelected: Boolean = false
)
