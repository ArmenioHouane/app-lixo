package com.example.util

import com.example.data.local.dao.ReportDao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object CodeGenerator {
    suspend fun generateUniqueCode(reportDao: ReportDao): String {
        val dateFormat = SimpleDateFormat("yyMMdd", Locale.getDefault())
        val datePart = dateFormat.format(Date())
        val chars = "0123456789"
        
        var code: String
        var attempts = 0
        do {
            val randomSuffix = (1..6)
                .map { chars[Random.nextInt(chars.length)] }
                .joinToString("")
            code = "DLX-$datePart-$randomSuffix"
            attempts++
        } while (reportDao.existsByCode(code) && attempts < 20)
        
        return code
    }
}

object DateFormatter {
    private val fullDateTimeFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale("pt", "PT"))
    private val shortDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "PT"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "PT"))

    fun formatDateTime(timestamp: Long): String {
        return fullDateTimeFormat.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        return shortDateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
}
