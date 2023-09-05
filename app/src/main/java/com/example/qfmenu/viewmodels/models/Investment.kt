package com.example.qfmenu.viewmodels.models

data class Investment(
    val name: String,
    val cost: Int,
    var isDropdown: Boolean = false
)
