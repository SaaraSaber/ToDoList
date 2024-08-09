package ir.developer.todolist.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ir.developer.todolist.datamodel.TabModel

@Dao
interface TabDao {

    @Query("SELECT * FROM tab")
    fun readTabs():List<TabModel>

    @Insert
    fun insertTab(tabInsert: TabModel):Long

    @Update
    fun updateTab(tabUpdate: TabModel):Int

    @Delete
    fun deleteTab(tabDelete: TabModel)
}