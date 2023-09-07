package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.CustomerAndOrderDb
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.database.entity.CustomerWithDishes
import com.example.qfmenu.database.entity.ReviewCustomerCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Transaction
    @Query("SELECT * FROM CustomerDb WHERE customerId = :customerId")
    fun getCustomerAndOrder(customerId: Int): Flow<CustomerAndOrderDb>

    @Transaction
    @Query("SELECT * FROM CustomerDb WHERE customerId = :customerId")
    fun getCustomerWithDishes(customerId: Int): Flow<CustomerWithDishes>

    @Query("SELECT * FROM CustomerDishCrossRef WHERE customerId = :customerId")
    fun getCustomerDishCrossRefs(customerId: Int): Flow<List<CustomerDishCrossRef>>

    @Query("SELECT * FROM ReviewCustomerCrossRef WHERE customerId = :customerId")
    fun getReviewCustomerCrossRefs(customerId: Int): Flow<ReviewCustomerCrossRef>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customerDb: CustomerDb)

    @Update
    suspend fun update(customerDb: CustomerDb)

    @Delete
    suspend fun delete(customerDb: CustomerDb)

}