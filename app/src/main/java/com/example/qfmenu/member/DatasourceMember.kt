package com.example.qfmenu.member

import com.example.qfmenu.R
import com.example.qfmenu.viewmodels.models.Member

class DatasourceMember {
    fun loadMemberList(): List<Member> {
        return listOf<Member>(
            Member(
                "member 1",
                "0123456789",
                "viplanhts@gmail.com",
                "password",
                "address 1",
                "employee 1",
                R.drawable.img_image_4
            ),
            Member(
                "member 2",
                "0123456789",
                "viplanhts@gmail.com",
                "password",
                "address 2",
                "employee 2",
                R.drawable.img_image_4
            ),
            Member(
                "member 3",
                "0123456789",
                "viplanhts@gmail.com",
                "password",
                "address 3",
                "employee 3",
                R.drawable.img_image_4
            ),
            Member(
                "member 4",
                "0123456789",
                "viplanhts@gmail.com",
                "password",
                "address 4",
                "employee 4",
                R.drawable.img_image_4
            ),
        )
    }
}