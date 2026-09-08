package com.example.domain.model

enum class ReportStatus(val label: String) {
    REGISTERED("Registada"),
    UNDER_ANALYSIS("Em análise"),
    IN_PROGRESS("Em andamento"),
    RESOLVED("Resolvida"),
    CLOSED("Encerrada"),
    REJECTED("Rejeitada")
}

enum class ReportOrigin(val label: String) {
    APP("APP"),
    USSD("USSD")
}

enum class ReportPriority(val label: String) {
    LOW("Baixa"),
    MEDIUM("Média"),
    HIGH("Alta"),
    URGENT("Urgente")
}

enum class UserRole(val label: String) {
    ADMIN("Administrador"),
    OPERATOR("Operador"),
    SUPERVISOR("Supervisor"),
    INSPECTOR("Fiscal")
}
