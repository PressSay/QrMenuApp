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

@Dao
interface ReviewDao {

    @Query("SELECT COUNT(*) FROM ReviewCustomerDb")
    fun countCustRev(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM ReviewDb")
    fun countRev(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM ReviewDishDb WHERE dishId = :dishId")
    suspend fun countDshRev(dishId: Long): Int

    @Query("SELECT * FROM ReviewDb WHERE reviewId = :reviewId")
    fun getReview(reviewId: Long): ReviewDb?

    @Query("SELECT * FROM ReviewDb ORDER BY reviewId DESC LIMIT 1")
    fun getLastReview(): ReviewDb?

    @Query("SELECT * FROM ReviewDishDb ORDER BY reviewId DESC LIMIT 1")
    fun getLastRevDish(): ReviewDishDb?

    @Query("SELECT * FROM ReviewCustomerDb ORDER BY reviewId DESC LIMIT 1")
    fun getLastRevCustomer(): ReviewCustomerDb?

    @Query("SELECT * FROM ReviewDb")
    fun getReviewListLiveData(): LiveData<List<ReviewDb>>

    @Query("SELECT * FROM ReviewDb WHERE reviewId = :reviewId")
    fun getReviewLiveData(reviewId: Long): LiveData<ReviewDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(reviewDb: ReviewDb): Long

    @Update
    suspend fun update(reviewDb: ReviewDb)

    @Delete
    suspend fun delete(reviewDb: ReviewDb)

    @Query("SELECT * FROM ReviewDishDb WHERE reviewId = :reviewId")
    suspend fun getReviewDishDb(reviewId: Long): ReviewDishDb?

    @Query("SELECT * FROM ReviewCustomerDb WHERE reviewId = :reviewId")
    suspend fun getReviewCustomerDb(reviewId: Long): ReviewCustomerDb?


    @Query("SELECT * FROM ReviewCustomerDb WHERE customerId = :customerId")
    suspend fun getCustomerReview(customerId: Long): ReviewCustomerDb

    @Query("SELECT * FROM ReviewCustomerDb JOIN ReviewDb ON ReviewCustomerDb.reviewId = ReviewDb.reviewId")
    fun getReviewCustomerCrossRefs(): LiveData<List<ReviewDb>>

    @Query("SELECT * FROM ReviewDishDb JOIN ReviewDb ON ReviewDb.reviewId = ReviewDishDb.reviewId WHERE ReviewDishDb.dishId = :dishId")
    fun getReviewsByDishId(dishId: Long): LiveData<List<ReviewDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReviewCustomerCrossRef(reviewCustomerDbs: List<ReviewCustomerDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewCustomerCrossRef(reviewCustomerDb: ReviewCustomerDb)

    @Update
    suspend fun updateReviewCustomerCrossRef(reviewCustomerDb: ReviewCustomerDb)

    @Delete
    suspend fun deleteReviewCustomerCrossRef(reviewCustomerDb: ReviewCustomerDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviewDishCrossRef(reviewDishDb: ReviewDishDb)

    @Update
    suspend fun updateReviewDishCrossRef(reviewDishDb: ReviewDishDb)

    @Delete
    suspend fun deleteReviewDishCrossRef(reviewDishDb: ReviewDishDb)

    @Query("DELETE FROM ReviewDishDb")
    suspend fun deleteAllReviewDish()

    @Query("DELETE FROM ReviewCustomerDb")
    suspend fun deleteAllReviewCustomer()

    @Delete
    suspend fun deleteAllReview(reviewDbList: List<ReviewDb>)

    @Query("UPDATE sqlite_sequence SET seq = (SELECT MAX(reviewId) FROM ReviewDb) WHERE name=\"ReviewDb\"")
    suspend fun resetLastKey()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name=\"ReviewDb\"")
    suspend fun resetKey()

    @Query("DELETE FROM ReviewDb")
    suspend fun deleteAll()

}