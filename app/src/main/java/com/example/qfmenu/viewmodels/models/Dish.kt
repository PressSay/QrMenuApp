package com.example.qfmenu.viewmodels.models

import androidx.annotation.DrawableRes

data class Dish(
    @DrawableRes val imgResourceId: Int,
    val title: String,
    val description: String,
    val cost: Int,
    var amount: Int,
    var selected: Boolean = false,
    val isReview: Boolean = false,
    val imgUrl: String = "empty",
    val id: Int
)