package com.example.qfmenu.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InvestmentDb(
    @PrimaryKey
    val investmentName: String,
    val cost: Int
)
