package com.example.qfmenu.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.database.entity.MenuWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Transaction
    @Query("SELECT * FROM MenuDb WHERE menuNameId = :menuNameId")
    fun getMenuWithCategories(menuNameId: String): Flow<MenuWithCategories>

    @Query("SELECT * FROM MenuDb")
    fun getMenus(): Flow<List<MenuDb>>

    @Query("SELECT * FROM MenuDb WHERE menuNameId = :menuNameId")
    fun getMenu(menuNameId: String): Flow<MenuDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(menuDb: MenuDb)

    @Update
    suspend fun update(menuDb: MenuDb)

    @Delete
    suspend fun delete(menuDb: MenuDb)
}