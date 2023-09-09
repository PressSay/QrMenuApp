package com.example.qfmenu.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["customerId", "dishNameId"])
data class CustomerDishCrossRef(
    val customerId: Long,
    val dishNameId: String,
    val reviewCreatorId: Long,
    val amount: Long,
    val promotion: Byte
)
