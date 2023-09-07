package com.example.qfmenu.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["customerId", "dishId"])
data class CustomerDishCrossRef(
    val customerId: Long,
    val dishId: Long,
    val reviewCreatorId: Long,
    val amount: Int,
    val promotion: Float
)
