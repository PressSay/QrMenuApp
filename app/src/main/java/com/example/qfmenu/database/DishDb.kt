package com.example.qfmenu.database

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishDb")
data class DishDb(
    @PrimaryKey(autoGenerate = true)
    val DishId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "cost")
    val cost: Float,
    @ColumnInfo(name = "categoryId")
    val categoryId: Int,
    @ColumnInfo(name = "countTimes")
    val countTimes: Int,
    @ColumnInfo(name = "img")
    val img: String
)