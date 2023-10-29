package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.AccountDao
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.network.NetworkRetrofit

class StaffRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val accountDao: AccountDao,
) {
    suspend fun createListStaff(accountDbList: List<AccountDb>) {
        accountDbList.forEach { accountDb ->
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
    }

    suspend fun updateListStaff(accountDbList: List<AccountDb>) {
        accountDbList.forEach { accountDb ->
            val userNet = networkRetrofit.user().update(
                id = accountDb.id.toString(),
                name = accountDb.name,
                email = accountDb.email,
                level = accountDb.level,
                address = accountDb.address,
                phoneNumber = accountDb.phoneNumber,
            )
        }
    }

    suspend fun deleteStaff(accountDbList: List<AccountDb>) {
        accountDbList.forEach { accountDb ->
            val userNet = networkRetrofit.user().delete(accountDb.id.toString())
        }
    }

    suspend fun fetchStaff() {
        val userNet = networkRetrofit.user().findAllStaff()
        if (userNet.isSuccessful) {
            userNet.body()?.let {
                accountDao.insertAll(it.map { user ->
                    AccountDb(
                        name = user.name,
                        email = user.email,
                        exp = user.exp,
                        phoneNumber = user.phoneNumber,
                        level = user.level,
                        password = "empty",
                        address = user.address,
                        avatar = user.image.source,
                        nameRole = user.nameRole!!
                    )
                })
            }
        }
    }
}