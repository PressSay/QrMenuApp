package com.example.qfmenu.repository

import android.util.Log
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.network.entity.Category
import com.example.qfmenu.network.entity.Dish
import com.example.qfmenu.network.entity.Menu


// fetch api and up it to database
class MenuRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val menuDao: MenuDao,
    private val categoryDao: CategoryDao,
    private val dishDao: DishDao,
) {
    suspend fun createMenuDb(menu: Menu) {}

    suspend fun createMenu(menuDb: MenuDb) {
        networkRetrofit.menu().create(
            menuDb.name,
            isUsed = if (menuDb.isUsed) 1 else 0
        )
    }

    suspend fun createCategory(categoryDb: CategoryDb) {
        networkRetrofit.category().create(
            categoryDb.name,
            categoryDb.menuId.toString()
        )
    }
    suspend fun createDish(dishDb: DishDb) {
        networkRetrofit.dish().create(
            dishDb.categoryId.toString(),
            dishDb.name,
            dishDb.description,
            dishDb.cost.toString(),
            dishDb.numberOfTimesCalled
        )
    }

    suspend fun updateMenu(menu: Menu) {
        menuDao.update(
            MenuDb(
                menuId = menu.menuId,
                name = menu.name,
                isUsed = menu.isUsed == 1
            )
        )
    }

    suspend fun updateMenuNet(menuDb: MenuDb) {
        networkRetrofit.menu().update(
            menuDb.menuId.toString(),
            menuDb.name,
            if (menuDb.isUsed) "1" else "0"
        )
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.update(
            CategoryDb(
                categoryId = category.categoryId,
                name = category.name,
                menuId = category.menuId
            )
        )
    }

    suspend fun updateCategoryNet(categoryDb: CategoryDb) {
        networkRetrofit.category().update(
            id = categoryDb.categoryId.toString(),
            newName = categoryDb.name
        )
    }

    suspend fun updateDish(dish: Dish) {
        dishDao.update(
            DishDb(
                dishId = dish.dishId,
                name = dish.name,
                cost = dish.cost.toInt(),
                description = dish.description,
                categoryId = dish.categoryId,
                numberOfTimesCalled = dish.numberOfTimesCalled,
                image = dish.imageDish?.source ?: "Empty"
            )
        )
    }

    suspend fun updateDishNet(dishDb: DishDb) {
        networkRetrofit.dish().update(
            dishDb.dishId.toString(),
            dishDb.name,
            dishDb.description,
            dishDb.cost.toString(),
            dishDb.numberOfTimesCalled.toString()
        )
    }

    suspend fun deleteMenu(menuDb: MenuDb) {
        menuDao.deleteMenuDishes(menuDb.menuId)
        menuDao.deleteMenuCategories(menuDb.menuId)
        menuDao.delete(menuDb)
    }

    suspend fun deleteMenuNet(menu: Menu) {
        this.updateMenuNet(
            MenuDb(
                menuId = menu.menuId,
                name = menu.name,
                isUsed = false
            )
        )
        networkRetrofit.menu().delete(
            menu.menuId.toString()
        )
    }


    suspend fun deleteCategory(categoryDb: CategoryDb) {
        categoryDao.deleteDishes(categoryDb.categoryId)
        categoryDao.delete(categoryDb)
    }

    suspend fun deleteCategoryNet(category: Category) {
        networkRetrofit.category().delete(
            category.categoryId.toString()
        )
    }

    suspend fun deleteDish(dishDb: DishDb) {
        dishDao.delete(dishDb)
        dishDao.resetKey()
    }

    suspend fun deleteDishNet(dish: Dish) {
        networkRetrofit.dish().delete(
            dish.dishId.toString()
        )
    }

    suspend fun resetKey() {
        val menuDbLast = menuDao.getLastMenu()
        val categoryDbLast = categoryDao.getLastCategory()
        val dishDbLast = dishDao.getLastDish()
        if (menuDbLast != null) {
            menuDao.resetLastKey()
        } else {
            menuDao.resetKey()
        }
        if (categoryDbLast != null) {
            categoryDao.resetLastKey()
        } else {
            categoryDao.resetKey()
        }
        if (dishDbLast != null) {
            dishDao.resetLastKey()
        } else {
            dishDao.resetKey()
        }
    }

    suspend fun fetchMenu(isSync: Boolean) {
        val menuListNet = networkRetrofit.menu().findALl()
        val menuDbList = menuDao.getMenus()
        val lastKey = menuDao.getLastKey()
        if (!menuListNet.isSuccessful) {
            return
        }
        val menuSr = menuListNet.body()!!
        if (menuSr.isNotEmpty() && isSync && lastKey >= menuSr.last().menuId) {
            // through server
            var i = 0
            while (i < menuSr.size) {
                val menu = menuSr[i++]
                val menuDb = menuDbList.firstOrNull { menuDb ->
                    menuDb.menuId == menu.menuId
                }
                try {
                    if (menuDb == null) {
                        deleteMenuNet(menu)
                    } else {
                        updateMenuNet(menuDb)
                    }
                } catch (e: Exception) {
                    Log.e("MenuRepository", menuDb.toString())
                }
            }
            // foreach through local data, delete if not exist in server
            for (i in menuDbList.indices) {
                val menuDb = menuDbList[i]
                val menuNet = networkRetrofit.menu().findOne(menuDb.menuId.toString())
                if (!menuNet.isSuccessful) {
                    this.deleteMenu(menuDb)
                } else {
                    val menuNetCur = menuNet.body()!!
                    this.updateMenu(menuNetCur)
                }
            }

        }
        if (menuSr.isNotEmpty() && (!isSync || lastKey < menuSr.last().menuId)) {
            for (menuDb in menuDbList) {
                if (!isSync) {
                    this.createMenu(menuDb)
                }
                this.deleteMenu(menuDb)
            }
            menuDao.insertAll(menuSr.map { menu ->
                MenuDb(
                    menuId = menu.menuId,
                    name = menu.name,
                    isUsed = menu.isUsed == 1
                )
            })
            if (lastKey > 0 && isSync) fetchCategory(isSync = false, isMenuUpdate = true)
        }
    }

    suspend fun fetchCategory(isSync: Boolean, isMenuUpdate: Boolean = false) {
        val categoryListNet = networkRetrofit.category().findALl()
        if (!categoryListNet.isSuccessful) {
            return
        }
        val categorySr = categoryListNet.body()!!
        val lastKey = categoryDao.getLastKey()
        if (!isMenuUpdate && categorySr.isNotEmpty() && isSync && lastKey >= categorySr.last().categoryId) {
            // foreach through local data
            val categoryDbList = categoryDao.getCategories()
            for (i in categoryDbList.indices) {
                val categoryDb = categoryDbList[i]
                val categoryNet =
                    networkRetrofit.category().findOne(categoryDb.categoryId.toString())
                if (!categoryNet.isSuccessful) {
                    this.deleteCategory(categoryDb)
                } else {
                    val categoryNetCur = categoryNet.body()!!
                    this.updateCategory(categoryNetCur)
                }
            }
            // foreach through server data
            for (i in categorySr.indices) {
                val category = categorySr[i]
                val categoryDb = categoryDao.getCategory(category.categoryId)
                if (categoryDb == null) {
                    this.deleteCategoryNet(category)
                } else {
                    this.updateCategoryNet(categoryDb)
                }
            }
        }
        if (categorySr.isNotEmpty() && (isMenuUpdate || !isSync || lastKey < categorySr.last().categoryId)) {
            val categoryDbList = categoryDao.getCategories()
            for (categoryDb in categoryDbList) {
                if (!isSync) {
                    this.createCategory(categoryDb)
                }
                this.deleteCategory(categoryDb)
            }
            categoryDao.insertAll(categorySr.map { category ->
                CategoryDb(
                    categoryId = category.categoryId,
                    name = category.name,
                    menuId = category.menuId
                )
            })
            if (isMenuUpdate) fetchDish(isSync = false, isCategoryUpdate = true)
        }
    }

    suspend fun fetchDish(isSync: Boolean, isCategoryUpdate: Boolean = false) {
        val dishListNet = networkRetrofit.dish().findALl()
        val lastKey = dishDao.getLastKey()
        if (!dishListNet.isSuccessful) {
            return
        }
        val dishSr = dishListNet.body()!!
        if (!isCategoryUpdate && dishSr.isNotEmpty() && isSync && lastKey >= dishSr.last().dishId) {
            val dishDbList = dishDao.getDishes()
            // foreach through local data
            for (i in dishDbList.indices) {
                val dishDb = dishDbList[i]
                val dishNet = networkRetrofit.dish().findOne(dishDb.dishId.toString())
                if (!dishNet.isSuccessful) {
                    dishDao.delete(dishDb)
                } else {
                    val dishNetCur = dishNet.body()!!
                    this.updateDish(dishNetCur)
                }
            }
            // foreach through server data
            for (i in dishSr.indices) {
                val dish = dishSr[i]
                val dishDb = dishDao.getDish(dish.dishId)
                if (dishDb == null) {
                    this.deleteDishNet(dish)
                } else {
                    this.updateDishNet(dishDb)
                }
            }
        }
        if (dishSr.isNotEmpty() && (isCategoryUpdate || !isSync || lastKey < dishSr.last().dishId)) {
            val dishDbList = dishDao.getDishes()
//                this.createListDish(dishDbList)
            for (dishDb in dishDbList) {
                if (!isSync) {
                    this.createDish(dishDb)
                }
                this.deleteDish(dishDb)
            }
            dishDao.insertAll(dishSr.map { dish ->
                DishDb(
                    dishId = dish.dishId,
                    name = dish.name,
                    cost = dish.cost.toInt(),
                    description = dish.description,
                    categoryId = dish.categoryId,
                    numberOfTimesCalled = dish.numberOfTimesCalled,
                    image = dish.imageDish?.source ?: "Empty"
                )
            })
        }
    }
}