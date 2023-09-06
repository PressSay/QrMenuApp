package com.example.qfmenu.database

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customerDishCrossRef", primaryKeys = ["customerId", "dishId"])
data class CustomerDishCrossRef(
    val customerId: Long,
    val dishId: Long,
    val reviewCreatorId: Long,
    val amount: Int,
    val promotion: Float
)
