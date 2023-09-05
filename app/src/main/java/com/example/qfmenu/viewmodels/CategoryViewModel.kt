package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qfmenu.viewmodels.models.Menu
import com.example.qfmenu.viewmodels.models.Category
import com.example.menumanager.menu.category.DataSourceCategory

class CategoryViewModel : ViewModel() {
    private val _categoryForCreate = MutableLiveData<Category>()
    val categoryForCreate : LiveData<Category> get() = _categoryForCreate

    private val _categories = MutableLiveData<List<Category>>()
    val categories : LiveData<List<Category>> get() = _categories

    private val _menu = MutableLiveData<Menu>()
    val menu: LiveData<Menu> get() = _menu

    init {
        _categories.value = DataSourceCategory().loadCategoryMenu()
        _menu.value = Menu("Menu 1", false)
    }

    fun createCategory(category: Category) {
        _categoryForCreate.value = category
    }

    fun updateCategory(id: Int, category: Category) {}

    fun deleteCategory(id: Int, category: Category) {}

    fun getCategory(id: Int): Category? {
        return _categories.value?.get(id)
    }

    fun setMenu(menu: Menu) {
        _menu.value = menu
        if (menu.title == "Menu 1") {
            _categories.value = DataSourceCategory().loadCategoryMenu()
        } else {
            _categories.value = DataSourceCategory().loadCategoryMenu1()
        }
    }

}