package com.example.qfmenu.network.entity

data class CustomerIntegration(
    val customer: Customer,
    val customerDish: List<CustomerDish>,
    val order: Order
)