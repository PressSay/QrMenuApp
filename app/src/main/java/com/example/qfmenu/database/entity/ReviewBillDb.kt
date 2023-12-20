package com.example.qfmenu.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReviewBillDb(
    @PrimaryKey
    val customerId: Long,
    val isThumbUp: Int,
    val description: String,
)
