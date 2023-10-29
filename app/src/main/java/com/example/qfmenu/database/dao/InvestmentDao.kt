package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qfmenu.database.entity.InvestmentDb
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM InvestmentDb")
    fun getInvestments(): LiveData<List<InvestmentDb>>

    @Query("SELECT * FROM InvestmentDb WHERE name = :investmentNameId")
    fun getInvestments(investmentNameId: String): Flow<InvestmentDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(investments: List<InvestmentDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(investmentDb: InvestmentDb)

    @Update
    suspend fun update(investmentDb: InvestmentDb)

    @Delete
    suspend fun delete(investmentDb: InvestmentDb)

}