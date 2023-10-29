package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.CategoryWidthDishes
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM CategoryDb")
    fun getCategoriesWithDishes(): Flow<List<CategoryWidthDishes>>

    @Transaction
    @Query("SELECT * FROM CategoryDb WHERE categoryId = :categoryId")
    suspend fun getCategoryWithDishes(categoryId: Long): CategoryWidthDishes

    @Transaction
    @Query("SELECT * FROM CategoryDb WHERE categoryId = :categoryId")
    fun getCategoryWithDishesLiveData(categoryId: Long): LiveData<CategoryWidthDishes>

    @Query("SELECT * FROM CategoryDb WHERE categoryId = :categoryId")
    fun getCategory(categoryId: Long): Flow<CategoryDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categoryList: List<CategoryDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(categoryDb: CategoryDb)

    @Update
    suspend fun update(categoryDb: CategoryDb)

    @Delete
    suspend fun delete(categoryDb: CategoryDb)

    @Query("DELETE FROM DishDb WHERE categoryId = :categoryId")
    suspend fun deleteDishes(categoryId: Long)
}