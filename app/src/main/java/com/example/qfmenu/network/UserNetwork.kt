package com.example.qfmenu.network

data class UserNetwork(
    val Exp: Int,
    val address: String,
    val created_at: String,
    val email: String,
    val email_verified_at: String?,
    val id: Int,
    val level: Int,
    val name: String,
    val nameRole: String?,
    val phoneNumber: String,
    val updated_at: String
)