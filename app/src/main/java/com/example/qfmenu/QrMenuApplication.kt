package com.example.qfmenu

import android.app.Application
import com.example.qfmenu.database.MenuQrRoomDatabase

class QrMenuApplication : Application() {
    val database: MenuQrRoomDatabase by lazy {
        MenuQrRoomDatabase.getDatabase(this)
    }
}