package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.TableDb
import com.example.qfmenu.database.entity.TableWithOrders
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Transaction
    @Query("SELECT * FROM TableDb WHERE tableId = :tableId")
    fun getTableWithOrders(tableId: Int): Flow<TableWithOrders>

    @Query("SELECT * FROM TableDb")
    suspend fun getTables(): List<TableDb>

    @Query("SELECT * FROM TableDb")
    fun getTablesLiveData(): LiveData<List<TableDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tables: List<TableDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tableDb: TableDb)

    @Update
    suspend fun update(tableDb: TableDb)

    @Delete
    suspend fun delete(tableDb: TableDb)

    @Query("DELETE FROM TableDb")
    suspend fun deleteAll()

}