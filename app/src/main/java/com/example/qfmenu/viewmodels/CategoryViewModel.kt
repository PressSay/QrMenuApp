package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.MenuWithCategories
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val menuDao: MenuDao,
) : ViewModel() {
    fun insertCategory(categoryDb: CategoryDb) {
        viewModelScope.launch {
            categoryDao.insert(categoryDb)
        }
    }

    fun updateCategory(categoryDb: CategoryDb) {
        viewModelScope.launch {
            categoryDao.update(categoryDb)
        }
    }

    fun deleteCategory(categoryDb: CategoryDb) {
        viewModelScope.launch {
            categoryDao.delete(categoryDb)
        }
    }

    fun deleteCategoryWithDishes(categoryDb: CategoryDb) {
        viewModelScope.launch {
            async(Dispatchers.IO) { categoryDao.getCategoryWithDishes(categoryDb.categoryId) }.await()
                .dishesDb.forEach { dishDb ->
                    dishDao.delete(dishDb)
                }
            categoryDao.delete(categoryDb)
        }
    }

    fun getCategory(categoryId: Long): LiveData<CategoryDb> {
        return categoryDao.getCategory(categoryId).asLiveData()
    }

    suspend fun getCategories(menuId: Long): MenuWithCategories {
        return menuDao.getMenuWithCategories(menuId)
    }

    fun getCategoriesLiveData(menuId: Long): LiveData<MenuWithCategories> {
        return menuDao.getMenuWithCategoriesLiveData(menuId)
    }

}

class CategoryViewModelFactory(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
    private val menuDao: MenuDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(
                dishDao,
                categoryDao,
                menuDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}