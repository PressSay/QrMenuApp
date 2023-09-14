package com.example.qfmenu.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.qfmenu.database.dao.AccountDao
import com.example.qfmenu.database.entity.AccountDb
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MemberViewModel(
    private val accountDao: AccountDao,
) : ViewModel() {

//    private val _members = accountDao.getAccountsWithNameRole("Member")

    private val _memberForCreate: AccountDb? = null
    val memberForCreate get() = _memberForCreate

    fun createMember(accountDb: AccountDb) {
        viewModelScope.launch {
            accountDao.insert(accountDb)
        }
    }

    fun updateMember(accountDb: AccountDb) {
        viewModelScope.launch {
            accountDao.update(accountDb)
        }
    }

    fun deleteMember(accountDb: AccountDb) {
        viewModelScope.launch {
            accountDao.delete(accountDb)
        }
    }

    suspend fun getMember(accountId: Long): AccountDb {
        return accountDao.getAccount(accountId)
    }

}

class MemberViewModelFactory(
    private val accountDao: AccountDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(accountDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}