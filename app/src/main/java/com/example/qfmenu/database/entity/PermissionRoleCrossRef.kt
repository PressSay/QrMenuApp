package com.example.qfmenu.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["roleId", "permissionId"])
data class PermissionRoleCrossRef(
    val roleId: Long,
    val permissionId: Long
)
