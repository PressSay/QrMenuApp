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
import com.example.qfmenu.database.entity.AccountWithRoles
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Transaction
    @Query("SELECT * FROM AccountDb WHERE accountId = :accountId")
    fun getAccountWithRoles(accountId: Int): Flow<AccountWithRoles>

    @Transaction
    @Query("SELECT * FROM AccountDb WHERE accountId = :accountId")
    fun getAccountWithCustomers(accountId: Int): Flow<AccountWithCustomers>

    @Query("SELECT * FROM AccountDb WHERE accountId = :accountId")
    fun getAccount(accountId: Int): Flow<AccountDb>

    @Query("SELECT * FROM AccountDb JOIN RoleDb ON accountCreatorId = accountId WHERE RoleDb.name = :nameRole")
    fun getAccountsWithNameRole(nameRole: String): Flow<List<AccountDb>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(accountDb: AccountDb)

    @Update
    suspend fun update(accountDb: AccountDb)

    @Delete
    suspend fun delete(accountDb: AccountDb)
}