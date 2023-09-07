package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.Entity.DishDb
import com.example.qfmenu.database.Entity.DishWithCustomers
import com.example.qfmenu.database.Entity.DishWithReviews
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    @Transaction
    @Query("SELECT * FROM DishDb WHERE dishId = :dishId")
    fun getDishWithCustomers(dishId: Int): Flow<DishWithCustomers>

    @Transaction
    @Query("SELECT * FROM DishDb WHERE dishId = :dishId")
    fun getDishWithReviews(dishId: Int): Flow<DishWithReviews>

    @Query("SELECT * FROM DishDb")
    fun getDishes(): Flow<List<DishDb>>

    @Query("SELECT * FROM DishDb WHERE dishId = :dishId")
    fun getDish(dishId: Int): Flow<DishDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dishDb: DishDb)

    @Update
    suspend fun update(dishDb: DishDb)

    @Delete
    suspend fun delete(dishDb: DishDb)

}