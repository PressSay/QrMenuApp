package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CustomerDao
import com.example.qfmenu.database.dao.CustomerDishDao
import com.example.qfmenu.database.dao.OrderDao
import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.entity.CustomerAndOrderDb
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.database.entity.ReviewCustomerDb
import com.example.qfmenu.database.entity.ReviewDb
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
    val reviews get() = reviewDao.getReviewListLiveData()
    fun getReview(reviewId: Long): LiveData<ReviewDb> {
        return reviewDao.getReviewLiveData(reviewId)
    }

    fun getCustomerDishCrossRefDao(): CustomerDishDao {
        return customerDishDao
    }

    private var _customerForCreate: CustomerDb? = null
    val customerForCreate get() = _customerForCreate!!

    val customerList get() = customerDao.getCustomersUnConfirmed()

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

    fun insertReview(reviewDb: ReviewDb) {
        viewModelScope.launch {
            reviewDao.insert(reviewDb)
        }
    }

//    fun insertReviewDishCrossRef(
//        customerDishes: List<CustomerDishDb>,
//        reviewsDb: List<ReviewDb>
//    ) {
//        viewModelScope.launch {
//            customerDishes.forEachIndexed { index, it ->
//                customerDishDao.update(
//                    CustomerDishDb(
//                        it.customerId,
//                        it.dishId,
//                        reviewsDb[index].reviewId,
//                        it.amount,
//                        it.promotion
//                    )
//                )
//                reviewDao.insert(reviewsDb[index])
//            }
//        }
//    }

    fun insertReviewCustomer(customerDb: CustomerDb, reviewDb: ReviewDb) {
        viewModelScope.launch {
            reviewDao.insertReviewCustomerCrossRef(
                ReviewCustomerDb(
                    reviewDb.reviewId,
                    customerDb.customerId
                )
            )
            reviewDao.insert(reviewDb)
        }
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
                        customerIdCreated,
                        dishAmountDb.dishDb.dishId,
                        dishAmountDb.amount,
                        0
                    )
                )
            }
            customerRepository.createCustomer(customerDb.copy(customerId = customerIdCreated))
        }

    }

    fun updateOrder(customerDb: CustomerDb, orderDb: OrderDb) {
        viewModelScope.launch {
            val customerAndOrder =
                async(Dispatchers.IO) { customerDao.getCustomerAndOrder(customerDb.customerId) }.await()
            orderDao.update(
                customerAndOrder.orderDb
            )

        }
    }

    fun deleteCustomerDish(customerDishDb: CustomerDishDb) {
        viewModelScope.launch {
            customerDishDao.delete(customerDishDb)
        }
    }

    fun deleteCustomer(customerDb: CustomerDb) {
        viewModelScope.launch {

            async(Dispatchers.IO) { customerDao.getCustomerDishCrossRefs(customerDb.customerId) }.await()
                .forEach { customerDishCrossRef ->
                    customerDishDao.delete(customerDishCrossRef)
                }

            val orderDeleteDb =
                async(Dispatchers.IO) { customerDao.getCustomerAndOrder(customerDb.customerId) }.await().orderDb

            orderDao.delete(orderDeleteDb)
            customerDao.delete(customerDb)

            val reviewCustomerCrossRef =
                async(Dispatchers.IO) { reviewDao.getCustomerReview(customerDb.customerId) }.await()
            reviewDao.updateReviewCustomerCrossRef(
                ReviewCustomerDb(
                    reviewCustomerCrossRef?.reviewId ?: -1,
                    -1
                )
            )

        }
    }

    suspend fun getCustomer(customerId: Long): CustomerDb {
        return customerDao.getCustomer(customerId)!!
    }

    suspend fun getAllCustomerAndOrder(): List<CustomerAndOrderDb> {
        return customerDao.getAllCustomerAndOrder()
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

