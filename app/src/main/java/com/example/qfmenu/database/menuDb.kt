package com.example.qfmenu.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "menuDb")
data class menuDb(
    @PrimaryKey(autoGenerate = true)
    val menuId: Long,
    val name: String
)

data class MenuWithCategories(
    @Embedded val menuDb: menuDb,
    @Relation(
        parentColumn = "menuId",
        entityColumn = "categoryId",
        associateBy = Junction(CategoryMenuCrossRef::class)
    )
    val categories: List<CategoryDb>
)
