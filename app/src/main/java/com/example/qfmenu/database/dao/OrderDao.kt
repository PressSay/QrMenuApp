package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.qfmenu.database.entity.OrderDb

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(orderDb: OrderDb)

    @Update
    suspend fun update(orderDb: OrderDb)

    @Delete
    suspend fun delete(orderDb: OrderDb)

}