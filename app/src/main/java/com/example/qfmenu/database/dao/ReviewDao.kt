package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qfmenu.database.entity.ReviewBillDb
import com.example.qfmenu.database.entity.ReviewDishDb

@Dao
interface ReviewDao {
    @Query("SELECT COUNT(*) FROM ReviewBillDb")
    fun countRevBill(): LiveData<Long>

    @Query("SELECT COUNT(*) FROM reviewdishdb")
    fun countRevDish(): LiveData<Long>

    @Query("SELECT COUNT(*) FROM reviewdishdb WHERE dishId = :dishId")
    suspend fun countRevDish(dishId: Long): Long

    @Query("SELECT * FROM reviewdishdb")
    suspend fun getRevDishArr(): List<ReviewDishDb>

    @Query("SELECT * FROM ReviewBillDb")
    suspend fun getRevBillArr(): List<ReviewBillDb>

    @Query("SELECT * FROM ReviewBillDb")
    fun getRevBillArrLiveData(): LiveData<List<ReviewBillDb>>

    @Query("SELECT * FROM ReviewDishDb WHERE dishId = :dishId AND customerId = :customerId")
    suspend fun getRevDish(dishId: Long, customerId: Long): ReviewDishDb?

    @Query("SELECT * FROM ReviewBillDb WHERE customerId = :customerId")
    suspend fun getRevBill(customerId: Long): ReviewBillDb?

    @Query("UPDATE ReviewDishDb SET isThumbUp = :isThumbUp, description = :description WHERE dishId = :dishId AND customerId = :customerId")
    suspend fun updateRevDish(dishId: Long, customerId: Long, description: String, isThumbUp: Int)

    @Query("UPDATE ReviewBillDb SET isThumbUp = :isThumbUp, description = :description WHERE customerId = :customerId")
    suspend fun updateRevBill(customerId: Long, description: String, isThumbUp: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRevDish(reviewDishDb: ReviewDishDb): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun  insertRevBill(reviewBillDb: ReviewBillDb): Long

    @Query("SELECT * FROM ReviewDishDb WHERE dishId = :dishId")
    fun getReviewsByDishId(dishId: Long): LiveData<List<ReviewDishDb>>

    @Delete
    suspend fun deleteRevBill(reviewDb: ReviewBillDb)

    @Query("DELETE FROM ReviewDishDb WHERE dishId = :dishId AND customerId = :customerId")
    suspend fun deleteRevDish(dishId: Long, customerId: Long)
}