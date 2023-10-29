package com.example.qfmenu.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["dishId", "reviewId"])
data class ReviewDishDb(
    val dishId: Long,
    val reviewId: Long,
    val customerId: Long = -1,
)
