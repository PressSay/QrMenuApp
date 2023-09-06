package com.example.qfmenu.database

import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CustomerDb::class], version = 1, exportSchema = true)
abstract class MenuQrRoomDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: MenuQrRoomDatabase? = null
        fun getDatabase(context: Context): MenuQrRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MenuQrRoomDatabase::class.java,
                    "menu_qr_databse"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}