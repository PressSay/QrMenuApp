package com.example.qfmenu.viewmodels.models

import androidx.annotation.DrawableRes

data class Dish(
    @DrawableRes val imgResourceId: Int,
    val title: String,
    val description: String,
    val cost: String,
    var amount: Int,
    var selected: Boolean = false,
    val isReview: Boolean = false,
)