package com.example.qfmenu.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderDb(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val customerOwnerId: Long,
    val tableId: Long = -1,
    val status: String,
    val payments: String,
    val promotion: Byte,
)
