package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class AccountDb(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val exp: Int,
    val phoneNumber: String,
    val level: Int,
    val password: String,
    val address: String,
    val avatar: String,
    val nameRole: String,
)

data class AccountWithCustomers(
    @Embedded val accountDb: AccountDb,
    @Relation(
        parentColumn = "id",
        entityColumn = "accountCreatorId",
    )
    val customersDb: List<CustomerDb>
)