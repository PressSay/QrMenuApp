package com.example.qfmenu.network.entity

data class Customer(
    val address: String,
    val code: String,
    val created_at: String,
    val customerId: Long,
    val dateExpireCode: String,
    val name: String,
    val phoneNumber: String,
    val userId: Long
)