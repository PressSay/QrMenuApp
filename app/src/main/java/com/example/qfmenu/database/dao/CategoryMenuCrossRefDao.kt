package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.qfmenu.database.entity.CategoryMenuCrossRef

@Dao
interface CategoryMenuCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(categoryMenuCrossRef: CategoryMenuCrossRef)

    @Update
    suspend fun update(categoryMenuCrossRef: CategoryMenuCrossRef)

    @Delete
    suspend fun delete(categoryMenuCrossRef: CategoryMenuCrossRef)

}