package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.TableDao
import com.example.qfmenu.database.entity.TableDb
import kotlinx.coroutines.launch

class TableViewModel(
    private val tableDao: TableDao
) : ViewModel() {
    val tables get(): LiveData<List<TableDb>> = tableDao.getTables().asLiveData()

    private var _isStartOrder : Boolean = false
    val isStartOrder get() = _isStartOrder
    fun setIsStartOrder(isStartOrder: Boolean) {
        _isStartOrder = isStartOrder
    }


    fun createTables(tablesDb: List<TableDb>) {
        viewModelScope.launch {
            tablesDb.forEach {
                tableDao.insert(it)
            }
        }
    }

    fun updateTable(tableDb: TableDb) {
        viewModelScope.launch {
            tableDao.update(tableDb)
        }
    }

    fun deleteTables(tablesDb: List<TableDb>) {
        viewModelScope.launch {
            tablesDb.forEach {
                tableDao.delete(it)
            }
        }
    }

}


class TableViewModelFactory(
    private val tableDao: TableDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TableViewModel(tableDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}