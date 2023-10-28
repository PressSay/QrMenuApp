package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class CategoryDb(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val name: String,
    val menuId: Long,
)

data class CategoryWidthDishes(
    @Embedded val categoryDb: CategoryDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val dishesDb: List<DishDb>
)

