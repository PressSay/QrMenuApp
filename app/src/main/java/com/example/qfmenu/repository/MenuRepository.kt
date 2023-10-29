package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.network.NetworkRetrofit


// fetch api and up it to database
class MenuRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val menuDao: MenuDao,
    private val categoryDao: CategoryDao,
    private val dishDao: DishDao,
) {
    suspend fun createListMenu(menuDbList: List<MenuDb>) {
        menuDbList.forEach { menuDb ->
            val menuNet = networkRetrofit.menu().create(
                menuDb.name,
                menuDb.isUsed.toString()
            )
        }
    }

    suspend fun createListCategory(categoryDbList: List<CategoryDb>) {
        categoryDbList.forEach { categoryDb ->
            val categoryNet = networkRetrofit.category().create(
                categoryDb.menuId.toString(),
                categoryDb.name
            )
        }
    }

    suspend fun createListDish(dishDbList: List<DishDb>) {
        dishDbList.forEach { dishDb ->
            val dishNet = networkRetrofit.dish().create(
                dishDb.categoryId.toString(),
                dishDb.name,
                dishDb.cost.toString(),
                dishDb.description,
                dishDb.numberOfTimesCalled
            )
        }
    }

    suspend fun updateListMenu(menuDbList: List<MenuDb>) {
        menuDbList.forEach { menuDb ->
            val menuNet = networkRetrofit.menu().update(
                menuDb.menuId.toString(),
                menuDb.name,
                menuDb.isUsed.toString()
            )
        }
    }

    suspend fun updateListCategory(categoryDbList: List<CategoryDb>) {
        categoryDbList.forEach { categoryDb ->
            val categoryNet = networkRetrofit.category().update(
                categoryDb.categoryId.toString(),
                categoryDb.name
            )
        }
    }

    suspend fun updateListDish(dishDbList: List<DishDb>) {
        dishDbList.forEach { dishDb ->
            val dishNet = networkRetrofit.dish().update(
                dishDb.dishId.toString(),
                dishDb.name,
                dishDb.cost.toString(),
                dishDb.description,
                dishDb.numberOfTimesCalled.toString()
            )
        }
    }

    suspend fun deleteListMenu(menuDbList: List<MenuDb>) {
        menuDbList.forEach { menuDb ->
            val menuNet = networkRetrofit.menu().delete(
                menuDb.menuId.toString()
            )
        }
    }

    suspend fun deleteListCategory(categoryDbList: List<CategoryDb>) {
        categoryDbList.forEach { categoryDb ->
            val categoryNet = networkRetrofit.category().delete(
                categoryDb.categoryId.toString()
            )
        }
    }

    suspend fun deleteListDish(dishDbList: List<DishDb>) {
        dishDbList.forEach { dishDb ->
            val dishNet = networkRetrofit.dish().delete(
                dishDb.dishId.toString()
            )
        }
    }

    suspend fun fetchMenu() {
        val menuNet = networkRetrofit.menu().findALl()
        if (menuNet.isSuccessful) {
            menuNet.body()?.let {
                menuDao.insertAll(it.map { menu ->
                    MenuDb(
                        menuId = menu.menuId,
                        name = menu.name,
                        isUsed = menu.isUsed
                    )
                })
            }
        }
    }

    suspend fun fetchCategory() {
        val categoryNet = networkRetrofit.category().findALl()
        if (categoryNet.isSuccessful) {
            categoryNet.body()?.let {
                categoryDao.insertAll(it.map { category ->
                    CategoryDb(
                        menuId = category.menuId,
                        categoryId = category.categoryId,
                        name = category.name,
                    )
                })
            }
        }
    }

    suspend fun fetchDish() {
        val dishNet = networkRetrofit.dish().findALl()
        if (dishNet.isSuccessful) {
            dishNet.body()?.let {
                dishDao.insertAll(it.map { dish ->
                    DishDb(
                        dishId = dish.dishId,
                        name = dish.name,
                        cost = dish.cost.toInt(),
                        description = dish.description,
                        image = dish.imageDish.source,
                        categoryId = dish.categoryId,
                        numberOfTimesCalled = dish.numberOfTimesCalled
                    )
                })
            }
        }
    }
}