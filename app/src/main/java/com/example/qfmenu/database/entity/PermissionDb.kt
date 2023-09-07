package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class PermissionDb(
    @PrimaryKey(autoGenerate = true)
    val permissionId: Long,
    val name: String,
    val description: String
)

data class PermissionWithRoles(
    @Embedded val permissionDb: PermissionDb,
    @Relation(
        parentColumn = "permissionId",
        entityColumn = "roleId",
        associateBy = Junction(PermissionRoleCrossRef::class)
    )
    val roles: List<RoleDb>
)
