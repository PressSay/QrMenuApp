package com.example.qfmenu.repository

import com.example.qfmenu.database.dao.TableDao
import com.example.qfmenu.database.entity.TableDb
import com.example.qfmenu.network.NetworkRetrofit

class TableRepository(
    private val networkRetrofit: NetworkRetrofit,
    private val tableDao: TableDao
) {
    suspend fun createTable(numberTable: Int) {
        val tableNet = networkRetrofit.table().create(numberTable)
    }

    suspend fun updateTable(id: String, status: String) {
        val tableNet = networkRetrofit.table().update(id, status)
    }

    suspend fun fetchTable() {
        val tableNet = networkRetrofit.table().findALl()
        if (tableNet.isSuccessful) {
            tableNet.body()?.let {
                tableDao.insertAll(it.map { table ->
                    TableDb(
                        tableId = table.nameTable,
                        status = table.status,
                    )
                })
            }
        }
    }
}