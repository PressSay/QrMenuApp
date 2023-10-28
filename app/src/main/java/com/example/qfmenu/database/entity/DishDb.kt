package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DishDb(
    @PrimaryKey(autoGenerate = true)
    val dishId:Long = 0,
    val dishName: String,
    val categoryId: Long,
    val description: String,
    val cost: Int,
    val numberOfTimesCalled: Int = 0,
    val images: String = "Empty"
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