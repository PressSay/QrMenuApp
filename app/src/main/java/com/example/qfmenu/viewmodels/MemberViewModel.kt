package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.menumanager.member.DatasourceMember
import com.example.qfmenu.database.dao.AccountDao
import com.example.qfmenu.database.dao.CustomerDao
import com.example.qfmenu.database.dao.RoleDao
import com.example.qfmenu.database.entity.AccountDb
import com.example.qfmenu.database.entity.RoleDb
import com.example.qfmenu.viewmodels.models.Member
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MemberViewModel(
    private val accountDao: AccountDao,
) : ViewModel() {

    private val _members = accountDao.getAccountsWithNameRole("Member").map {
        it.accountsDb
    }.asLiveData()

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

    fun getMember(accountId: Long): LiveData<AccountDb> {
        return accountDao.getAccount(accountId).asLiveData()
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