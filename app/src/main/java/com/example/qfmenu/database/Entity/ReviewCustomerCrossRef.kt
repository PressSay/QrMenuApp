package com.example.qfmenu.database.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReviewCustomerCrossRef(
    @PrimaryKey val reviewId: Long,
    val customerId: Long
)
