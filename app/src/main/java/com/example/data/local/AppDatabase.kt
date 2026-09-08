package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ReportDao
import com.example.data.local.dao.ReportHistoryDao
import com.example.data.local.dao.SystemSettingsDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.ReportHistoryEntity
import com.example.data.local.entity.SystemSettingsEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        ReportEntity::class,
        ReportHistoryEntity::class,
        UserEntity::class,
        SystemSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
    abstract fun reportHistoryDao(): ReportHistoryDao
    abstract fun userDao(): UserDao
    abstract fun systemSettingsDao(): SystemSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maputo_waste_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
