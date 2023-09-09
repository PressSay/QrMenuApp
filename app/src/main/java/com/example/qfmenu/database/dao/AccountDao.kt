package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.database.entity.AccountWithCustomers
import com.example.qfmenu.database.entity.RoleWithAccounts
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Transaction
    @Query("SELECT * FROM AccountDb WHERE accountId = :accountId")
    fun getAccountWithCustomers(accountId: Long): Flow<AccountWithCustomers>

    @Query("SELECT * FROM AccountDb WHERE accountId = :accountId")
    fun getAccount(accountId: Long): Flow<AccountDb>

    @Transaction
    @Query("SELECT * FROM RoleDb WHERE roleNameId = :roleNameId")
    fun getAccountsWithNameRole(roleNameId: String): Flow<RoleWithAccounts>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(accountDb: AccountDb)

    @Update
    suspend fun update(accountDb: AccountDb)

    @Delete
    suspend fun delete(accountDb: AccountDb)
}