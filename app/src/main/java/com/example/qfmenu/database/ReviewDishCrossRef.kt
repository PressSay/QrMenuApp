package com.example.qfmenu.database

import androidx.room.Entity

@Entity(tableName = "reviewDishCrossRef", primaryKeys = ["dishId", "reviewId"])
data class ReviewDishCrossRef(
    val dishId: Long,
    val reviewId: Long
)
