package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.CategoryWidthDishes
import com.example.qfmenu.database.entity.CategoryWithMenus
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM CategoryDb WHERE categoryId = :categoryId")
    fun getCategoryWithMenus(categoryId: Int): Flow<CategoryWithMenus>

    @Transaction
    @Query("SELECT * FROM CategoryDb WHERE categoryId = :categoryId")
    fun getCategoryWithDishes(categoryId: Int): Flow<CategoryWidthDishes>

    @Query("SELECT * FROM CategoryDb WHERE categoryId = :categoryId")
    fun getCategory(categoryId: Int): Flow<CategoryDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(categoryDb: CategoryDb)

    @Update
    suspend fun update(categoryDb: CategoryDb)

    @Delete
    suspend fun delete(categoryDb: CategoryDb)
}