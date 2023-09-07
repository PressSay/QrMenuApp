package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.qfmenu.database.entity.PermissionRoleCrossRef

@Dao
interface PermissionRoleCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(permissionRoleCrossRef: PermissionRoleCrossRef)

    @Update
    suspend fun update(permissionRoleCrossRef: PermissionRoleCrossRef)

    @Delete
    suspend fun delete(permissionRoleCrossRef: PermissionRoleCrossRef)

}