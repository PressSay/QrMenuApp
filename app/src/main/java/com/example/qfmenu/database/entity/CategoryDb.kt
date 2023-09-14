package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class CategoryDb(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val categoryName: String,
    val menuCreatorId: Long,
)

data class CategoryWidthDishes(
    @Embedded val categoryDb: CategoryDb,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryCreatorId"
    )
    val dishesDb: List<DishDb>
)

