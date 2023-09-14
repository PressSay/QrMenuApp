package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qfmenu.database.dao.DishDao
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
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

    private var _selectedDishes: MutableList<DishAmountDb> = mutableListOf()
    val selectedDishes: MutableList<DishAmountDb> = this._selectedDishes

    fun setSelectedDishes(selectedDishes: List<DishAmountDb>) {
        _selectedDishes.addAll(selectedDishes)
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

    fun getDishesLiveData(categoryId: Long): LiveData<List<DishDb>> {
        return categoryDao.getCategoryWithDishesLiveData(categoryId).map { it.dishesDb }
    }


    suspend fun getDishesAmountDb(categoryId: Long): List<DishAmountDb> {
        return categoryDao.getCategoryWithDishes(categoryId).dishesDb.map {
            DishAmountDb(
                it,
                0,
                false
            )
        }
    }


    fun getDishesAmountDbLiveData(categoryId: Long): LiveData<List<DishAmountDb>> {
        return categoryDao.getCategoryWithDishesLiveData(categoryId).map { dishes ->
            dishes.dishesDb.map { DishAmountDb(it, 0, false) }
        }
    }

}

data class DishAmountDb(
    val dishDb: DishDb,
    var amount: Long,
    var selected: Boolean = false
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