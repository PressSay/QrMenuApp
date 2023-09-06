package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tableDb")
data class tableDb(
    @PrimaryKey
    val tableId: Int,
    val status: String
)
