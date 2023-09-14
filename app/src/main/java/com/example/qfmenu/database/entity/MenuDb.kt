package com.example.qfmenu.database.entity

import android.text.Editable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class MenuDb(
    @PrimaryKey(autoGenerate = true)
    val menuId: Long = 0,
    val menuName: String,
    var isUsed: Boolean,
)

data class MenuWithCategories(
    @Embedded val menuDb: MenuDb,
    @Relation(
        parentColumn = "menuId",
        entityColumn = "menuCreatorId",
    )
    val categoriesDb: List<CategoryDb>
)
