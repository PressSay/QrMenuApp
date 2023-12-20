package com.example.qfmenu.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DishDb(
    @PrimaryKey(autoGenerate = true)
    val dishId:Long = 0,
    val name: String,
    val categoryId: Long,
    val description: String,
    val cost: Int,
    val numberOfTimesCalled: Int = 0,
    val image: String = "Empty"
)