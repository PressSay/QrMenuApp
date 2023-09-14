package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.OrderDb
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM OrderDb WHERE customerOwnerId = :customerOwnerId ")
    suspend fun getOrderCustomerOwner(customerOwnerId: Long): OrderDb

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(orderDb: OrderDb)

    @Update
    suspend fun update(orderDb: OrderDb)

    @Delete
    suspend fun delete(orderDb: OrderDb)

}