package com.example.qfmenu.network.entity

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?,
    val created_at: String,
    val updated_at: String,
    val level: Int,
    val phoneNumber: String,
    val exp: Int,
    val address: String,
    val nameRole: String?,
    val image: Image
)