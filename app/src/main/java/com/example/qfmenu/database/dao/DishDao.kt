package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.database.entity.DishWithCustomers
import com.example.qfmenu.database.entity.DishWithReviews
import kotlinx.coroutines.flow.Flow

@Dao
interface DishDao {
    @Transaction
    @Query("SELECT * FROM DishDb WHERE dishNameId = :dishNameId")
    fun getDishWithCustomers(dishNameId: String): Flow<DishWithCustomers>

    @Transaction
    @Query("SELECT * FROM DishDb WHERE dishNameId = :dishNameId")
    fun getDishWithReviews(dishNameId: String): Flow<DishWithReviews>

    @Query("SELECT * FROM DishDb")
    fun getDishes(): Flow<List<DishDb>>

    @Query("SELECT * FROM DishDb WHERE dishNameId = :dishNameId")
    fun getDish(dishNameId: String): Flow<DishDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dishDb: DishDb)

    @Update
    suspend fun update(dishDb: DishDb)

    @Delete
    suspend fun delete(dishDb: DishDb)

}