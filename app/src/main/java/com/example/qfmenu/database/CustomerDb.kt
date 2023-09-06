package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Calendar

@Entity(tableName = "customerDb")
data class CustomerDb(
    @PrimaryKey(autoGenerate = true)
    val customerId: Int,
    val expired: Calendar,
    val name: String,
    val code: String,
    val phone: String,
    val address: String,
)

data class CustomerAndOrderDb(
    @Embedded val customerDb: CustomerDb,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "orderId"
    )
    val orderDb: OrderDb
)
