package com.example.qfmenu.database.Entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DishDb(
    @PrimaryKey(autoGenerate = true)
    val dishId: Int,
    val name: String,
    val cost: Float,
    val categoryCreatorId: Int,
    val countTimes: Int,
    val img: String
)

data class DishWithCustomers(
    @Embedded val dishDb: DishDb,
    @Relation(
        parentColumn = "dishId",
        entityColumn = "customerId",
        associateBy = Junction(CustomerDishCrossRef::class)
    )
    val customersDb: List<CustomerDb>
)

data class DishWithReviews(
    @Embedded val dishDb: DishDb,
    @Relation(
        parentColumn = "dishId",
        entityColumn = "dishCreatorId",
    )
    val reviewsDb: List<ReviewDb>
)