package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.CustomerAndOrderDb
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.CustomerWithDishes
import com.example.qfmenu.database.entity.ReviewCustomerDb
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Transaction
    @Query("SELECT * FROM CustomerDb WHERE customerId = :customerId")
    suspend fun getCustomerAndOrder(customerId: Long): CustomerAndOrderDb

    @Transaction
    @Query("SELECT * FROM CustomerDb WHERE customerId = :customerId")
    suspend fun getCustomerWithDishes(customerId: Long): CustomerWithDishes

    @Transaction
    @Query("SELECT * FROM CustomerDb")
    suspend fun getAllCustomerAndOrder(): List<CustomerAndOrderDb>

    @Query("SELECT * FROM CustomerDb JOIN OrderDb ON CustomerDb.customerId = OrderDb.customerOwnerId WHERE OrderDb.status = 'Bill Not Paid'")
    fun getCustomersUnConfirmed(): Flow<List<CustomerDb>>

    @Query("SELECT * FROM CustomerDb WHERE created = :calendarPaid")
    fun getCustomersByCalendar(calendarPaid: String): LiveData<List<CustomerDb>>

    @Query("SELECT * FROM CustomerDb ORDER BY customerId DESC LIMIT 1")
    fun getLastCustomer(): Flow<CustomerDb>

    @Query("SELECT * FROM CustomerDishDb WHERE customerId = :customerId")
    suspend fun getCustomerDishCrossRefs(customerId: Long): List<CustomerDishDb>

    @Query("SELECT * FROM ReviewCustomerDb WHERE customerId = :customerId")
    fun getReviewCustomerCrossRefs(customerId: Long): Flow<ReviewCustomerDb>

    @Query("SELECT * FROM CustomerDb WHERE customerId = :customerId")
    suspend fun getCustomer(customerId: Long): CustomerDb

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customerDishCrossRefs: List<CustomerDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE,)
    suspend fun insert(customerDb: CustomerDb) : Long

    @Update
    suspend fun update(customerDb: CustomerDb)

    @Delete
    suspend fun delete(customerDb: CustomerDb)

}