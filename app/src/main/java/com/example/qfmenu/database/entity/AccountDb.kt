package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class AccountDb(
    @PrimaryKey(autoGenerate = true)
    val accountId: Long = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val level: Int,
    val password: String,
    val address: String,
    val avatar: String,
    val roleCreatorId: String,
)

data class AccountWithCustomers(
    @Embedded val accountDb: AccountDb,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountCreatorId",
    )
    val customersDb: List<CustomerDb>
)