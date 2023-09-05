package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menumanager.table.DatasourceTable
import com.example.qfmenu.viewmodels.models.Table

class TableViewModel : ViewModel() {
    
    private var _isStartOrder : Boolean = false
    val isStartOrder get() = _isStartOrder
    fun setIsStartOrder(isStartOrder: Boolean) {
        _isStartOrder = isStartOrder
    }

    private val _tables = MutableLiveData<List<Table>>()
    val tables : LiveData<List<Table>> get() = _tables

    private val _tableForCreate = MutableLiveData<Table>()
    val tableForCreate : LiveData<Table> get() = _tableForCreate

    init {
        _tables.value = DatasourceTable().loadTableList()
    }

    fun createTable(table: Table) {
        _tableForCreate.value = table
    }

    fun updateTable(id: Int, table: Table) {}

    fun deleteTable(id: Int) {}

    fun getAmountTable(): Int? {
        return _tables.value?.size
    }

}