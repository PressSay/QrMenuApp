package com.example.menumanager.order.offline.queue

import com.example.qfmenu.viewmodels.models.Customer

data class OrderQueue(
    val customer: Customer,
    var isSelected: Boolean = false
)
