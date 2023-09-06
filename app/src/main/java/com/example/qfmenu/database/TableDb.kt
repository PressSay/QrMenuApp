package com.example.qfmenu.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "tableDb")
data class TableDb(
    @PrimaryKey
    val tableId: Long,
    val status: String
)

data class TableAndOrders(
    @Embedded val tableDb: TableDb,
    @Relation(
        parentColumn = "tableId",
        entityColumn = "tableCreatorId"
    )
    val ordersDb: List<OrderDb>
)
