package com.example.qfmenu.database

import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qfmenu.database.dao.AccountDao
import com.example.qfmenu.database.dao.CategoryDao
import com.example.qfmenu.database.dao.CategoryMenuCrossRefDao
import com.example.qfmenu.database.dao.CustomerDao
import com.example.qfmenu.database.dao.CustomerDishCrossRefDao
import com.example.qfmenu.database.dao.DishDao
import com.example.qfmenu.database.dao.InvestmentDao
import com.example.qfmenu.database.dao.MenuDao
import com.example.qfmenu.database.dao.OrderDao
import com.example.qfmenu.database.dao.PermissionDao
import com.example.qfmenu.database.dao.PermissionRoleCrossRefDao
import com.example.qfmenu.database.dao.ReviewCustomerCrossRefDao
import com.example.qfmenu.database.dao.ReviewDao
import com.example.qfmenu.database.dao.RoleDao
import com.example.qfmenu.database.dao.TableDao
import com.example.qfmenu.database.Entity.AccountDb
import com.example.qfmenu.database.Entity.CategoryDb
import com.example.qfmenu.database.Entity.CategoryMenuCrossRef
import com.example.qfmenu.database.Entity.CustomerDb
import com.example.qfmenu.database.Entity.CustomerDishCrossRef
import com.example.qfmenu.database.Entity.DishDb
import com.example.qfmenu.database.Entity.InvestmentDb
import com.example.qfmenu.database.Entity.MenuDb
import com.example.qfmenu.database.Entity.OrderDb
import com.example.qfmenu.database.Entity.PermissionDb
import com.example.qfmenu.database.Entity.PermissionRoleCrossRef
import com.example.qfmenu.database.Entity.ReviewCustomerCrossRef
import com.example.qfmenu.database.Entity.ReviewDb
import com.example.qfmenu.database.Entity.ReviewDishCrossRef
import com.example.qfmenu.database.Entity.RoleDb
import com.example.qfmenu.database.Entity.TableDb

@Database(
    entities = [
        AccountDb::class,
        CategoryDb::class,
        CategoryMenuCrossRef::class,
        CustomerDb::class,
        CustomerDishCrossRef::class,
        DishDb::class,
        InvestmentDb::class, MenuDb::class,
        OrderDb::class, PermissionDb::class,
        PermissionRoleCrossRef::class,
        ReviewCustomerCrossRef::class,
        ReviewDb::class,
        ReviewDishCrossRef::class,
        RoleDb::class,
        TableDb::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MenuQrRoomDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun categoryMenuCrossRefDao(): CategoryMenuCrossRefDao

    abstract fun customerDao(): CustomerDao

    abstract fun customerDishCrossRefDao(): CustomerDishCrossRefDao

    abstract fun dishDao(): DishDao

    abstract fun investmentDao(): InvestmentDao

    abstract fun menuDao(): MenuDao

    abstract fun orderDao(): OrderDao

    abstract fun permissionDao(): PermissionDao

    abstract fun permissionRoleCrossRefDao(): PermissionRoleCrossRefDao

    abstract fun reviewCustomerCrossRefDao(): ReviewCustomerCrossRefDao

    abstract fun reviewDao(): ReviewDao

    abstract fun roleDao(): RoleDao

    abstract fun tableDao(): TableDao

    companion object {
        @Volatile
        private var INSTANCE: MenuQrRoomDatabase? = null
        fun getDatabase(context: Context): MenuQrRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MenuQrRoomDatabase::class.java,
                    "menu_qr_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}