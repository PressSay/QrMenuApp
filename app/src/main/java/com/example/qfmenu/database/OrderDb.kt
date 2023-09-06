package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orderDb",)
data class OrderDb(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long,
    val customerId: Long,
    val status: String,
    val tableId: Int,
    val payments: String,
    val promotion: Float,
)
