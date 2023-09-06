package com.example.qfmenu.database

import androidx.room.PrimaryKey

data class RoleDb(
    @PrimaryKey(autoGenerate = true)
    val roleId: Long,
    val expired: String,
    val name: String,
    val description: String,
)
