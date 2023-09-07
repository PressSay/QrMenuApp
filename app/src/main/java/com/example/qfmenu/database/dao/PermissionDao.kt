package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.Entity.PermissionDb
import com.example.qfmenu.database.Entity.PermissionWithRoles
import kotlinx.coroutines.flow.Flow

@Dao
interface PermissionDao {
    @Transaction
    @Query("SELECT * FROM PermissionDb")
    fun getPermissionsWithRoles(): Flow<List<PermissionWithRoles>>

    @Query("SELECT * FROM PermissionDb")
    fun getPermissions(): Flow<List<PermissionDb>>

    @Query("SELECT * FROM PermissionDb WHERE permissionId = :permissionId")
    fun getPermission(permissionId: Int): Flow<PermissionDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(permissionDb: PermissionDb)

    @Update
    suspend fun update(permissionDb: PermissionDb)

    @Delete
    suspend fun delete(permissionDb: PermissionDb)

}