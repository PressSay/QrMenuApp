package com.example.qfmenu.database.Entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
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