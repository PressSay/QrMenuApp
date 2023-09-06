package com.example.qfmenu.database

import androidx.room.Entity

@Entity(tableName = "dishReviewCrossRef", primaryKeys = ["reviewId", "dishId"])
data class DishReviewCrossRef(
    val reviewId: Long,
    val dishId: Long
)
