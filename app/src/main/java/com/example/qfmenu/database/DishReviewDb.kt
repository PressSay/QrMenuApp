package com.example.qfmenu.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishReviewDb")
data class DishReviewDb(
    val reviewId: Long,
    val dishId: Long
)
