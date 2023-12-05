package com.example.qfmenu.network.entity

data class Review(
    val description: String,
    val reviewId: Int,
    val star: Int
)

data class RevCusInter (
    val reviewCustomer: ReviewCustomer,
    val review: Review
)

data class RevDishInter(
    val reviewDish: ReviewDish,
    val review: Review
)


data class ReviewCustomer(
    val reviewId: Long,
    val customerId: Long,
)

data class ReviewDish(
    val reviewId: Long,
    val dishId: Long,
    val customerId: Long,
)