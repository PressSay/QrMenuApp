package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.menumanager.menu.DatasourceMenu
import com.example.qfmenu.viewmodels.models.Menu

class MenuViewModel {

    private val _menuForCreate = MutableLiveData<Menu>()
    val menuForCreate : LiveData<Menu> = _menuForCreate

    private val _menus = MutableLiveData<List<Menu>>()
    val menus : LiveData<List<Menu>> get() = _menus

    init {
        _menus.value = DatasourceMenu().loadMenu()
    }

    fun createMenu(menu: Menu) {
        _menuForCreate.value = menu
    }

    fun updateMenu(id: Int, menu: Menu) {}

    fun deleteMenu(id: Int, menu: Menu) {}

    fun getMenu(id: Int): Menu? {
        return _menus.value?.get(id)
    }

}