package com.example.qfmenu.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

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

data class AccountAndRoles(
    @Embedded val accountDb: AccountDb,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountCreatorId"
    )
    val rolesDb: List<RoleDb>
)

data class AccountAndCustomers(
    @Embedded val accountDb: AccountDb,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountCreatorId",
    )
    val customersDb: List<CustomerDb>
)