package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.ReviewCustomerCrossRef
import com.example.qfmenu.database.entity.ReviewDb
import com.example.qfmenu.database.entity.ReviewDishCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM ReviewDb")
    fun getReviews(): Flow<List<ReviewDb>>

    @Query("SELECT * FROM ReviewDb WHERE reviewId = :reviewId")
    fun getReview(reviewId: Long): Flow<ReviewDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reviewDb: ReviewDb): Long

    @Update
    suspend fun update(reviewDb: ReviewDb)

    @Delete
    suspend fun delete(reviewDb: ReviewDb)

    @Query("SELECT * FROM ReviewCustomerCrossRef WHERE customerId = :customerId")
    suspend fun getCustomerReview(customerId: Long): ReviewCustomerCrossRef

    @Query("SELECT * FROM ReviewCustomerCrossRef JOIN ReviewDb ON ReviewCustomerCrossRef.reviewId = ReviewDb.reviewId")
    fun getReviewCustomerCrossRefs(): LiveData<List<ReviewDb>>

    @Query("SELECT * FROM ReviewDishCrossRef JOIN ReviewDb ON ReviewDb.reviewId = ReviewDishCrossRef.reviewId WHERE ReviewDishCrossRef.dishId = :dishId")
    fun getReviewsByDishId(dishId: Long): LiveData<List<ReviewDb>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReviewCustomerCrossRef(reviewCustomerCrossRef: ReviewCustomerCrossRef)

    @Update
    suspend fun updateReviewCustomerCrossRef(reviewCustomerCrossRef: ReviewCustomerCrossRef)

    @Delete
    suspend fun deleteReviewCustomerCrossRef(reviewCustomerCrossRef: ReviewCustomerCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReviewDishCrossRef(reviewDishCrossRef: ReviewDishCrossRef)

    @Update
    suspend fun updateReviewDishCrossRef(reviewDishCrossRef: ReviewDishCrossRef)

    @Delete
    suspend fun deleteReviewDishCrossRef(reviewDishCrossRef: ReviewDishCrossRef)

}