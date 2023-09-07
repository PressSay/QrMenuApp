package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class ReviewDb (
    @PrimaryKey(autoGenerate = true)
    val reviewId: Long,
    val dishCreatorId: Long,
    val isThumbUp: Int,
    val description: String,
)

data class ReviewWithCustomerDishCrossRefs(
    @Embedded val reviewDb: ReviewDb,
    @Relation(
        parentColumn = "reviewId",
        entityColumn = "reviewCreatorId"
    )
    val customerDishCrossRefsDb: List<CustomerDishCrossRef>
)