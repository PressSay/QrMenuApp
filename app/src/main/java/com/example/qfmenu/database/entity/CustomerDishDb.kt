package com.example.qfmenu.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["customerId", "dishId"])
data class CustomerDishDb(
    val customerId: Long,
    val dishId: Long,
    val amount: Int,
    val promotion: Byte
)
