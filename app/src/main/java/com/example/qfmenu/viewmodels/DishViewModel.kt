package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.viewmodels.models.Dish
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.entity.DishDb
import kotlinx.coroutines.flow.map
import java.lang.IllegalArgumentException
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.entity.CategoryWidthDishes
import kotlinx.coroutines.launch


class DishViewModel(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
) : ViewModel() {
    
    private var _categoryNameId: String = "Popular"
    val categoryNameId: String get() = _categoryNameId

    private var _dishForCreate: DishDb? = null
    val dishForCreate: DishDb get() = _dishForCreate!!

    val dishesWithCategories: LiveData<List<CategoryWidthDishes>> get() = categoryDao.getCategoriesWithDishes().asLiveData()

    private var _selectedDishes: MutableList<DishAmountDb> =  mutableListOf()
    val selectedDishes: MutableList<DishAmountDb> = this._selectedDishes

    fun setSelectedDishes(selectedDishes: List<DishAmountDb>) {
        _selectedDishes.addAll(selectedDishes)
    }

    fun createDish(dishDb: DishDb) {
        _dishForCreate = dishDb
    }

    fun insertDish(dishDb: DishDb) {
        viewModelScope.launch {
            dishDao.insert(dishDb)
        }
    }

    fun updateDish(dishDb: DishDb) {
        viewModelScope.launch {
            dishDao.update(dishDb)
        }
    }

    fun deleteDish(dishDb: DishDb) {
        viewModelScope.launch {
            dishDao.delete(dishDb)
        }
    }

    fun getDish(dishNameId: String): LiveData<DishDb> {
        return dishDao.getDish(dishNameId).asLiveData()
    }

    fun setCategory(categoryNameId: String) {
        _categoryNameId = categoryNameId
    }

}

data class DishAmountDb (
    val dishDb: DishDb,
    val amount: Long,
)

class DishViewModelFactory(
    private val dishDao: DishDao,
    private val categoryDao: CategoryDao,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DishViewModel(dishDao, categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}