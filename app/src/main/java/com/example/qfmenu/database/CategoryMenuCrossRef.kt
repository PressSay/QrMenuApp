package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "categoryMenuCrossRef", primaryKeys = ["categoryId", "menuId"])
data class CategoryMenuCrossRef(
    val categoryId: Long,
    val menuId: Long
)
