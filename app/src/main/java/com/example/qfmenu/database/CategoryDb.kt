package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "categoryDb")
data class CategoryDb(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int,
    @ColumnInfo(name = "name")
    val name: String,
)

data class CategoryWithMenus(
    @Embedded val categoryDb: CategoryDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "menuId",
        associateBy = Junction(CategoryMenuCrossRef::class)
    )
    val menusDb: List<MenuDb>
)

data class CategoryWidthDishes(
    @Embedded val categoryDb: CategoryDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryCreatorId"
    )
    val dishesDb: List<DishDb>
)

