package com.example.qfmenu.network.entity

import com.google.gson.annotations.SerializedName


data class CustomerDishId(
    val dishId: Long,
    val amount: Int,
    val promotion: Byte
)
data class CustomerDishCrossRefs(
    val customerId: Long,
    val dishId: Long,
    val amount: Int,
    val promotion: Byte
)
data class Customer (
    val customerId: Long,
    val userId: Long,
    val dateExpireCode: String,
    val name: String,
    val code: String,
    val phoneNumber: String,
    val address: String,
    val created_at: String,
    val order: Order,
    val customerDishCrossRefs: ArrayList<CustomerDishCrossRefs>
)

data class Order(
    val customerId: Long,
    val nameTable: Int,
    val payments: String,
    val promotion: Byte,
    val status: String
)

data class CustomerUpdate(
    @SerializedName("id")
    val customerId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("dateExpireCode")
    val dateExpireCode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("order")
    val order: Order,
    @SerializedName("dishes")
    val dishes: List<CustomerDishId>
)
