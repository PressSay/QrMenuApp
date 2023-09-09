package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class AccountDb(
    @PrimaryKey(autoGenerate = true)
    val accountId: Long,
    val roleCreatorId: String,
    val name: String,
    val phoneNumber: String,
    val level: Int,
    val email: String,
    val password: String,
    val address: String,
    val avatar: String,
)

data class AccountWithCustomers(
    @Embedded val accountDb: AccountDb,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountCreatorId",
    )
    val customersDb: List<CustomerDb>
)