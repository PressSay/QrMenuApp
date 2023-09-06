package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviewDb")
data class ReviewDb (
    @PrimaryKey(autoGenerate = true)
    val reviewId: Int,
    val accountId: Int,
    val isThumbUp: Int,
    val description: String,
)