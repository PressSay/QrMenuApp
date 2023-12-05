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
    @Query("SELECT * FROM CategoryDb")
    suspend fun getCategories(): List<CategoryDb>
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
    suspend fun getCategory(categoryId: Long): CategoryDb?

    @Query("SELECT * FROM CategoryDb WHERE categoryId = :categoryId")
    fun getCategoryLiveData(categoryId: Long): LiveData<CategoryDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categoryList: List<CategoryDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(categoryDb: CategoryDb)

    @Update
    suspend fun update(categoryDb: CategoryDb)

    @Delete
    suspend fun delete(categoryDb: CategoryDb)

    @Delete
    suspend fun deleteAll(categoryDbList: List<CategoryDb>)

    @Query("DELETE FROM DishDb WHERE categoryId = :categoryId")
    suspend fun deleteDishes(categoryId: Long)

    @Query("SELECT * FROM CategoryDb ORDER BY categoryId DESC LIMIT 1")
    suspend fun getLastCategory(): CategoryDb?

    @Query("UPDATE sqlite_sequence SET seq = (SELECT MAX(categoryId) FROM CategoryDb) WHERE name=\"CategoryDb\"")
    suspend fun resetLastKey()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name=\"CategoryDb\"")
    suspend fun resetKey()

    @Query("SELECT seq FROM sqlite_sequence WHERE name=\"CategoryDb\"")
    suspend fun getLastKey(): Long
}