package com.example.qfmenu.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accountDb")
data class AccountDb(
    @PrimaryKey(autoGenerate = true)
    val accountId: Long,
    val name: String,
    val phoneNumber: String,
    val level: Int,
    val email: String,
    val password: String,
    val address: String,
    val avatar: String,
)
