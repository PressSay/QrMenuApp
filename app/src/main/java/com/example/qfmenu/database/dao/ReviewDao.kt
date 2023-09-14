package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.ReviewDb
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM ReviewDb")
    fun getReviews(): Flow<List<ReviewDb>>

    @Query("SELECT * FROM ReviewDb WHERE reviewId = :reviewId")
    fun getReview(reviewId: Long): Flow<ReviewDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reviewDb: ReviewDb)

    @Update
    suspend fun update(reviewDb: ReviewDb)

    @Delete
    suspend fun delete(reviewDb: ReviewDb)

}