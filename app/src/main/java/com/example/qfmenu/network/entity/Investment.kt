package com.example.qfmenu.network.entity

data class Investment(
    val cost: String,
    val name: String
)

class ListInvestment : ArrayList<Investment>()