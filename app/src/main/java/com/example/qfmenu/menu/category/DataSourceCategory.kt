package com.example.qfmenu.menu.category

import com.example.qfmenu.viewmodels.models.Category

class DataSourceCategory {
    fun loadCategoryMenu(): List<Category> {
        return listOf(
            Category(
                "Category 1"
            ),
            Category(
                "Category 2"
            ),
        )
    }

    fun loadCategoryMenu1(): List<Category> {
        return listOf(
            Category(
                "Category 3"
            ),
            Category(
                "Category 4"
            ),
        )
    }
}