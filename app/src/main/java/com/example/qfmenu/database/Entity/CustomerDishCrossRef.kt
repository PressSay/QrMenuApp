package com.example.qfmenu.database.Entity

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["customerId", "dishId"])
data class CustomerDishCrossRef(
    val customerId: Long,
    val dishId: Long,
    val reviewCreatorId: Long,
    val amount: Int,
    val promotion: Float
)
