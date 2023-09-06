package com.example.qfmenu.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("permissionDb")
data class PermissionDb(
    @PrimaryKey(autoGenerate = true)
    val permissionId: Long,
    val name: String,
    val description: String
)
