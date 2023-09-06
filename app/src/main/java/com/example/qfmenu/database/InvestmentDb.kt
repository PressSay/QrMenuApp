package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investmentDb")
data class InvestmentDb(
    @PrimaryKey(autoGenerate = true)
    val investmentId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "cost")
    val cost: Int
)
