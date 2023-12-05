package com.example.qfmenu.repository

import android.util.Log
import com.example.qfmenu.database.dao.CustomerDao
import com.example.qfmenu.database.dao.CustomerDishDao
import com.example.qfmenu.database.dao.OrderDao
import com.example.qfmenu.database.entity.CustomerDb
import com.example.qfmenu.database.entity.CustomerDishDb
import com.example.qfmenu.database.entity.OrderDb
import com.example.qfmenu.network.NetworkRetrofit
import com.example.qfmenu.network.entity.Customer
import org.json.JSONArray
import org.json.JSONObject


class CustomerRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val customerDao: CustomerDao,
    private val customerDishDao: CustomerDishDao,
    private val orderDao: OrderDao
) {
    suspend fun createCustomerDb(customer: Customer) {
        customerDao.insert(
            CustomerDb(
                customerId = customer.customerId,
                accountCreatorId = customer.userId,
                dateExpireCode = customer.dateExpireCode,
                name = customer.name,
                code = customer.code,
                phone = customer.phoneNumber,
                address = customer.address,
                created = customer.created_at.split(" ")[0]
            )
        )
        orderDao.insert(
            OrderDb(
                orderId = customer.order.customerId,
                customerOwnerId = customer.order.customerId,
                tableId = customer.order.nameTable.toLong(),
                status = customer.order.status,
                payments = customer.order.payments,
                promotion = customer.order.promotion
            )
        )
        customerDishDao.insertAll(customer.customerDishCrossRefs.map { customerDish ->
            CustomerDishDb(
                customerId = customer.customerId,
                dishId = customerDish.dishId,
                amount = customerDish.amount,
                promotion = customerDish.promotion
            )
        })
    }

    suspend fun createCustomer(customerDb: CustomerDb) {
        val orderDb = orderDao.getOrderCustomerOwner(customerDb.customerId)
        val dishes = customerDishDao.getListByCustomerId(customerDb.customerId)

        val arrDish = JSONArray()
        for (dish in dishes) {
            val jsonDish = JSONObject()
            jsonDish.put("dishId", dish.dishId)
            jsonDish.put("amount", dish.amount)
            jsonDish.put("promotion", dish.promotion)
            arrDish.put(jsonDish)
        }


        val reponse = networkRetrofit.customer().create(
            userId = customerDb.accountCreatorId,
            name = customerDb.name,
            code = customerDb.code,
            address = customerDb.address,
            phoneNumber = customerDb.phone,
            dishes = arrDish.toString(),
            promotion = orderDb.promotion,
            statusOrder = orderDb.status,
            payments = orderDb.payments,
            tableId = orderDb.tableId.toString()
        )
        Log.d("CustomerRepository", arrDish.toString() + " " + reponse.code())
    }


    suspend fun updateCustomerNet(customerDb: CustomerDb) {
        val orderDb = orderDao.getOrderCustomerOwner(customerDb.customerId)
        val dishes = customerDishDao.getListByCustomerId(customerDb.customerId)

        val arrDish = JSONArray()
        for (dish in dishes) {
            val jsonDish = JSONObject()
            jsonDish.put("dishId", dish.dishId)
            jsonDish.put("amount", dish.amount)
            jsonDish.put("promotion", dish.promotion)
            arrDish.put(jsonDish)
        }

        val response = networkRetrofit.customer().update(
            id = customerDb.customerId.toString(),
            name = customerDb.name,
            code = customerDb.code,
            phoneNumber = customerDb.phone,
            address = customerDb.address,
            dishes = arrDish.toString(),
            promotion = orderDb.promotion,
            statusOrder = orderDb.status,
            payments = orderDb.payments
        )

        Log.d("CustomerRepository", arrDish.toString() + " " + response.code())
    }

    suspend fun updateCustomer(customerNC: Customer) {
        customerDao.update(
            CustomerDb(
                customerId = customerNC.customerId,
                accountCreatorId = customerNC.userId,
                dateExpireCode = customerNC.dateExpireCode,
                name = customerNC.name,
                code = customerNC.code,
                phone = customerNC.phoneNumber,
                address = customerNC.address,
                created = customerNC.created_at.split(" ")[0]
            )
        )
        orderDao.update(
            OrderDb(
                orderId = customerNC.order.customerId,
                customerOwnerId = customerNC.order.customerId,
                tableId = customerNC.order.nameTable.toLong(),
                status = customerNC.order.status,
                payments = customerNC.order.payments,
                promotion = customerNC.order.promotion
            )
        )
        customerDishDao.updateAll(
            customerNC.customerDishCrossRefs.map { customerDish ->
                CustomerDishDb(
                    customerId = customerNC.customerId,
                    dishId = customerDish.dishId,
                    amount = customerDish.amount,
                    promotion = customerDish.promotion
                )
            }
        )
    }

    suspend fun deleteCustomerNet(customer: Customer) {
        networkRetrofit.customer().delete(customer.customerId.toString())
    }

    suspend fun deleteCustomer(customerDb: CustomerDb) {
        customerDao.delete(customerDb)
        orderDao.delete(orderDao.getOrderCustomerOwner(customerDb.customerId))
        customerDishDao.deleteAll(customerDishDao.getListByCustomerId(customerDb.customerId))
    }

    suspend fun resetKey() {
        if (customerDao.getLastCustomer() == null) {
            customerDao.resetKey()
        }
        if (orderDao.getLastOrder() == null) {
            orderDao.resetKey()
        }
        customerDao.resetLastKey()
        orderDao.resetLastKey()
    }

    suspend fun fetchCustomer(isSync: Boolean) {
        val customerListNet = networkRetrofit.customer().findALl()
        val customerDbList = customerDao.getAllCustomer()
        if (!customerListNet.isSuccessful) {
            return
        }
        // ham let ko thuoc pham vi cua ham if
        val customerSr = customerListNet.body()!!
        val lastKey = customerDao.getLastKey()
        if (customerSr.isNotEmpty() && lastKey >= customerSr.last().customerId && isSync) {
            // through all customer in server
            for (customer in customerSr) {
                try {
                    val customerDb = customerDao.getCustomer(customer.customerId)
                    if (customerDb == null) {
                        deleteCustomerNet(customer)
                    } else {
                        updateCustomerNet(customerDb)
                    }
                } catch (e: Exception) {
                    Log.e("CustomerRepository", customer.toString())
                }
            }
            // through all customer in local
            for (i in customerDbList.indices) {
                val customerDb = customerDbList[i]
                val customerNetCur =
                    networkRetrofit.customer().findOne(customerDb.customerId.toString())
                if (!customerNetCur.isSuccessful) {
                    this.deleteCustomer(customerDb)
                } else {
                    val customerNet = customerNetCur.body()!!
                    this.updateCustomer(customerNet)
                }
            }
            this.resetKey()
        }
        if (customerSr.isNotEmpty() && (!isSync ||  lastKey < customerSr.last().customerId)) {
            // problem it can be same id, need convert id local to last id in server
            for (customerDb in customerDbList) {
                if (!isSync) {
                    this.createCustomer(customerDb)
                }
                this.deleteCustomer(customerDb)
            }
            this.resetKey()
            customerDao.insertAll(customerSr.map { customerIntegration ->
                CustomerDb(
                    customerId = customerIntegration.customerId,
                    accountCreatorId = customerIntegration.userId,
                    dateExpireCode = customerIntegration.dateExpireCode,
                    name = customerIntegration.name,
                    code = customerIntegration.code,
                    phone = customerIntegration.phoneNumber,
                    address = customerIntegration.address,
                    created = customerIntegration.created_at.split(" ")[0]
                )
            })
            orderDao.insertAll(customerSr.map { customerIntegration ->
                OrderDb(
                    orderId = customerIntegration.order.customerId,
                    customerOwnerId = customerIntegration.order.customerId,
                    tableId = customerIntegration.order.nameTable.toLong(),
                    status = customerIntegration.order.status,
                    payments = customerIntegration.order.payments,
                    promotion = customerIntegration.order.promotion
                )
            })
            customerDishDao.insertAll(customerSr.flatMap { customerIntegration ->
                customerIntegration.customerDishCrossRefs.map { customerDish ->
                    CustomerDishDb(
                        customerId = customerIntegration.customerId,
                        dishId = customerDish.dishId,
                        amount = customerDish.amount,
                        promotion = customerDish.promotion
                    )
                }
            })
        }
    }
}