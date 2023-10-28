package com.example.qfmenu.network.entity

data class CustomerDish(
    val amount: Int,
    val customerId: Long,
    val dishId: Long,
    val promotion: Int
)