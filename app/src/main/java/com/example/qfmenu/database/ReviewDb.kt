package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "reviewDb")
data class ReviewDb (
    @PrimaryKey(autoGenerate = true)
    val reviewId: Long,
    val isThumbUp: Int,
    val description: String,
)

data class ReviewAndCustomerDishCrossRefs(
    @Embedded val reviewDb: ReviewDb,
    @Relation(
        parentColumn = "reviewId",
        entityColumn = "reviewCreatorId"
    )
    val customerDishCrossRefsDb: List<CustomerDishCrossRef>
)