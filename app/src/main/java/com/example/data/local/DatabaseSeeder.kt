package com.example.data.local

import com.example.data.local.dao.ReportDao
import com.example.data.local.dao.ReportHistoryDao
import com.example.data.local.dao.SystemSettingsDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.ReportHistoryEntity
import com.example.data.local.entity.SystemSettingsEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {
    suspend fun seedDatabase(
        reportDao: ReportDao,
        historyDao: ReportHistoryDao,
        userDao: UserDao,
        settingsDao: SystemSettingsDao,
        force: Boolean = false
    ) = withContext(Dispatchers.IO) {
        if (!force && reportDao.existsByCode("DLX-250515-000123")) {
            return@withContext
        }

        if (force) {
            reportDao.deleteAllReports()
            historyDao.deleteAllHistory()
            userDao.deleteAllUsers()
        }

        val now = System.currentTimeMillis()
        val oneHour = 3600_000L
        val oneDay = 86400_000L

        // Settings
        settingsDao.saveSettings(
            SystemSettingsEntity(
                id = 1,
                serviceCode = "*384*73407#",
                ussdServerUrl = "http://10.0.2.2:8000/ussd.php",
                requestTimeoutSeconds = 8,
                operationMode = "Offline (local)",
                appVersion = "1.0.0",
                environment = "Produção"
            )
        )

        // Seed Users
        val users = listOf(
            UserEntity(
                id = 1,
                name = "Carlos Macuácua",
                role = "ADMIN",
                phone = "+258 84 123 4567",
                email = "carlos.macuacua@maputo.gov.mz",
                lastAccess = now - (15 * 60_000L),
                isOnline = true,
                canManageReports = true,
                canManageUsers = true,
                canViewReports = true,
                canConfigureSystem = true
            ),
            UserEntity(
                id = 2,
                name = "Ana Paula",
                role = "OPERATOR",
                phone = "+258 82 456 7890",
                email = "ana.paula@maputo.gov.mz",
                lastAccess = now - (5 * 60_000L),
                isOnline = true,
                canManageReports = true,
                canManageUsers = false,
                canViewReports = true,
                canConfigureSystem = false
            ),
            UserEntity(
                id = 3,
                name = "José Nhantumbo",
                role = "SUPERVISOR",
                phone = "+258 84 987 6543",
                email = "jose.nhantumbo@maputo.gov.mz",
                lastAccess = now - (2 * oneHour),
                isOnline = false,
                canManageReports = true,
                canManageUsers = false,
                canViewReports = true,
                canConfigureSystem = false
            ),
            UserEntity(
                id = 4,
                name = "Marta Cossa",
                role = "OPERATOR",
                phone = "+258 87 234 5678",
                email = "marta.cossa@maputo.gov.mz",
                lastAccess = now - (30 * 60_000L),
                isOnline = true,
                canManageReports = true,
                canManageUsers = false,
                canViewReports = true,
                canConfigureSystem = false
            ),
            UserEntity(
                id = 5,
                name = "Paulo Guambe",
                role = "INSPECTOR",
                phone = "+258 86 345 6789",
                email = "paulo.guambe@maputo.gov.mz",
                lastAccess = now - (5 * oneHour),
                isOnline = false,
                canManageReports = true,
                canManageUsers = false,
                canViewReports = true,
                canConfigureSystem = false
            ),
            UserEntity(
                id = 6,
                name = "Isac Macamo",
                role = "OPERATOR",
                phone = "+258 84 567 8901",
                email = "isac.macamo@maputo.gov.mz",
                lastAccess = now - (10 * 60_000L),
                isOnline = true,
                canManageReports = true,
                canManageUsers = false,
                canViewReports = true,
                canConfigureSystem = false
            )
        )
        userDao.insertUsers(users)

        // Seed Reports
        val reports = listOf(
            ReportEntity(
                code = "DLX-250515-000123",
                neighborhood = "Chamanculo",
                wasteType = "Lixo doméstico",
                reference = "Em frente à Escola Primária de Chamanculo, próximo ao mercado 2 de Fevereiro.",
                phoneNumber = "+258 84 123 4567",
                otpVerified = true,
                status = "UNDER_ANALYSIS",
                priority = "MEDIUM",
                assignedTeam = "Equipe Chamanculo",
                latitude = -25.9535,
                longitude = 32.5580,
                origin = "APP",
                createdAt = now - (3 * oneHour),
                updatedAt = now - (45 * 60_000L)
            ),
            ReportEntity(
                code = "DLX-250515-000122",
                neighborhood = "Alto-Maé",
                wasteType = "Entulho / Construção",
                reference = "Rua da Unidade, próximo à paragem do Zimpeto.",
                phoneNumber = "+258 82 991 3344",
                otpVerified = true,
                status = "REGISTERED",
                priority = "HIGH",
                assignedTeam = null,
                latitude = -25.9610,
                longitude = 32.5720,
                origin = "USSD",
                createdAt = now - (5 * oneHour),
                updatedAt = now - (5 * oneHour)
            ),
            ReportEntity(
                code = "DLX-250515-000121",
                neighborhood = "Mavalane",
                wasteType = "Lixo doméstico",
                reference = "Rua da Resistência, ao lado do campo de futebol.",
                phoneNumber = "+258 87 554 1122",
                otpVerified = true,
                status = "IN_PROGRESS",
                priority = "MEDIUM",
                assignedTeam = "Equipe Mavalane",
                latitude = -25.9250,
                longitude = 32.5800,
                origin = "USSD",
                createdAt = now - (8 * oneHour),
                updatedAt = now - (2 * oneHour)
            ),
            ReportEntity(
                code = "DLX-250515-000120",
                neighborhood = "Maxaquene",
                wasteType = "Poda de árvores",
                reference = "Av. Julius Nyerere, próximo ao antigo Cine África.",
                phoneNumber = "+258 84 332 9900",
                otpVerified = true,
                status = "RESOLVED",
                priority = "LOW",
                assignedTeam = "Equipe Maxaquene",
                latitude = -25.9450,
                longitude = 32.5900,
                origin = "USSD",
                createdAt = now - (1 * oneDay),
                updatedAt = now - (6 * oneHour)
            ),
            ReportEntity(
                code = "DLX-250515-000119",
                neighborhood = "Sommerchield",
                wasteType = "Entulho / Construção",
                reference = "Av. Agostinho Neto, perto da padaria Phoenix.",
                phoneNumber = "+258 86 112 7788",
                otpVerified = true,
                status = "UNDER_ANALYSIS",
                priority = "MEDIUM",
                assignedTeam = "Equipe Central",
                latitude = -25.9670,
                longitude = 32.5930,
                origin = "APP",
                createdAt = now - (1 * oneDay + 4 * oneHour),
                updatedAt = now - (18 * oneHour)
            ),
            ReportEntity(
                code = "DLX-250515-000118",
                neighborhood = "Central \"B\"",
                wasteType = "Lixo hospitalar",
                reference = "Próximo à clínica privada, esquina com Av. Eduardo Mondlane.",
                phoneNumber = "+258 84 776 5544",
                otpVerified = true,
                status = "REJECTED",
                priority = "URGENT",
                assignedTeam = null,
                latitude = -25.9680,
                longitude = 32.5780,
                origin = "APP",
                createdAt = now - (2 * oneDay),
                updatedAt = now - (1 * oneDay)
            ),
            ReportEntity(
                code = "DLX-250515-000117",
                neighborhood = "Malhangalene",
                wasteType = "Lixo doméstico",
                reference = "Atrás do centro comunitário de Malhangalene.",
                phoneNumber = "+258 85 443 2211",
                otpVerified = true,
                status = "CLOSED",
                priority = "LOW",
                assignedTeam = "Equipe Limpeza Norte",
                latitude = -25.9420,
                longitude = 32.5740,
                origin = "USSD",
                createdAt = now - (3 * oneDay),
                updatedAt = now - (2 * oneDay)
            ),
            ReportEntity(
                code = "DLX-250515-000116",
                neighborhood = "Benfica",
                wasteType = "Lixo industrial",
                reference = "Estrada Nacional 1, perto do armazém da Coca-Cola.",
                phoneNumber = "+258 84 665 1199",
                otpVerified = true,
                status = "IN_PROGRESS",
                priority = "HIGH",
                assignedTeam = "Equipe Benfica",
                latitude = -25.8890,
                longitude = 32.5400,
                origin = "APP",
                createdAt = now - (3 * oneDay + 2 * oneHour),
                updatedAt = now - (1 * oneDay)
            )
        )

        for (report in reports) {
            reportDao.insertReport(report)
        }

        // Seed History
        val histories = listOf(
            ReportHistoryEntity(
                reportCode = "DLX-250515-000123",
                previousStatus = null,
                newStatus = "REGISTERED",
                observation = "Denúncia criada pelo cidadão.",
                changedAt = now - (3 * oneHour),
                author = "APP",
                source = "APP"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000123",
                previousStatus = "REGISTERED",
                newStatus = "UNDER_ANALYSIS",
                observation = "Denúncia recebida e em análise pela equipa técnica.",
                changedAt = now - (45 * 60_000L),
                author = "Administrador",
                source = "Admin"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000122",
                previousStatus = null,
                newStatus = "REGISTERED",
                observation = "Denúncia criada via terminal USSD *384*73407#.",
                changedAt = now - (5 * oneHour),
                author = "Cidadão",
                source = "USSD"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000121",
                previousStatus = null,
                newStatus = "REGISTERED",
                observation = "Denúncia criada pelo cidadão.",
                changedAt = now - (8 * oneHour),
                author = "Cidadão",
                source = "USSD"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000121",
                previousStatus = "REGISTERED",
                newStatus = "UNDER_ANALYSIS",
                observation = "Verificada viabilidade operacional pela equipa.",
                changedAt = now - (4 * oneHour),
                author = "Operador",
                source = "Admin"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000121",
                previousStatus = "UNDER_ANALYSIS",
                newStatus = "IN_PROGRESS",
                observation = "Equipa deslocada ao local. Limpeza em progresso.",
                changedAt = now - (2 * oneHour),
                author = "Equipa Mavalane",
                source = "Operações"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000120",
                previousStatus = null,
                newStatus = "REGISTERED",
                observation = "Denúncia criada pelo cidadão.",
                changedAt = now - (1 * oneDay),
                author = "Cidadão",
                source = "USSD"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000120",
                previousStatus = "REGISTERED",
                newStatus = "IN_PROGRESS",
                observation = "Camião de recolha de podas mobilizado.",
                changedAt = now - (18 * oneHour),
                author = "Supervisor",
                source = "Admin"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000120",
                previousStatus = "IN_PROGRESS",
                newStatus = "RESOLVED",
                observation = "Lixo e ramagens removidos por completo. Local limpo.",
                changedAt = now - (6 * oneHour),
                author = "Equipe Maxaquene",
                source = "Operações"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000118",
                previousStatus = null,
                newStatus = "REGISTERED",
                observation = "Denúncia submetida.",
                changedAt = now - (2 * oneDay),
                author = "APP",
                source = "APP"
            ),
            ReportHistoryEntity(
                reportCode = "DLX-250515-000118",
                previousStatus = "REGISTERED",
                newStatus = "REJECTED",
                observation = "Resíduos perigosos requerem protocolo de saúde especial; encaminhado ao Ministério da Saúde.",
                changedAt = now - (1 * oneDay),
                author = "Supervisor",
                source = "Admin"
            )
        )

        for (history in histories) {
            historyDao.insertHistory(history)
        }
    }
}
