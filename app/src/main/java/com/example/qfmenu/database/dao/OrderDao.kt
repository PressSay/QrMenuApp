package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.OrderDb

@Dao
interface OrderDao {

    @Query("SELECT * FROM OrderDb WHERE customerOwnerId = :customerOwnerId")
    suspend fun getOrderCustomerOwner(customerOwnerId: Long): OrderDb

    @Query("SELECT * FROM OrderDb ORDER BY orderId DESC LIMIT 1")
    suspend fun getLastOrder(): OrderDb?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<OrderDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(orderDb: OrderDb)

    @Update
    suspend fun update(orderDb: OrderDb)

    @Delete
    suspend fun delete(orderDb: OrderDb)

    @Query("DELETE FROM OrderDb")
    suspend fun deleteAll()

    @Query("UPDATE sqlite_sequence SET seq = (SELECT MAX(orderId) FROM OrderDb) WHERE name=\"OrderDb\"")
    suspend fun resetLastKey()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name=\"OrderDb\"")
    suspend fun resetKey()
}