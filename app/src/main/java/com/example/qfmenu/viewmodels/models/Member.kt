package com.example.qfmenu.viewmodels.models

import androidx.annotation.DrawableRes

data class Member(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val address: String,
    val position: String,
    @DrawableRes val image: Int,
)
