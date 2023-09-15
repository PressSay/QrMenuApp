package com.example.qfmenu.viewmodels

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
import com.example.qfmenu.database.entity.CustomerAndOrderDb
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishCrossRef
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.database.entity.ReviewCustomerCrossRef
import com.example.qfmenu.database.entity.ReviewDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    val customerList get() = customerDao.getCustomersUnConfirmed().asLiveData()

    suspend fun getOrder(customerId: Long): OrderDb {
        return orderDao.getOrderCustomerOwner(customerId)
    }

    suspend fun getCustomerDishes(customerId: Long): List<CustomerDishCrossRef> {
        return customerDao.getCustomerDishCrossRefs(customerId)
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
//        customerDishes: List<CustomerDishCrossRef>,
//        reviewsDb: List<ReviewDb>
//    ) {
//        viewModelScope.launch {
//            customerDishes.forEachIndexed { index, it ->
//                customerDishCrossRefDao.update(
//                    CustomerDishCrossRef(
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
            reviewCustomerCrossRefDao.insert(
                ReviewCustomerCrossRef(
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
        tableId: Long

    ) {

        viewModelScope.launch {

            val customerIdCreated = customerDao.insert(
                customerDb
            )
            orderDao.insert(
                OrderDb(
                    customerOwnerId = customerIdCreated,
                    tableCreatorId = tableId,
                    payments = payment,
                    status = status,
                    promotion = promotion
                )
            )
            selectedDishes.forEach { dishAmountDb ->
                customerDishCrossRefDao.insert(
                    CustomerDishCrossRef(
                        customerIdCreated,
                        dishAmountDb.dishDb.dishId,
                        dishAmountDb.amount,
                        0
                    )
                )
            }


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

    fun deleteCustomerDish(customerDishCrossRef: CustomerDishCrossRef) {
        viewModelScope.launch {
            customerDishCrossRefDao.delete(customerDishCrossRef)
        }
    }

    fun deleteCustomer(customerDb: CustomerDb) {
        viewModelScope.launch {

            async(Dispatchers.IO) { customerDao.getCustomerDishCrossRefs(customerDb.customerId) }.await()
                .forEach { customerDishCrossRef ->
                    customerDishCrossRefDao.delete(customerDishCrossRef)
                }

            val orderDeleteDb =
                async(Dispatchers.IO) { customerDao.getCustomerAndOrder(customerDb.customerId) }.await().orderDb

            orderDao.delete(orderDeleteDb)
            customerDao.delete(customerDb)

            val reviewCustomerCrossRef =
                async(Dispatchers.IO) { reviewCustomerCrossRefDao.getCustomerReview(customerDb.customerId) }.await()
            if (reviewCustomerCrossRef != null) {
                reviewCustomerCrossRefDao.update(
                    ReviewCustomerCrossRef(
                        reviewCustomerCrossRef.reviewId,
                        -1
                    )
                )
            }

        }
    }

    suspend fun getCustomer(customerId: Long): CustomerDb {
        return customerDao.getCustomer(customerId)
    }

    suspend fun getAllCustomerAndOrder(): List<CustomerAndOrderDb> {
        return customerDao.getAllCustomerAndOrder()
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

