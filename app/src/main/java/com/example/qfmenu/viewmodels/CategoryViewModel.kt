package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.MenuWithCategories
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
            categoryDao.deleteDishes(categoryDb.categoryId)
            categoryDao.delete(categoryDb)
        }
    }

    fun getCategory(categoryId: Long): LiveData<CategoryDb> {
        return categoryDao.getCategoryLiveData(categoryId)
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