package com.example.qfmenu.database.dao

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
    fun getInvestments(): Flow<List<InvestmentDb>>

    @Query("SELECT * FROM InvestmentDb WHERE investmentName = :investmentNameId")
    fun getInvestments(investmentNameId: String): Flow<InvestmentDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(investmentDb: InvestmentDb)

    @Update
    suspend fun update(investmentDb: InvestmentDb)

    @Delete
    suspend fun delete(investmentDb: InvestmentDb)

}