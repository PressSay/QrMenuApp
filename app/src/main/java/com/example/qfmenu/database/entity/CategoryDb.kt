package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class CategoryDb(
    @PrimaryKey
    val categoryNameId: String,
)

data class CategoryWithMenus(
    @Embedded val categoryDb: CategoryDb,
    @Relation(
        parentColumn = "categoryNameId",
        entityColumn = "menuNameId",
        associateBy = Junction(CategoryMenuCrossRef::class)
    )
    val menusDb: List<MenuDb>
)

data class CategoryWidthDishes(
    @Embedded val categoryDb: CategoryDb,
    @Relation(
        parentColumn = "categoryNameId",
        entityColumn = "categoryCreatorId"
    )
    val dishesDb: List<DishDb>
)

