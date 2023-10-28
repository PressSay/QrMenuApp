package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class TableDb(
    @PrimaryKey
    val tableId: Long,
    val status: String
)

data class TableWithOrders(
    @Embedded val tableDb: TableDb,
    @Relation(
        parentColumn = "tableId",
        entityColumn = "tableId"
    )
    val ordersDb: List<OrderDb>
)
