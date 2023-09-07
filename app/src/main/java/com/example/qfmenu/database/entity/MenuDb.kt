package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class MenuDb(
    @PrimaryKey(autoGenerate = true)
    val menuId: Long,
    val name: String,
    val isUsed: Int,
)

data class MenuWithCategories(
    @Embedded val menuDb: MenuDb,
    @Relation(
        parentColumn = "menuId",
        entityColumn = "categoryId",
        associateBy = Junction(CategoryMenuCrossRef::class)
    )
    val categoriesDb: List<CategoryDb>
)
