package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.database.entity.AccountWithCustomers

@Dao
interface AccountDao {
    @Transaction
    @Query("SELECT * FROM AccountDb WHERE id = :accountId")
    suspend fun getAccountWithCustomers(accountId: Long): AccountWithCustomers

    @Query("SELECT * FROM AccountDb WHERE id = :accountId")
    suspend fun getAccount(accountId: Long): AccountDb?

    @Query("SELECT * FROM AccountDb WHERE nameRole = \"staff\"")
    suspend fun getAllStaff(): List<AccountDb>

    @Query("SELECT * FROM AccountDb WHERE nameRole = \"staff\" ORDER BY id DESC LIMIT 1")
    suspend fun getLastStaff(): AccountDb?

    @Transaction
    @Query("SELECT * FROM AccountDb WHERE nameRole = :roleName")
    fun getAccountsWithNameRole(roleName: String): LiveData<List<AccountDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(accountDb: AccountDb)

    @Update
    suspend fun update(accountDb: AccountDb)

    @Delete
    suspend fun delete(accountDb: AccountDb)

    @Delete
    suspend fun deleteAll(accountDbList: List<AccountDb>)
}