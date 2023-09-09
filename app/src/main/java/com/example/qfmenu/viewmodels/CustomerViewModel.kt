package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.CustomerDao
import com.example.qfmenu.database.dao.CustomerDishCrossRefDao
import com.example.qfmenu.database.dao.OrderDao
import com.example.qfmenu.database.dao.ReviewCustomerCrossRefDao
import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.database.entity.ReviewCustomerCrossRef
import com.example.qfmenu.database.entity.ReviewDb
import com.example.qfmenu.database.entity.TableDb
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CustomerViewModel(
    private val customerDao: CustomerDao,
    private val customerDishCrossRefDao: CustomerDishCrossRefDao,
    private val reviewDao: ReviewDao,
    private val reviewCustomerCrossRefDao: ReviewCustomerCrossRefDao,
    private val orderDao: OrderDao,
    private val selectedDishes: List<DishAmountDb>,
) : ViewModel() {
    val reviews get() = reviewDao.getReviews().asLiveData()
    fun getReview(reviewId: Long): LiveData<ReviewDb> {
        return reviewDao.getReview(reviewId).asLiveData()
    }

    private var _customerForCreate: CustomerDb? = null
    val customerForCreate get() = _customerForCreate!!

    val customerList get() = customerDao.getCustomers().asLiveData()

    fun getCustomerDishes(customerId: Long): LiveData<List<CustomerDishCrossRef>> {
        return customerDao.getCustomerDishCrossRefs(customerId).asLiveData()
    }

    fun createCustomer(customerDb: CustomerDb) {
        _customerForCreate = customerDb
    }

    fun insertReview(reviewDb: ReviewDb) {
        viewModelScope.launch {
            reviewDao.insert(reviewDb)
        }
    }

    fun insertReviewDishCrossRef(
        customerDishes: List<CustomerDishCrossRef>,
        reviewsDb: List<ReviewDb>
    ) {
        viewModelScope.launch {
            customerDishes.forEachIndexed { index, it ->
                customerDishCrossRefDao.update(
                    CustomerDishCrossRef(
                        it.customerId,
                        it.dishNameId,
                        reviewsDb[index].reviewId,
                        it.amount,
                        it.promotion
                    )
                )
                reviewDao.insert(reviewsDb[index])
            }
        }
    }

    fun insertReviewCustomer(customerDb: CustomerDb, reviewDb: ReviewDb) {
        viewModelScope.launch {
            reviewCustomerCrossRefDao.insert(
                ReviewCustomerCrossRef(
                    reviewDb.reviewId,
                    customerDb.customerId
                )
            )
            reviewDao.insert(reviewDb)
        }
    }

    fun insertCustomer(customerDb: CustomerDb, payment: String, status: String, promotion: Byte, tableDb: TableDb?) {
        viewModelScope.launch {
            selectedDishes.forEach { dishAmountDb ->
                customerDishCrossRefDao.insert(
                    CustomerDishCrossRef(
                        customerDb.customerId,
                        dishAmountDb.dishDb.dishNameId,
                        -1,
                        dishAmountDb.amount,
                        0
                    )
                )
            }
            customerDao.insert(
                customerDb
            )
            orderDao.insert(
                OrderDb(
                    customerDb.customerId,
                    customerDb.customerId,
                    tableDb?.tableId ?: -1,
                    payment,
                    status,
                    promotion
                )
            )
        }
    }

    fun updateOrder(customerDb: CustomerDb, orderDb: OrderDb) {
        viewModelScope.launch {
            customerDao.getCustomerAndOrder(customerDb.customerId).collect() {
                orderDao.update(
                    it.orderDb
                )
            }
        }
    }

    fun deleteCustomerDish(customerDishCrossRef: CustomerDishCrossRef) {
        viewModelScope.launch {
            customerDishCrossRefDao.delete(customerDishCrossRef)
        }
    }

    fun deleteCustomer(customerDb: CustomerDb) {
        viewModelScope.launch {
            customerDao.getCustomerDishCrossRefs(customerDb.customerId).collect() { customerDishes ->
                customerDishes.forEach {
                    customerDishCrossRefDao.delete(it)
                }
            }
            reviewCustomerCrossRefDao.getCustomerReview(customerDb.customerId).collect() {
                reviewCustomerCrossRefDao.update(
                    ReviewCustomerCrossRef(
                        it.reviewId,
                        -1
                    )
                )
            }
            customerDao.getCustomerAndOrder(customerDb.customerId).collect() {
                orderDao.delete(it.orderDb)
            }
            customerDao.delete(customerDb)
        }
    }

    fun getCustomer(customerId: Long) {
        customerDao.getCustomer(customerId)
    }

}

class CustomerViewModelFactory(
    private val customerDao: CustomerDao,
    private val customerDishCrossRefDao: CustomerDishCrossRefDao,
    private val reviewDao: ReviewDao,
    private val reviewCustomerCrossRefDao: ReviewCustomerCrossRefDao,
    private val orderDao: OrderDao,
    private val selectedDishes: List<DishAmountDb>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerViewModel(
                customerDao,
                customerDishCrossRefDao,
                reviewDao,
                reviewCustomerCrossRefDao,
                orderDao,
                selectedDishes
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

