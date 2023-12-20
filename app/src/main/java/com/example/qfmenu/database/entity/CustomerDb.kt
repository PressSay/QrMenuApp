package com.example.qfmenu.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Calendar

@Entity
data class CustomerDb(
    @PrimaryKey(autoGenerate = true)
    val customerId: Long = 0,
    val accountCreatorId: Long,
    val dateExpireCode: String,
    val name: String,
    val code: String,
    val phone: String,
    val address: String,
    val created: String = String.format(
        "%04d-%02d-%02d %02d:%02d:%02d",
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DATE),
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        Calendar.getInstance().get(Calendar.SECOND),
    )
)

data class CustomerAndOrderDb(
    @Embedded val customerDb: CustomerDb,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerOwnerId"
    )
    val orderDb: OrderDb
)

data class CustomerWithDishes(
    @Embedded val customerDb: CustomerDb,
    @Relation(
        parentColumn = "customerId",
        entityColumn = "dishId",
        associateBy = Junction(CustomerDishDb::class)
    )
    val dishesDb: List<DishDb>
)