package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.qfmenu.database.entity.RoleDb

@Dao
interface RoleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(roleDb: RoleDb)

    @Update
    suspend fun update(roleDb: RoleDb)

    @Delete
    suspend fun delete(roleDb: RoleDb)

}