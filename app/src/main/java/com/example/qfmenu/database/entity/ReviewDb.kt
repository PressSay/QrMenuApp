package com.example.qfmenu.database.entity

import androidx.annotation.Nullable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class ReviewDb (
    @PrimaryKey(autoGenerate = true)
    val reviewId: Long = 0,
    val isThumbUp: Int,
    val description: String,
)