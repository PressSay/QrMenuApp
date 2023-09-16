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
    val expired: String,
    val name: String,
    val code: String,
    val phone: String,
    val address: String,
    val calendar: String = String.format(
        "%02d/%02d/%04d", Calendar.getInstance()
            .get(Calendar.DATE), Calendar.getInstance()
            .get(Calendar.MONTH), Calendar.getInstance()
            .get(Calendar.YEAR)
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
        associateBy = Junction(CustomerDishCrossRef::class)
    )
    val dishesDb: List<DishDb>
)