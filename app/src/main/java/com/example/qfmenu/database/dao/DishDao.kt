package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.DishDb

@Dao
interface DishDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( dishList: List<DishDb>): List<Long>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dishDb: DishDb)

    @Update
    suspend fun update(dishDb: DishDb)

    @Delete
    suspend fun delete(dishDb: DishDb)

    @Delete
    suspend fun deleteAll(dishDb: List<DishDb>)

    @Query("SELECT * FROM DishDb")
    suspend fun getDishes(): List<DishDb>

    @Query("SELECT * FROM DishDb WHERE dishId = :dishId")
    fun getDish(dishId: Long): DishDb?

    @Query("SELECT * FROM DishDb ORDER BY dishId DESC LIMIT 1")
    suspend fun getLastDish(): DishDb?

    @Query("UPDATE sqlite_sequence SET seq = (SELECT MAX(dishId) FROM DishDb) WHERE name=\"DishDb\"\n")
    suspend fun resetLastKey()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name=\"DishDb\"\n")
    suspend fun resetKey()

    @Query("SELECT seq FROM sqlite_sequence WHERE name=\"DishDb\"\n")
    suspend fun getLastKey(): Long
}