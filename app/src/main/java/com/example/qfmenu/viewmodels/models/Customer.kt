package com.example.qfmenu.viewmodels.models

import java.util.Date

data class Customer(
    val id: Int,
    val table: Table,
    val expireDate: Date,
    val selectedDishesList: List<Dish>,
    val oldCode: String,
    val newCode: String,
    val name: String,
    val phoneNumber: String,
    val address: String,
)
