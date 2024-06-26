package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.DishDb

@Dao
interface CustomerDishDao {
    @Query("SELECT * FROM CustomerDishDb WHERE customerId = :customerId")
    suspend fun getListByCustomerId(customerId: Long): List<CustomerDishDb>

    @Query("SELECT * FROM DishDb WHERE dishId = :dishCreatorId")
    suspend fun getDish(dishCreatorId: Long): DishDb

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customerDishCrossRefs: List<CustomerDishDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customerDishDb: CustomerDishDb)

    @Update
    suspend fun update(customerDishDb: CustomerDishDb)

    @Update
    suspend fun  updateAll(customerDishDb: List<CustomerDishDb>)

    @Delete
    suspend fun delete(customerDishDb: CustomerDishDb)

    @Delete
    suspend fun deleteAll(customerDishDb: List<CustomerDishDb>)

    @Query("DELETE FROM CustomerDishDb")
    suspend fun deleteAll()

}