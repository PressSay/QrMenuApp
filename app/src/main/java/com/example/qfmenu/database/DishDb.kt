package com.example.qfmenu.database

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "dishDb")
data class DishDb(
    @PrimaryKey(autoGenerate = true)
    val dishId: Int,
    val name: String,
    val cost: Float,
    val categoryCreatorId: Int,
    val countTimes: Int,
    val img: String
)

data class DishAndCustomers(
    @Embedded val dishDb: DishDb,
    @Relation(
        parentColumn = "dishId",
        entityColumn = "customerId",
        associateBy = Junction(CustomerDishCrossRef::class)
    )
    val customersDb: List<CustomerDb>
)

data class DishAndReviews(
    @Embedded val dishDb: DishDb,
    @Relation(
        parentColumn = "dishId",
        entityColumn = "reviewId",
        associateBy = Junction(ReviewDishCrossRef::class)
    )
    val reviewsDb: List<ReviewDb>
)