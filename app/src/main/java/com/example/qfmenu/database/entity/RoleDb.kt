package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class RoleDb(
    @PrimaryKey
    val roleNameId: String,
    val expired: String,
    val description: String,
)

data class RoleWithAccounts(
    @Embedded val roleDb: RoleDb,
    @Relation(
        parentColumn = "roleNameId",
        entityColumn = "roleCreatorId"
    )
    val accountsDb: List<AccountDb>
)