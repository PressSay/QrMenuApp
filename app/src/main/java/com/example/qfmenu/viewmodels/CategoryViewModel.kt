package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.viewmodels.models.Menu
import com.example.qfmenu.viewmodels.models.Category
import com.example.menumanager.menu.category.DataSourceCategory
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.CategoryMenuCrossRefDao
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.CategoryMenuCrossRef
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val menuDao: MenuDao,
    private val categoryMenuCrossRefDao: CategoryMenuCrossRefDao,
    private val menuNameId: String
) : ViewModel() {
    private var _categoryForCreate: CategoryDb? = null
    val categoryForCreate get() = _categoryForCreate

    private var _categories: LiveData<List<CategoryDb>> =
        menuDao.getMenuWithCategories(menuNameId).map {
            it.categoriesDb
        }.asLiveData()
    val categories: LiveData<List<CategoryDb>> get() = _categories

    fun createCategory(categoryDb: CategoryDb) {
        _categoryForCreate = categoryDb
    }

    fun insertCategory(categoryDb: CategoryDb) {
        viewModelScope.launch {
            categoryDao.insert(categoryDb)
            categoryMenuCrossRefDao.insert(
                CategoryMenuCrossRef(
                    categoryDb.categoryNameId,
                    menuNameId
                )
            )
        }
    }

    fun updateCategory(categoryDb: CategoryDb) {
        viewModelScope.launch {
            categoryDao.update(categoryDb)
            categoryMenuCrossRefDao.update(
                CategoryMenuCrossRef(
                    categoryDb.categoryNameId,
                    menuNameId
                )
            )
        }
    }

    fun deleteCategory(categoryDb: CategoryDb) {
        viewModelScope.launch {
            categoryDao.getCategoryWithDishes(categoryDb.categoryNameId)
                .collect { categoryWidthDishes ->
                    categoryWidthDishes.dishesDb.forEach { dishDb ->
                        dishDao.delete(dishDb)
                    }
                }
            categoryDao.delete(categoryDb)
            categoryMenuCrossRefDao.delete(
                CategoryMenuCrossRef(
                    categoryDb.categoryNameId,
                    menuNameId
                )
            )
        }
    }

    fun getCategory(categoryNameId: String): LiveData<CategoryDb> {
        return categoryDao.getCategory(categoryNameId).asLiveData()
    }

}

class CategoryViewModelFactory(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val menuDao: MenuDao,
    private val categoryMenuCrossRefDao: CategoryMenuCrossRefDao,
    private val menuNameId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(
                dishDao,
                categoryDao,
                menuDao,
                categoryMenuCrossRefDao,
                menuNameId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}