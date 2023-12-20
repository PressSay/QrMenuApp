package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CustomerDao
import com.example.qfmenu.database.dao.CustomerDishDao
import com.example.qfmenu.database.dao.OrderDao
import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.network.entity.Customer
import com.example.qfmenu.repository.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val customerDao: CustomerDao,
    private val customerDishDao: CustomerDishDao,
    private val reviewDao: ReviewDao,
    private val orderDao: OrderDao,
    private val selectedDishes: List<DishAmountDb>,
) : ViewModel() {
    fun getCustomerDishCrossRefDao(): CustomerDishDao {
        return customerDishDao
    }

    private var _customerForCreate: CustomerDb? = null

    val customerList get() = customerDao.getCustomersUnConfirmed()

    fun getCustomerListByTableId(tableId: Long): LiveData<List<CustomerDb>> {
        return customerDao.getCustomersByTableId(tableId)
    }

    suspend fun getOrder(customerId: Long): OrderDb {
        return orderDao.getOrderCustomerOwner(customerId)
    }

    suspend fun getCustomerDishes(customerId: Long): List<CustomerDishDb> {
        return customerDao.getCustomerDishCrossRefs(customerId)
    }

    fun getCustomersByCalendar(calendarPaid: String): LiveData<List<CustomerDb>> {
        return customerDao.getCustomersByCalendar(calendarPaid)
    }

    fun createCustomer(customerDb: CustomerDb) {
        _customerForCreate = customerDb
    }

    fun insertCustomer(
        customerDb: CustomerDb,
        payment: String,
        status: String,
        promotion: Byte,
        tableId: Long,
        customerRepository: CustomerRepository
    ) {
        viewModelScope.launch {
            val customerIdCreated = customerDao.insert(
                customerDb
            )
            orderDao.insert(
                OrderDb(
                    customerOwnerId = customerIdCreated,
                    tableId = tableId,
                    payments = payment,
                    status = status,
                    promotion = promotion
                )
            )
            selectedDishes.forEach { dishAmountDb ->
                customerDishDao.insert(
                    CustomerDishDb(
                        customerId = customerIdCreated,
                        dishId = dishAmountDb.dishDb.dishId,
                        amount = dishAmountDb.amount,
                        promotion = 0
                    )
                )
            }
            try {
                customerRepository.createCustomer(customerDb.copy(customerId = customerIdCreated))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun deleteCustomer(customerDb: CustomerDb, customerRepository: CustomerRepository) {
        viewModelScope.launch {
//
//            async(Dispatchers.IO) { customerDao.getCustomerDishCrossRefs(customerDb.customerId) }.await()
//                .forEach { customerDishCrossRef ->
//                    customerDishDao.delete(customerDishCrossRef)
//                }
//
//            val orderDeleteDb =
//                async(Dispatchers.IO) { customerDao.getCustomerAndOrder(customerDb.customerId) }.await().orderDb
//
//            orderDao.delete(orderDeleteDb)
//            customerDao.delete(customerDb)
            try {
                customerRepository.deleteCustomerNet(customerDb.customerId)
                customerRepository.deleteCustomer(customerDb)
            } catch (e: Exception) {
                customerRepository.deleteCustomer(customerDb)
            }
        }
    }

    suspend fun getCustomer(customerId: Long): CustomerDb {
        return customerDao.getCustomer(customerId)!!
    }

}

class CustomerViewModelFactory(
    private val customerDao: CustomerDao,
    private val customerDishDao: CustomerDishDao,
    private val reviewDao: ReviewDao,
    private val orderDao: OrderDao,
    private val selectedDishes: List<DishAmountDb>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerViewModel(
                customerDao,
                customerDishDao,
                reviewDao,
                orderDao,
                selectedDishes
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

