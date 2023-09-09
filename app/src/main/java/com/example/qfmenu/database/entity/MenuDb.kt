package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class MenuDb(
    @PrimaryKey
    val menuNameId: String,
    val isUsed: Int,
)

data class MenuWithCategories(
    @Embedded val menuDb: MenuDb,
    @Relation(
        parentColumn = "menuNameId",
        entityColumn = "categoryNameId",
        associateBy = Junction(CategoryMenuCrossRef::class)
    )
    val categoriesDb: List<CategoryDb>
)
