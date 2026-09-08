package com.example.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.local.dao.ReportDao
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Cria um ficheiro real no directório de ficheiros da aplicação (files/records/)
 * e devolve um content:// Uri via FileProvider, pronto para ser usado pela câmara.
 */
object MediaStoreHelper {
    private const val RECORDS_DIR = "records"
    private const val AUTHORITY_SUFFIX = ".fileprovider"

    fun createMediaFile(context: Context, isVideo: Boolean): Pair<File, Uri> {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = if (isVideo) "VID_$timestamp.mp4" else "IMG_$timestamp.jpg"
        val dir = File(context.filesDir, RECORDS_DIR).apply { if (!exists()) mkdirs() }
        val file = File(dir, fileName)

        val authority = "${context.packageName}$AUTHORITY_SUFFIX"
        val uri = FileProvider.getUriForFile(context, authority, file)
        return file to uri
    }

    fun fileExists(context: Context, uriString: String?): Boolean {
        if (uriString.isNullOrBlank()) return false
        val uri = Uri.parse(uriString)
        return when {
            uriString.startsWith("file://") -> File(uri.path ?: "").exists()

            // Ficheiro criado pela app via FileProvider (captura da câmara)
            uri.scheme == "content" && uri.authority == "${context.packageName}$AUTHORITY_SUFFIX" -> {
                File(File(context.filesDir, RECORDS_DIR), uri.lastPathSegment ?: "").exists()
            }

            // Anexo da galeria (Photo Picker) — gerido pelo sistema, assumimos disponível
            uri.scheme == "content" -> true

            // URIs simulados/legados ("local://...") — tratados como disponíveis
            else -> true
        }
    }
}

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
