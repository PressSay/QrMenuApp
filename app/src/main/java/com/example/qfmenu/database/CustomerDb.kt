package com.example.qfmenu.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Calendar

@Entity(tableName = "customerDb")
data class CustomerDb(
    @PrimaryKey(autoGenerate = true)
    val customerId: Long,
    val accountCreatorId: Long,
    val expired: String,
    val name: String,
    val code: String,
    val phone: String,
    val address: String,
    val isReviewStore: Int,
)

data class CustomerAndOrderDb(
    @Embedded val customerDb: CustomerDb,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerOwnerId"
    )
    val orderDb: OrderDb
)

data class CustomerAndDishes(
    @Embedded val customerDb: CustomerDb,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "dishId",
        associateBy = Junction(CustomerDishCrossRef::class)
    )
    val dishesDb: List<DishDb>
)