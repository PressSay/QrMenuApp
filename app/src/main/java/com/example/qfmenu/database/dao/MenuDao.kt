package com.example.qfmenu.database.dao

import androidx.lifecycle.LiveData
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
    @Query("SELECT * FROM MenuDb WHERE menuId = :menuId")
    suspend fun getMenuWithCategories(menuId: Long): MenuWithCategories

    @Transaction
    @Query("SELECT * FROM MenuDb WHERE menuId = :menuId")
    fun getMenuWithCategoriesLiveData(menuId: Long): LiveData<MenuWithCategories>

    @Query("SELECT * FROM MenuDb")
    fun getMenus(): Flow<List<MenuDb>>

    @Query("SELECT * FROM MenuDb WHERE isUsed = :isUsed")
    suspend fun getMenuUsed(isUsed: Boolean = true): MenuDb

    @Query("SELECT * FROM MenuDb WHERE isUsed = :isUsed")
    fun getMenuUsedLiveData(isUsed: Boolean = true): LiveData<MenuDb>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(menus: List<MenuDb>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(menuDb: MenuDb)

    @Update
    suspend fun update(menuDb: MenuDb)

    @Delete
    suspend fun delete(menuDb: MenuDb)

    @Query("DELETE FROM DishDb WHERE categoryId IN (SELECT categoryId FROM CategoryDb WHERE menuId = :menuId)")
    suspend fun deleteMenuDishes(menuId: Long)

    @Query("DELETE FROM CategoryDb WHERE menuId = :menuId")
    suspend fun deleteMenuCategories(menuId: Long)

}