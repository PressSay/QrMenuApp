package com.example.qfmenu.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["customerId", "dishId"])
data class CustomerDishCrossRef(
    val customerId: Long,
    val dishId: Long,
    val amount: Long,
    val promotion: Byte
)
