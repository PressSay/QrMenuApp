package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.CustomerDao
import com.example.qfmenu.database.dao.CustomerDishDao
import com.example.qfmenu.database.dao.OrderDao
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.network.entity.CustomerDishId

class CustomerRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val customerDao: CustomerDao,
    private val customerDishDao: CustomerDishDao,
    private val orderDao: OrderDao
) {
    suspend fun createListCustomer(customerDbList: List<CustomerDb>) {
        customerDbList.forEach { customerDb ->
            val orderDb = orderDao.getOrderCustomerOwner(customerDb.customerId)
            val customerDishDb = customerDishDao.getListByCustomerId(customerDb.customerId)
            val customerNet = networkRetrofit.customer().create(
                userId = customerDb.accountCreatorId,
                name = customerDb.name,
                code = customerDb.code,
                phoneNumber = customerDb.phone,
                address = customerDb.address,
                dishes = customerDishDb.map {
                    CustomerDishId(
                        dishId = it.dishId,
                        amount = it.amount
                    )
                },
                promotion = orderDb.promotion,
                statusOrder = orderDb.status,
                payments = orderDb.payments,
                tableId = orderDb.tableId.toString()
            )
        }
    }

    suspend fun updateListCustomer(customerDbList: List<CustomerDb>) {
        customerDbList.forEach { customerDb ->
            val orderDb = orderDao.getOrderCustomerOwner(customerDb.customerId)
            val customerDishDb = customerDishDao.getListByCustomerId(customerDb.customerId)
            val customerNet = networkRetrofit.customer().update(
                id = customerDb.customerId.toString(),
                name = customerDb.name,
                code = customerDb.code,
                phoneNumber = customerDb.phone,
                address = customerDb.address,
                dishes = customerDishDb.map {
                    CustomerDishId(
                        dishId = it.dishId,
                        amount = it.amount
                    )
                },
                promotion = orderDb.promotion,
                statusOrder = orderDb.status,
                payments = orderDb.payments
            )
        }
    }

    suspend fun deleteListCustomer(customerDbList: List<CustomerDb>) {
        customerDbList.forEach { customerDb ->
            val customerNet = networkRetrofit.customer().delete(customerDb.customerId.toString())
        }
    }

    suspend fun fetchCustomer() {
        val customerNet = networkRetrofit.customer().findALl()
        if (customerNet.isSuccessful) {
            customerNet.body()?.let {
                customerDao.insertAll(it.map { customerIntegration ->
                    CustomerDb(
                        customerId = customerIntegration.customer.customerId,
                        accountCreatorId = customerIntegration.customer.userId,
                        dateExpireCode = customerIntegration.customer.dateExpireCode,
                        name = customerIntegration.customer.name,
                        code = customerIntegration.customer.code,
                        phone = customerIntegration.customer.phoneNumber,
                        address = customerIntegration.customer.address,
                        created = customerIntegration.customer.created_at
                    )
                })
                orderDao.insertAll(it.map { customerIntegration ->
                    OrderDb(
                        orderId = customerIntegration.order.customerId,
                        customerOwnerId = customerIntegration.order.customerId,
                        tableId = customerIntegration.order.nameTable.toLong(),
                        status = customerIntegration.order.status,
                        payments = customerIntegration.order.payments,
                        promotion = customerIntegration.order.promotion
                    )
                })
                it.forEach { customerIntegration ->
                    customerDishDao.insertAll(customerIntegration.customerDish.map { customerDish ->
                        CustomerDishDb(
                            customerId = customerIntegration.customer.customerId,
                            dishId = customerDish.dishId,
                            amount = customerDish.amount,
                            promotion = customerDish.promotion
                        )
                    })
                }
            }
        }
    }
}