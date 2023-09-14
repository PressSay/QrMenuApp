package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.database.entity.ReviewDb
import kotlinx.coroutines.flow.Flow
@Dao
interface CustomerDishCrossRefDao {
    @Query("SELECT * FROM CustomerDishCrossRef WHERE customerId = :customerId")
   suspend fun getListByCustomerId(customerId: Long): List<CustomerDishCrossRef>
    @Query("SELECT * FROM DishDb WHERE dishId = :dishCreatorId")
    suspend fun getDish(dishCreatorId: Long): DishDb

    @Query("SELECT * FROM ReviewDb WHERE reviewId = :reviewCreatorId")
    suspend fun getReview(reviewCreatorId: Int): ReviewDb

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customerDishCrossRef: CustomerDishCrossRef)

    @Update
    suspend fun update(customerDishCrossRef: CustomerDishCrossRef)

    @Delete
    suspend fun delete(customerDishCrossRef: CustomerDishCrossRef)
}