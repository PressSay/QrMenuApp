package com.example.qfmenu.network.entity

data class ReviewDish(
    val review: Review,
    val dish: Dish,
    val customerId: Long,
)