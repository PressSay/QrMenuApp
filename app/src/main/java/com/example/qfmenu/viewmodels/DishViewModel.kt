package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qfmenu.viewmodels.models.Category
import com.example.menumanager.menu.category.DataSourceCategory
import com.example.menumanager.menu.dish.DatasourceDish
import com.example.qfmenu.viewmodels.models.Dish

class DishViewModel() : ViewModel() {

    private val _dishForCreate = MutableLiveData<Dish>()
    val dishForCreate : LiveData<Dish> get() = _dishForCreate

    private val _dishes = MutableLiveData<List<Dish>>()
    val  dishes: LiveData<List<Dish>> get() = _dishes

    private val _category = MutableLiveData<Category>(DataSourceCategory().loadCategoryMenu().get(0))
    val category : LiveData<Category> get() = _category

    private val _selectedDishes = MutableLiveData<List<Dish>>()
    val selectedDishes : LiveData<List<Dish>> get() = _selectedDishes


    init {
        _dishes.value = DatasourceDish().loadDishMenu()
        _selectedDishes.value = listOf()
    }

    fun setSelectedDishes(selectedDishes: List<Dish>) {
        _selectedDishes.value = selectedDishes
    }

    fun createDish(dish: Dish) {
        _dishForCreate.value = dish
    }

    fun updateDish(id: Int, dish: Dish) {

    }

    fun deleteDish(id: Int) {

    }

    fun getDish(id: Int): Dish? {
        return _dishes.value?.get(id)
    }

    fun setCategory(category: Category) {
        _category.value = category
        if (category.title == "Category 1") {
            _dishes.value = DatasourceDish().loadDishMenu()
        } else {
            _dishes.value = DatasourceDish().loadDishMenu1()
        }
    }

}