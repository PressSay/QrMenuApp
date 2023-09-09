package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DishDb(
    @PrimaryKey
    val dishNameId: String,
    val categoryCreatorId: String,
    val description: String,
    val cost: Int,
    val countTimes: Int,
    val img: String
)

data class DishWithCustomers(
    @Embedded val dishDb: DishDb,
    @Relation(
        parentColumn = "dishNameId",
        entityColumn = "customerId",
        associateBy = Junction(CustomerDishCrossRef::class)
    )
    val customersDb: List<CustomerDb>
)

data class DishWithReviews(
    @Embedded val dishDb: DishDb,
    @Relation(
        parentColumn = "dishNameId",
        entityColumn = "dishCreatorId",
    )
    val reviewsDb: List<ReviewDb>
)