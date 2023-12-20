package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.AccountDao
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.network.NetworkRetrofit

class StaffRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val accountDao: AccountDao,
) {
    suspend fun createStaff(accountDb: AccountDb) {
        val userNet = networkRetrofit.user().create(
            name = accountDb.name,
            email = accountDb.email,
            password = accountDb.password,
            password_confirmation = accountDb.password,
            level = accountDb.level,
            address = accountDb.address,
            phoneNumber = accountDb.phoneNumber,
        )
    }

    suspend fun updateStaff(accountDb: AccountDb) {
        val userNet = networkRetrofit.user().update(
            id = accountDb.id.toString(),
            name = accountDb.name,
            email = accountDb.email,
            level = accountDb.level,
            exp = accountDb.exp,
            address = accountDb.address,
            phoneNumber = accountDb.phoneNumber,
        )
    }

    suspend fun deleteStaff(accountDb: AccountDb) {
        val userNet = networkRetrofit.user().delete(accountDb.id.toString())
    }

    suspend fun fetchStaff() {
        val staffListNet = networkRetrofit.user().findAllStaff()
        if (staffListNet.isSuccessful) {
            val staffSr = staffListNet.body()!!
//                this.createListStaff(staffDbList)
            accountDao.insertAll(
                staffSr.map {
                    AccountDb(
                        it.id.toLong(),
                        it.name,
                        it.email,
                        it.exp,
                        it.phoneNumber,
                        it.level,
                        "Have been hash",
                        it.address,
                        "empty",
                        it.nameRole ?: "staff"
                    )
                }
            )
        }
    }
}