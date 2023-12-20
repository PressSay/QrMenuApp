package com.example.qfmenu.viewmodels

import androidx.lifecycle.ViewModel
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.database.entity.CategoryDb
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.DishDb
import com.example.qfmenu.database.entity.MenuDb
import com.example.qfmenu.database.entity.TableDb
import java.util.Calendar

class SaveStateViewModel : ViewModel() {

    var isOpenSlide = false
    var stateTableDb: TableDb? = null
    var stateReviewDb: Int = -1
    var stateCalendar: String = "%" + String.format(
        "%04d-%02d-%02d",
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH)+1,
        Calendar.getInstance().get(Calendar.DATE)) + "%"
    var stateCustomerOrderQueue: CustomerOrderQueue? = null

    var stateIsTableDetail: Boolean = false

    var stateIsOffOnOrder: Boolean = false
    var stateCategoryPosition: Long = 0
    var stateIsTableUnClock: Boolean = true
    var stateIsStartOrder: Boolean = true
    var stateAccountDb: AccountDb? = null

    private var _stateDishDb : DishDb? = null
    private var _stateCategoryDb : CategoryDb? = null
    private var _stateMenuDb: MenuDb? = null
    private var _stateCustomerDb: CustomerDb? = null

    val stateDishDb get() = _stateDishDb!!
    val stateCategoryDb get() = _stateCategoryDb!!
    val stateMenuDb get() = _stateMenuDb!!
    val stateCustomerDb get() = _stateCustomerDb!!

//    var stateDishesByCategories: MutableList<MutableList<DishAmountDb>> = mutableListOf()
    var stateDishesByCategories: MutableMap<Long, MutableList<DishAmountDb>> = mutableMapOf()
    private var _stateDishes : List<DishAmountDb> = listOf()
    private var _stateCustomerDishDbs: List<CustomerDishDb> = listOf()

//    var stateCustomerWithSelectDishes: MutableList<CustomerWithSelectDishes> = mutableListOf()
//    var stateCustomerWithSelectDishesToBill: MutableList<CustomerWithSelectDishes> = mutableListOf()

    var posCusCurrentQueue: Int = -1
    var stateCustomerOrderQueues: MutableList<CustomerOrderQueue> = mutableListOf()
    var stateCustomerOrderQueuesPos: MutableList<Int> = mutableListOf()
    val stateDishes get() = _stateDishes

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
    fun setStateDishesDb(states: List<DishAmountDb>) {
        _stateDishes = states
    }
    fun setStateCustomerDishCrossRefs(states: List<CustomerDishDb>) {
        _stateCustomerDishDbs = states
    }

}


data class CustomerOrderQueue (
    val customerDb: CustomerDb = CustomerDb(
        -1,
        -1,
        "",
        "",
        "",
        "",
        ""
    ),
    val dishesAmountDb: List<DishAmountDb> = listOf(),
    val tableDb: TableDb = TableDb(-1, ""),
    var isSelected: Boolean = false,
)