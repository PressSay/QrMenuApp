package com.example.qfmenu.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReviewCustomerDb(
    @PrimaryKey val reviewId: Long,
    val customerId: Long
)
