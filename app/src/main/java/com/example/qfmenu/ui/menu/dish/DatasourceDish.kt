package com.example.qfmenu.ui.menu.dish

import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Dish


class DatasourceDish {
    fun loadDishMenu(): List<Dish> {

        return listOf(
            Dish(
                R.drawable.dish_menu_img,
                "Example 1",
                "Just a example description",
                12000,
                0,
                false
            ),
            Dish(
                R.drawable.dish_menu_img,
                "Example 2",
                "Just a example description",
                12000,
                0,
                false
            ),
            Dish(
                R.drawable.dish_menu_img,
                "Example 3",
                "Just a example description",
                12000,
                0,
                false
            ),
            Dish(
                R.drawable.dish_menu_img,
                "Example 4",
                "Just a example description",
                12000,
                0,
                false
            ),
        )
    }

    fun loadDishMenu1(): List<Dish> {

        return listOf(
            Dish(
                R.drawable.dish_menu_img,
                "Example 5",
                "Just a example description",
                12000,
                0,
                false
            ),
            Dish(
                R.drawable.dish_menu_img,
                "Example 6",
                "Just a example description",
                12000,
                0,
                false
            ),
            Dish(
                R.drawable.dish_menu_img,
                "Example 7",
                "Just a example description",
                12000,
                0,
                false
            ),
            Dish(
                R.drawable.dish_menu_img,
                "Example 8",
                "Just a example description",
                12000,
                0,
                false
            ),
        )
    }

}