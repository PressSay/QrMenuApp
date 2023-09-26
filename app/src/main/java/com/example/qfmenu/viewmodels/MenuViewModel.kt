package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.DishDao
import kotlinx.coroutines.launch
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.MenuDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MenuViewModel(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val menuDao: MenuDao
) : ViewModel() {
    private var _menuForCreate: MenuDb? = null
    val menuForCreate: MenuDb get() = _menuForCreate!!

    val menus: LiveData<List<MenuDb>> get() = menuDao.getMenus().asLiveData()

    fun createMenu(menuDb: MenuDb) {
        _menuForCreate = menuDb
    }

    fun insertMenu(menuDb: MenuDb) {
        viewModelScope.launch {
            menuDao.insert(menuDb)
        }
    }

    fun updateMenu(menuDb: MenuDb) {
        viewModelScope.launch {
            menuDao.update(menuDb)
        }
    }

    fun deleteMenu(menuDb: MenuDb) {
        viewModelScope.launch {

            async(Dispatchers.IO) { menuDao.deleteMenuDishes(menuDb.menuId) }.await()
            menuDao.deleteMenuCategories(menuDb.menuId)
            menuDao.delete(menuDb)

        }
    }

    suspend fun getMenuUsed(): MenuDb {
        return menuDao.getMenuUsed()
    }

}

class MenuViewModelFactory(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val menuDao: MenuDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(dishDao, categoryDao, menuDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

