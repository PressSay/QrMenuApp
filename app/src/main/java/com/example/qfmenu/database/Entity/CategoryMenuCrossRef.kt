package com.example.qfmenu.database.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["categoryId", "menuId"])
data class CategoryMenuCrossRef(
    val categoryId: Long,
    val menuId: Long
)
