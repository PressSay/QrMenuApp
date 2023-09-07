package com.example.qfmenu.database.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InvestmentDb(
    @PrimaryKey(autoGenerate = true)
    val investmentId: Int,
    val name: String,
    val cost: Int
)
