package com.example.qfmenu.viewmodels

import androidx.lifecycle.ViewModel
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.database.entity.TableDb

class SaveStateViewModel : ViewModel() {

    var stateIsOfflineOrder: Boolean = false
    var stateCategoryPositionMenu: Int = 0
    var stateIsTableUnClock: Boolean = true
    var stateIsStartOrder: Boolean = true
    private var _stateDishDb : DishDb? = null
    private var _stateCategoryDb : CategoryDb? = null
    private var _stateMenuDb: MenuDb? = null
    private var _stateCustomerDb: CustomerDb? = null
    private var _stateCustomerDishCrossRef: CustomerDishCrossRef? = null

    val stateDishDb get() = _stateDishDb!!
    val stateCategoryDb get() = _stateCategoryDb!!
    val stateMenuDb get() = _stateMenuDb!!
    val stateCustomerDb get() = _stateCustomerDb!!
    val stateCustomerDishCrossRef get() = _stateCustomerDishCrossRef!!


    var stateDishesByCategories: MutableList<MutableList<DishAmountDb>> = mutableListOf()
    private var _stateDishes : List<DishAmountDb> = listOf()
    private var _stateCategories: List<CategoryDb> = listOf()
    private var _stateMenus: List<MenuDb> = listOf()
    private var _stateCustomers: List<CustomerDb> = listOf()
    private var _stateCustomerDishCrossRefs: List<CustomerDishCrossRef> = listOf()

//    var stateCustomerWithSelectDishes: MutableList<CustomerWithSelectDishes> = mutableListOf()
//    var stateCustomerWithSelectDishesToBill: MutableList<CustomerWithSelectDishes> = mutableListOf()

    var stateCustomerWithSelectDishesDb: MutableList<CustomerWithSelectDishesDb> = mutableListOf()

    val stateDishes get() = _stateDishes
    val stateCategories get() = _stateCategories
    val stateMenus get() = _stateMenus

    fun setStateDish(state: DishDb) {
        _stateDishDb = state
    }
    fun setStateCategory(state: CategoryDb) {
        _stateCategoryDb = state
    }
    fun setStateMenu(state: MenuDb) {
        _stateMenuDb = state
    }
    fun setStateCustomer(state: CustomerDb) {
        _stateCustomerDb = state
    }
    fun setCustomerDishCrossRefs(state: CustomerDishCrossRef) {
        _stateCustomerDishCrossRef = state
    }

    fun setStateDishesDb(states: List<DishAmountDb>) {
        _stateDishes = states
    }
    fun setStateCategoriesDb(states: List<CategoryDb>) {
        _stateCategories = states
    }
    fun setStateMenusDb(states: List<MenuDb>) {
        _stateMenus = states
    }
    fun setStateCustomers(states: List<CustomerDb>) {
        _stateCustomers = states
    }
    fun setStateCustomerDishCrossRefs(states: List<CustomerDishCrossRef>) {
        _stateCustomerDishCrossRefs = states
    }

}


data class CustomerWithSelectDishesDb (
    val customerDb: CustomerDb,
    val tableDb: TableDb,
    var isSelected: Boolean = false,
)