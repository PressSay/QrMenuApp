package com.example.menumanager.table

import com.example.qfmenu.viewmodels.models.Table

class DatasourceTable {
    fun loadTableList(): List<Table> {
        return listOf(
            Table("1", "Ordered"),
            Table("1", "Free"),
            Table("1", "Waiting"),
        )
    }
}