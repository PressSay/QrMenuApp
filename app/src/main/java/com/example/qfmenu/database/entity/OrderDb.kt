package com.example.qfmenu.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderDb(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long,
    val customerOwnerId: Long,
    val tableCreatorId: Long,
    val status: String,
    val tableId: Int,
    val payments: String,
    val promotion: Float,
)
