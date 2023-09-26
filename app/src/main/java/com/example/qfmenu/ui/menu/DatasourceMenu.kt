package com.example.qfmenu.ui.menu

import com.example.qfmenu.viewmodels.models.Menu

class DatasourceMenu {
    fun loadMenu(): List<Menu> {
        return listOf<Menu>(
            Menu("Menu 1", false),
            Menu("Menu 2", true),
        )
    }
}