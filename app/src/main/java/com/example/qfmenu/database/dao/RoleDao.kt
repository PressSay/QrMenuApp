package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.RoleDb
import com.example.qfmenu.database.entity.RoleWithPermissions
import kotlinx.coroutines.flow.Flow

@Dao
interface RoleDao {
    @Transaction
    @Query("SELECT * FROM RoleDb")
    fun getRolesWithPermissions(): Flow<List<RoleWithPermissions>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(roleDb: RoleDb)

    @Update
    suspend fun update(roleDb: RoleDb)

    @Delete
    suspend fun delete(roleDb: RoleDb)

}