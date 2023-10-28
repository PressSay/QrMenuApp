package com.example.qfmenu.network.entity

data class Dish(
    val categoryId: Long,
    val cost: String,
    val deleted_at: String?,
    val description: String,
    val dishId: Long,
    val imageDish: ImageDish,
    val name: String,
    val numberOfTimesCalled: Int
)