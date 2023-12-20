package com.example.qfmenu.network.entity

data class RevBill(
    val customerId: Long,
    val star: Int,
    val description: String
)

data class RevDish (
    val customerId: Long,
    val dishId: Long,
    val description: String,
    val star: Int
)
