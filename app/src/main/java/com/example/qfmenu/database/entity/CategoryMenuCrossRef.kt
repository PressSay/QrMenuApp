package com.example.qfmenu.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["categoryId", "menuId"])
data class CategoryMenuCrossRef(
    val categoryId: Long,
    val menuId: Long
)
