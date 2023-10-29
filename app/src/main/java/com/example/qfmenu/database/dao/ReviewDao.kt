package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.ReviewCustomerDb
import com.example.qfmenu.database.entity.ReviewDb
import com.example.qfmenu.database.entity.ReviewDishDb
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM ReviewDb")
    fun getReviews(): Flow<List<ReviewDb>>

    @Query("SELECT * FROM ReviewDb WHERE reviewId = :reviewId")
    fun getReview(reviewId: Long): ReviewDb



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reviewDb: ReviewDb): Long

    @Update
    suspend fun update(reviewDb: ReviewDb)

    @Delete
    suspend fun delete(reviewDb: ReviewDb)

    @Query("SELECT * FROM ReviewCustomerDb WHERE customerId = :customerId")
    suspend fun getCustomerReview(customerId: Long): ReviewCustomerDb

    @Query("SELECT * FROM ReviewCustomerDb JOIN ReviewDb ON ReviewCustomerDb.reviewId = ReviewDb.reviewId")
    fun getReviewCustomerCrossRefs(): LiveData<List<ReviewDb>>

    @Query("SELECT * FROM ReviewDishDb JOIN ReviewDb ON ReviewDb.reviewId = ReviewDishDb.reviewId WHERE ReviewDishDb.dishId = :dishId")
    fun getReviewsByDishId(dishId: Long): LiveData<List<ReviewDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReviewCustomerCrossRef(reviewCustomerDbs: List<ReviewCustomerDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReviewCustomerCrossRef(reviewCustomerDb: ReviewCustomerDb)

    @Update
    suspend fun updateReviewCustomerCrossRef(reviewCustomerDb: ReviewCustomerDb)

    @Delete
    suspend fun deleteReviewCustomerCrossRef(reviewCustomerDb: ReviewCustomerDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReviewDishCrossRef(reviewDishDbs: List<ReviewDishDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReviewDishCrossRef(reviewDishDb: ReviewDishDb)

    @Update
    suspend fun updateReviewDishCrossRef(reviewDishDb: ReviewDishDb)

    @Delete
    suspend fun deleteReviewDishCrossRef(reviewDishDb: ReviewDishDb)

}