package com.example.qfmenu.database

import androidx.room.Entity

@Entity(tableName = "permissionRoleCrossRef", primaryKeys = ["roleId", "permissionId"])
data class PermissionRoleCrossRef(
    val roleId: Long,
    val permissionId: Long
)
