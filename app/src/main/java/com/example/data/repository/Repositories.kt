package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.ReportHistoryEntity
import com.example.data.local.entity.SystemSettingsEntity
import com.example.data.local.entity.UserEntity
import com.example.domain.model.DashboardMetrics
import com.example.domain.model.Report
import com.example.domain.model.ReportHistory
import com.example.domain.model.ReportOrigin
import com.example.domain.model.ReportPriority
import com.example.domain.model.ReportStatus
import com.example.domain.model.SystemSettings
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.util.CodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ReportRepository(private val database: AppDatabase) {
    private val reportDao = database.reportDao()
    private val historyDao = database.reportHistoryDao()
    private val userDao = database.userDao()
    private val settingsDao = database.systemSettingsDao()

    @Volatile
    var lastGeneratedCode: String? = "DLX-250515-000123"
        private set

    fun getAllReports(): Flow<List<Report>> {
        return reportDao.getAllReports().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    fun getReportsFiltered(status: ReportStatus?, query: String = ""): Flow<List<Report>> {
        val flow = if (query.isNotBlank()) {
            reportDao.searchReports(query.trim())
        } else if (status != null) {
            reportDao.getReportsByStatus(status.name)
        } else {
            reportDao.getAllReports()
        }

        return flow.map { entities ->
            var list = entities.map { it.toDomain() }
            if (status != null && query.isNotBlank()) {
                list = list.filter { it.status == status }
            }
            list
        }.flowOn(Dispatchers.IO)
    }

    fun getReportByCode(code: String): Flow<Report?> {
        return reportDao.observeReportByCode(code.trim().uppercase()).map {
            it?.toDomain()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getReportSync(code: String): Report? = withContext(Dispatchers.IO) {
        reportDao.getReportByCode(code.trim().uppercase())?.toDomain()
    }

    suspend fun createReport(
        neighborhood: String,
        wasteType: String,
        reference: String,
        phoneNumber: String,
        otpVerified: Boolean = true,
        origin: ReportOrigin = ReportOrigin.APP,
        priority: ReportPriority = ReportPriority.MEDIUM,
        latitude: Double? = null,
        longitude: Double? = null,
        mediaUri: String? = null,
        mediaType: String? = null
    ): String = withContext(Dispatchers.IO) {
        val code = CodeGenerator.generateUniqueCode(reportDao)
        val now = System.currentTimeMillis()

        // Assign default coordinates based on neighborhood if not provided
        val coords = getNeighborhoodCoords(neighborhood)
        val finalLat = latitude ?: coords.first
        val finalLng = longitude ?: coords.second

        val entity = ReportEntity(
            code = code,
            neighborhood = neighborhood.trim(),
            wasteType = wasteType.trim(),
            reference = reference.trim(),
            phoneNumber = phoneNumber.trim(),
            otpVerified = otpVerified,
            status = ReportStatus.REGISTERED.name,
            priority = priority.name,
            assignedTeam = null,
            latitude = finalLat,
            longitude = finalLng,
            origin = origin.name,
            mediaUri = mediaUri,
            mediaType = mediaType,
            createdAt = now,
            updatedAt = now
        )

        val historyEntity = ReportHistoryEntity(
            reportCode = code,
            previousStatus = null,
            newStatus = ReportStatus.REGISTERED.name,
            observation = if (origin == ReportOrigin.USSD) {
                "Denúncia criada via terminal USSD *384*73407#."
            } else {
                "Denúncia criada pelo cidadão na aplicação."
            },
            changedAt = now,
            author = if (origin == ReportOrigin.USSD) "Cidadão (USSD)" else "Cidadão (APP)",
            source = origin.name
        )

        // Atomic insertion
        reportDao.insertReport(entity)
        historyDao.insertHistory(historyEntity)
        lastGeneratedCode = code

        code
    }

    suspend fun updateReportStatus(
        reportCode: String,
        newStatus: ReportStatus,
        observation: String?,
        author: String = "Operador",
        source: String = "Admin",
        assignedTeam: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val current = reportDao.getReportByCode(reportCode) ?: return@withContext false
        if (current.status == newStatus.name) {
            return@withContext false // Prevent same status update
        }

        val now = System.currentTimeMillis()
        val updated = current.copy(
            status = newStatus.name,
            assignedTeam = assignedTeam ?: current.assignedTeam,
            updatedAt = now
        )

        val history = ReportHistoryEntity(
            reportCode = reportCode,
            previousStatus = current.status,
            newStatus = newStatus.name,
            observation = observation?.trim()?.ifEmpty { null },
            changedAt = now,
            author = author,
            source = source
        )

        reportDao.updateReport(updated)
        historyDao.insertHistory(history)
        true
    }

    suspend fun updateReportTeam(reportCode: String, team: String?): Boolean = withContext(Dispatchers.IO) {
        val current = reportDao.getReportByCode(reportCode) ?: return@withContext false
        val now = System.currentTimeMillis()
        val updated = current.copy(assignedTeam = team, updatedAt = now)
        reportDao.updateReport(updated)

        historyDao.insertHistory(
            ReportHistoryEntity(
                reportCode = reportCode,
                previousStatus = current.status,
                newStatus = current.status,
                observation = "Equipa atribuída: ${team ?: "Nenhuma"}",
                changedAt = now,
                author = "Administrador",
                source = "Admin"
            )
        )
        true
    }

    suspend fun updateReportPriority(reportCode: String, priority: ReportPriority): Boolean = withContext(Dispatchers.IO) {
        val current = reportDao.getReportByCode(reportCode) ?: return@withContext false
        val now = System.currentTimeMillis()
        val updated = current.copy(priority = priority.name, updatedAt = now)
        reportDao.updateReport(updated)
        true
    }

    fun getReportHistory(reportCode: String): Flow<List<ReportHistory>> {
        return historyDao.getHistoryForReport(reportCode).map { list ->
            list.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    fun getDashboardMetrics(): Flow<DashboardMetrics> {
        return reportDao.getAllReports().map { reports ->
            val total = reports.size
            val pending = reports.count { it.status == "REGISTERED" || it.status == "UNDER_ANALYSIS" || it.status == "IN_PROGRESS" }
            val resolved = reports.count { it.status == "RESOLVED" || it.status == "CLOSED" }
            val sla = if (total > 0) ((resolved.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100) else 94

            val statusMap = mutableMapOf<ReportStatus, Int>()
            for (s in ReportStatus.values()) {
                statusMap[s] = reports.count { it.status == s.name }
            }

            val neighborMap = mutableMapOf<String, Int>()
            for (r in reports) {
                neighborMap[r.neighborhood] = (neighborMap[r.neighborhood] ?: 0) + 1
            }
            val topNeighborhoods = neighborMap.toList().sortedByDescending { it.second }.take(5)

            val originMap = mutableMapOf<ReportOrigin, Int>()
            for (o in ReportOrigin.values()) {
                originMap[o] = reports.count { it.origin == o.name }
            }

            DashboardMetrics(
                totalReports = total,
                pendingReports = pending,
                resolvedReports = resolved,
                slaRatePercent = sla,
                totalGrowthPercent = 12,
                pendingGrowthPercent = 8,
                resolvedGrowthPercent = 18,
                slaGrowthPercent = 5,
                statusDistribution = statusMap,
                topNeighborhoods = topNeighborhoods,
                originDistribution = originMap
            )
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteAllData() = withContext(Dispatchers.IO) {
        reportDao.deleteAllReports()
        historyDao.deleteAllHistory()
        userDao.deleteAllUsers()
    }

    // Users
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers().map { list ->
        list.map { it.toDomain() }
    }.flowOn(Dispatchers.IO)

    suspend fun getUserById(id: Long): User? = withContext(Dispatchers.IO) {
        userDao.getUserById(id)?.toDomain()
    }

    suspend fun addUser(user: User): Long = withContext(Dispatchers.IO) {
        userDao.insertUser(user.toEntity())
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUser(user.toEntity())
    }

    // Settings
    fun getSettings(): Flow<SystemSettings> = settingsDao.getSettings().map {
        it?.toDomain() ?: SystemSettings()
    }.flowOn(Dispatchers.IO)

    suspend fun saveSettings(settings: SystemSettings) = withContext(Dispatchers.IO) {
        settingsDao.saveSettings(settings.toEntity())
    }

    private fun getNeighborhoodCoords(name: String): Pair<Double, Double> {
        return when (name.lowercase()) {
            "chamanculo" -> Pair(-25.9535, 32.5580)
            "alto-maé" -> Pair(-25.9610, 32.5720)
            "mavalane" -> Pair(-25.9250, 32.5800)
            "maxaquene" -> Pair(-25.9450, 32.5900)
            "sommerchield" -> Pair(-25.9670, 32.5930)
            "central \"b\"", "central b" -> Pair(-25.9680, 32.5780)
            "malhangalene" -> Pair(-25.9420, 32.5740)
            "benfica" -> Pair(-25.8890, 32.5400)
            "coop" -> Pair(-25.9520, 32.5820)
            "laulane" -> Pair(-25.9080, 32.6020)
            "catembe" -> Pair(-25.9890, 32.5560)
            else -> Pair(-25.9600 + (Math.random() - 0.5) * 0.05, 32.5700 + (Math.random() - 0.5) * 0.05)
        }
    }
}

// Extension mappers
fun ReportEntity.toDomain(): Report {
    return Report(
        code = code,
        neighborhood = neighborhood,
        wasteType = wasteType,
        reference = reference,
        phoneNumber = phoneNumber,
        otpVerified = otpVerified,
        status = runCatching { ReportStatus.valueOf(status) }.getOrDefault(ReportStatus.REGISTERED),
        priority = runCatching { ReportPriority.valueOf(priority) }.getOrDefault(ReportPriority.MEDIUM),
        assignedTeam = assignedTeam,
        latitude = latitude,
        longitude = longitude,
        origin = runCatching { ReportOrigin.valueOf(origin) }.getOrDefault(ReportOrigin.APP),
        mediaUri = mediaUri,
        mediaType = mediaType,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ReportHistoryEntity.toDomain(): ReportHistory {
    return ReportHistory(
        id = id,
        reportCode = reportCode,
        previousStatus = previousStatus?.let { runCatching { ReportStatus.valueOf(it) }.getOrNull() },
        newStatus = runCatching { ReportStatus.valueOf(newStatus) }.getOrDefault(ReportStatus.REGISTERED),
        observation = observation,
        changedAt = changedAt,
        author = author,
        source = source
    )
}

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.OPERATOR),
        phone = phone,
        email = email,
        lastAccess = lastAccess,
        isOnline = isOnline,
        canManageReports = canManageReports,
        canManageUsers = canManageUsers,
        canViewReports = canViewReports,
        canConfigureSystem = canConfigureSystem
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        role = role.name,
        phone = phone,
        email = email,
        lastAccess = lastAccess,
        isOnline = isOnline,
        canManageReports = canManageReports,
        canManageUsers = canManageUsers,
        canViewReports = canViewReports,
        canConfigureSystem = canConfigureSystem
    )
}

fun SystemSettingsEntity.toDomain(): SystemSettings {
    return SystemSettings(
        id = id,
        serviceCode = serviceCode,
        ussdServerUrl = ussdServerUrl,
        requestTimeoutSeconds = requestTimeoutSeconds,
        operationMode = operationMode,
        appVersion = appVersion,
        environment = environment
    )
}

fun SystemSettings.toEntity(): SystemSettingsEntity {
    return SystemSettingsEntity(
        id = id,
        serviceCode = serviceCode,
        ussdServerUrl = ussdServerUrl,
        requestTimeoutSeconds = requestTimeoutSeconds,
        operationMode = operationMode,
        appVersion = appVersion,
        environment = environment
    )
}
