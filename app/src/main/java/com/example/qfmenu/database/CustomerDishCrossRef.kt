package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customerDishDb", primaryKeys = ["customerId", "dishId"])
data class CustomerDishCrossRef(
    val customerId: Long,
    val dishId: Long,
    val isReviewed: Int,
    val amount: Int,
    val promotion: Float
)
