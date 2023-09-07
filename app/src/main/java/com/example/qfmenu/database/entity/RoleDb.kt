package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class RoleDb(
    @PrimaryKey(autoGenerate = true)
    val roleId: Long,
    val accountCreatorId: Long,
    val expired: String,
    val name: String,
    val description: String,
)

data class RoleWithPermissions(
    @Embedded val roleDb: RoleDb,
    @Relation(
        parentColumn = "roleId",
        entityColumn = "permissionId",
        associateBy = Junction(PermissionRoleCrossRef::class)
    )
    val permissionsDb: List<PermissionDb>
)
