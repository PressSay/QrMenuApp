package com.example.menumanager.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menumanager.member.DatasourceMember
import com.example.qfmenu.viewmodels.models.Member

class MemberViewModel : ViewModel() {

    private val _members = MutableLiveData<List<Member>>()
    val members : LiveData<List<Member>> get() = _members

    private val _memberForCreate = MutableLiveData<Member>()
    val memberForCreate : LiveData<Member> get() = _memberForCreate

    init {
        _members.value = DatasourceMember().loadMemberList()
    }

    fun createMember(member: Member) {
        _memberForCreate.value = member
    }

    fun updateMember(id: Int, member: Member) {}

    fun deleteMember(id: Int) {}

    fun getMember(id: Int) {}

}