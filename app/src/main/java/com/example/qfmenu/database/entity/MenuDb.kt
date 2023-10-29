package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class MenuDb(
    @PrimaryKey(autoGenerate = true)
    val menuId: Long = 0,
    val name: String,
    var isUsed: Boolean,
)

data class MenuWithCategories(
    @Embedded val menuDb: MenuDb,
    @Relation(
        parentColumn = "menuId",
        entityColumn = "menuId",
    )
    val categoriesDb: List<CategoryDb>
)
