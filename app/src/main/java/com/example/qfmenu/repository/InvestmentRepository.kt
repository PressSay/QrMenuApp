package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.InvestmentDao
import com.example.qfmenu.database.entity.InvestmentDb
import com.example.qfmenu.network.NetworkRetrofit

class InvestmentRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val investmentDao: InvestmentDao,
) {
    suspend fun fetchInvestment() {
        val investmentNet = networkRetrofit.investment().findALl()
        if (investmentNet.isSuccessful) {
            investmentDao.deleteAll()
            val ListInvestment = investmentNet.body()!!
            investmentDao.insertAll(ListInvestment.map { investment ->
                InvestmentDb(
                    name = investment.name,
                    cost = investment.cost.toInt(),
                )
            })
        }
    }

    suspend fun createInvestment(investmentDbList: List<InvestmentDb>) {
        investmentDbList.forEach { investmentDb ->
            val investmentNet = networkRetrofit.investment().create(
                investmentDb.name,
                investmentDb.cost.toString()
            )
        }
    }

    suspend fun deleteInvestment(investmentDb: InvestmentDb) {
        val investmentNet = networkRetrofit.investment().delete(investmentDb.name)
    }
}