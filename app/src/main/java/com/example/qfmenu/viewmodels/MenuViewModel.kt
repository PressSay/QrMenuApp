package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.CategoryMenuCrossRefDao
import com.example.qfmenu.database.dao.DishDao
import kotlinx.coroutines.launch
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CategoryMenuCrossRef
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.viewmodels.models.Menu
import kotlinx.coroutines.flow.collect

class MenuViewModel(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val categoryMenuCrossRefDao: CategoryMenuCrossRefDao,
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
            menuDao.getMenuWithCategories(menuDb.menuNameId)
                .collect { menuWithCategories ->
                    menuWithCategories.categoriesDb.forEach { categoryDb ->
                        categoryDao.getCategoryWithDishes(categoryDb.categoryNameId)
                            .collect { categoryWithDishes ->
                                categoryWithDishes.dishesDb.forEach {
                                    dishDao.delete(it)
                                }
                            }
                        categoryDao.delete(categoryDb)
                        categoryMenuCrossRefDao.delete(
                            CategoryMenuCrossRef(
                                categoryDb.categoryNameId,
                                menuDb.menuNameId
                            )
                        )
                    }
                }
            menuDao.delete(menuDb)
        }
    }

    fun getMenu(menuNameId: String): LiveData<MenuDb> {
        return menuDao.getMenu(menuNameId).asLiveData()
    }

}

class MenuViewModelFactory(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val categoryMenuCrossRefDao: CategoryMenuCrossRefDao,
    private val menuDao: MenuDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(dishDao, categoryDao, categoryMenuCrossRefDao, menuDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

