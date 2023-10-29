package com.example.qfmenu.network.entity

data class Order(
    val customerId: Long,
    val nameTable: Int,
    val payments: String,
    val promotion: Byte,
    val status: String
)