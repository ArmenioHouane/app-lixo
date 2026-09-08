package com.example.domain.model

data class Report(
    val code: String,
    val neighborhood: String,
    val wasteType: String,
    val reference: String,
    val phoneNumber: String,
    val otpVerified: Boolean,
    val status: ReportStatus,
    val priority: ReportPriority = ReportPriority.MEDIUM,
    val assignedTeam: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val origin: ReportOrigin,
    val mediaUri: String? = null,
    val mediaType: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

data class ReportHistory(
    val id: Long = 0,
    val reportCode: String,
    val previousStatus: ReportStatus?,
    val newStatus: ReportStatus,
    val observation: String?,
    val changedAt: Long,
    val author: String = "Sistema",
    val source: String = "APP"
)

data class User(
    val id: Long = 0,
    val name: String,
    val role: UserRole,
    val phone: String,
    val email: String,
    val lastAccess: Long,
    val isOnline: Boolean,
    val canManageReports: Boolean = true,
    val canManageUsers: Boolean = (role == UserRole.ADMIN),
    val canViewReports: Boolean = true,
    val canConfigureSystem: Boolean = (role == UserRole.ADMIN)
)

data class SystemSettings(
    val id: Int = 1,
    val serviceCode: String = "*384*73407#",
    val ussdServerUrl: String = "http://10.0.2.2:8000/ussd.php",
    val requestTimeoutSeconds: Int = 8,
    val operationMode: String = "Offline (local)",
    val appVersion: String = "1.0.0",
    val environment: String = "Produção"
)

data class DashboardMetrics(
    val totalReports: Int = 0,
    val pendingReports: Int = 0,
    val resolvedReports: Int = 0,
    val slaRatePercent: Int = 94,
    val totalGrowthPercent: Int = 12,
    val pendingGrowthPercent: Int = 8,
    val resolvedGrowthPercent: Int = 18,
    val slaGrowthPercent: Int = 5,
    val statusDistribution: Map<ReportStatus, Int> = emptyMap(),
    val topNeighborhoods: List<Pair<String, Int>> = emptyList(),
    val originDistribution: Map<ReportOrigin, Int> = emptyMap()
)
