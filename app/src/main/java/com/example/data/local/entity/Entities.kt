package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val code: String,
    val neighborhood: String,
    val wasteType: String,
    val reference: String,
    val phoneNumber: String,
    val otpVerified: Boolean,
    val status: String,
    val priority: String = "MEDIUM",
    val assignedTeam: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val origin: String,
    val mediaUri: String? = null,
    val mediaType: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "report_history",
    foreignKeys = [
        ForeignKey(
            entity = ReportEntity::class,
            parentColumns = ["code"],
            childColumns = ["reportCode"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reportCode")]
)
data class ReportHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reportCode: String,
    val previousStatus: String?,
    val newStatus: String,
    val observation: String?,
    val changedAt: Long,
    val author: String = "Sistema",
    val source: String = "APP"
)

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String,
    val phone: String,
    val email: String,
    val lastAccess: Long,
    val isOnline: Boolean,
    val canManageReports: Boolean = true,
    val canManageUsers: Boolean = false,
    val canViewReports: Boolean = true,
    val canConfigureSystem: Boolean = false
)

@Entity(tableName = "system_settings")
data class SystemSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val serviceCode: String = "*384*73407#",
    val ussdServerUrl: String = "http://10.0.2.2:8000/ussd.php",
    val requestTimeoutSeconds: Int = 8,
    val operationMode: String = "Offline (local)",
    val appVersion: String = "1.0.0",
    val environment: String = "Produção"
)
