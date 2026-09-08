package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseSeeder
import com.example.data.repository.ReportRepository
import com.example.ussd.UssdEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WasteApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ReportRepository(database) }
    val ussdEngine by lazy { UssdEngine(repository) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Prepopulate realistic data if empty
        appScope.launch {
            DatabaseSeeder.seedDatabase(
                reportDao = database.reportDao(),
                historyDao = database.reportHistoryDao(),
                userDao = database.userDao(),
                settingsDao = database.systemSettingsDao(),
                force = false
            )
        }
    }
}
