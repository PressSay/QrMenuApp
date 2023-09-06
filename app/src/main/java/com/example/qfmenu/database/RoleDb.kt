package com.example.qfmenu.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "roleDb")
data class RoleDb(
    @PrimaryKey(autoGenerate = true)
    val roleId: Long,
    val accountCreatorId: Long,
    val expired: String,
    val name: String,
    val description: String,
)

data class RoleAndPermissions(
    @Embedded val roleDb: RoleDb,
    @Relation(
        parentColumn = "roleId",
        entityColumn = "permissionId",
        associateBy = Junction(PermissionRoleCrossRef::class)
    )
    val permissionsDb: List<PermissionDb>
)
