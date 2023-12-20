package com.example.qfmenu.database.entity

import androidx.room.Entity


@Entity(primaryKeys = ["customerId", "dishId"])
data class ReviewDishDb(
    val customerId: Long,
    val dishId: Long,
    val isThumbUp: Int? = null,
    val description: String? = null,
)