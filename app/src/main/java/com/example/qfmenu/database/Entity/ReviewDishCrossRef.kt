package com.example.qfmenu.database.Entity

import androidx.room.Entity

@Entity(primaryKeys = ["dishId", "reviewId"])
data class ReviewDishCrossRef(
    val dishId: Long,
    val reviewId: Long
)
