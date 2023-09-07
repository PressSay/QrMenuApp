package com.example.qfmenu.database

import androidx.room.Database
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qfmenu.database.Dao.AccountDao
import com.example.qfmenu.database.Dao.CategoryDao
import com.example.qfmenu.database.Dao.CategoryMenuCrossRefDao
import com.example.qfmenu.database.Dao.CustomerDao
import com.example.qfmenu.database.Dao.CustomerDishCrossRefDao
import com.example.qfmenu.database.Dao.DishDao
import com.example.qfmenu.database.Dao.InvestmentDao
import com.example.qfmenu.database.Dao.MenuDao
import com.example.qfmenu.database.Dao.OrderDao
import com.example.qfmenu.database.Dao.PermissionDao
import com.example.qfmenu.database.Dao.PermissionRoleCrossRefDao
import com.example.qfmenu.database.Dao.ReviewCustomerCrossRefDao
import com.example.qfmenu.database.Dao.ReviewDao
import com.example.qfmenu.database.Dao.RoleDao
import com.example.qfmenu.database.Dao.TableDao
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