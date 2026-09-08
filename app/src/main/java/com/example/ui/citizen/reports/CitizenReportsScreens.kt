package com.example.ui.citizen.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ReportRepository
import com.example.domain.model.ReportStatus
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.AppChip
import com.example.ui.components.AppTextField
import com.example.ui.components.ButtonVariant
import com.example.ui.components.EmptyState
import com.example.ui.components.InfoRow
import com.example.ui.components.ReportCard
import com.example.ui.components.ScreenHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TimelineItem
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography
import com.example.util.DateFormatter

// ==========================================
// 1. CITIZEN REPORT LIST SCREEN
// ==========================================
@Composable
fun CitizenReportListScreen(
    repository: ReportRepository,
    onNavigateDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf<ReportStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchDialog by remember { mutableStateOf(false) }

    val rawReports by repository.getReportsFiltered(selectedStatus, searchQuery).collectAsState(initial = emptyList())
    val allReportsCount by repository.getAllReports().collectAsState(initial = emptyList())

    // Most recent reports sorted descending by createdAt
    val reports = remember(rawReports) {
        rawReports.sortedByDescending { it.createdAt }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Minhas denúncias",
            subtitle = "Total: ${allReportsCount.size} ocorrências registadas",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack,
            actions = {
                IconButton(
                    onClick = { showSearchDialog = true },
                    modifier = Modifier.testTag("search_reports_header_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Consultar código",
                        tint = Color.White
                    )
                }
            }
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppChip(
                    text = "Todos",
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null },
                    count = allReportsCount.size
                )
                for (status in ReportStatus.values()) {
                    val count = allReportsCount.count { it.status == status }
                    AppChip(
                        text = status.label,
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status },
                        count = count
                    )
                }
            }

            if (reports.isEmpty()) {
                EmptyState(
                    title = "Nenhuma denúncia encontrada",
                    message = if (selectedStatus != null) {
                        "Não existem denúncias com o estado \"${selectedStatus?.label}\"."
                    } else {
                        "Ainda não registou nenhuma denúncia de lixo na cidade de Maputo."
                    },
                    icon = Icons.Default.Inbox,
                    actionText = if (selectedStatus != null) "Limpar filtro" else null,
                    onActionClick = { selectedStatus = null },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AppColors.TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ordenadas por mais recentes primeiro",
                                style = AppTypography.Caption.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextSecondary
                                )
                            )
                        }
                    }

                    items(reports, key = { it.code }) { report ->
                        ReportCard(
                            report = report,
                            onClick = { onNavigateDetail(report.code) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    if (showSearchDialog) {
        SearchReportDialog(
            repository = repository,
            onDismiss = { showSearchDialog = false },
            onReportSelected = { code ->
                showSearchDialog = false
                onNavigateDetail(code)
            }
        )
    }
}

// ==========================================
// 2. CITIZEN REPORT DETAIL SCREEN
// ==========================================
@Composable
fun CitizenReportDetailScreen(
    reportCode: String,
    repository: ReportRepository,
    onNavigateBack: () -> Unit,
    onNavigateHistory: (String) -> Unit
) {
    val report by repository.getReportByCode(reportCode).collectAsState(initial = null)
    val history by repository.getReportHistory(reportCode).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Detalhes da denúncia",
            subtitle = reportCode,
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        if (report == null) {
            EmptyState(
                title = "Denúncia não encontrada",
                message = "Não encontrámos nenhuma denúncia com o código $reportCode.",
                icon = Icons.Default.SearchOff,
                actionText = "Voltar",
                onActionClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            )
        } else {
            val r = report!!

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Code and Status Card
                AppCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = r.code,
                            style = AppTypography.Title.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        )
                        StatusBadge(status = r.status)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Visual Evidence Card if media is present
                if (r.mediaUri != null) {
                    AppCard(
                        title = "Evidência Multimédia Anexada",
                        subtitle = "Ficheiro guardado localmente no dispositivo"
                    ) {
                        Surface(
                            color = AppColors.PrimaryLight,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AppColors.Primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppColors.Primary)
                                ) {
                                    Icon(
                                        imageVector = if (r.mediaType == "VIDEO") Icons.Default.PlayCircleOutline else Icons.Default.Image,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (r.mediaType == "VIDEO") "Registo de Vídeo Gravado" else "Fotografia do Local Registada",
                                        style = AppTypography.Subtitle.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.PrimaryDark
                                        )
                                    )
                                    Text(
                                        text = "Evidência pronta para análise da brigada municipal",
                                        style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // General Information Card
                AppCard(title = "Informações gerais") {
                    InfoRow(label = "Bairro", value = r.neighborhood, icon = Icons.Default.Place)
                    InfoRow(label = "Tipo de problema", value = r.wasteType, icon = Icons.Default.Category)
                    InfoRow(label = "Referência", value = r.reference, icon = Icons.Default.ShortText)
                    InfoRow(label = "Telefone", value = r.phoneNumber, icon = Icons.Default.Phone)
                    InfoRow(
                        label = "Registada em",
                        value = DateFormatter.formatDateTime(r.createdAt),
                        icon = Icons.Default.CalendarMonth
                    )
                    InfoRow(
                        label = "Última actualização",
                        value = DateFormatter.formatDateTime(r.updatedAt),
                        icon = Icons.Default.Update
                    )
                    InfoRow(
                        label = "Origem",
                        value = r.origin.name,
                        icon = Icons.Default.Public,
                        showDivider = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recent History preview card
                AppCard(
                    title = "Histórico de acompanhamento",
                    subtitle = "Últimas actualizações da denúncia"
                ) {
                    if (history.isEmpty()) {
                        Text(
                            text = "A aguardar actualização da equipa técnica municipal.",
                            style = AppTypography.Caption.copy(color = AppColors.TextMuted)
                        )
                    } else {
                        val preview = history.take(3)
                        preview.forEachIndexed { index, item ->
                            TimelineItem(
                                history = item,
                                isLast = index == preview.size - 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AppButton(
                        text = "Ver histórico completo",
                        onClick = { onNavigateHistory(r.code) },
                        variant = ButtonVariant.SECONDARY,
                        leadingIcon = Icons.Default.History,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ==========================================
// 3. CITIZEN HISTORY SCREEN
// ==========================================
@Composable
fun CitizenHistoryScreen(
    reportCode: String,
    repository: ReportRepository,
    onNavigateBack: () -> Unit
) {
    val history by repository.getReportHistory(reportCode).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Histórico da denúncia",
            subtitle = reportCode,
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        if (history.isEmpty()) {
            EmptyState(
                title = "Sem histórico disponível",
                message = "Ainda não foram registadas alterações de estado para esta denúncia.",
                icon = Icons.Default.History,
                actionText = "Voltar",
                onActionClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                item {
                    AppCard(
                        title = "Linha temporal da ocorrência",
                        subtitle = "${history.size} actualizações registadas"
                    ) {
                        history.forEachIndexed { index, item ->
                            TimelineItem(
                                history = item,
                                isLast = index == history.size - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. SEARCH REPORT DIALOG (Com Denúncias Recentes)
// ==========================================
@Composable
fun SearchReportDialog(
    repository: ReportRepository? = null,
    onDismiss: () -> Unit,
    onReportSelected: (String) -> Unit
) {
    var searchCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val recentReports by (repository?.getAllReports()?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf(emptyList()) })

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Consultar denúncia", style = AppTypography.Title)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Insira o código ou escolha uma das denúncias recentes abaixo.",
                    style = AppTypography.Body.copy(color = AppColors.TextSecondary)
                )

                Spacer(modifier = Modifier.height(14.dp))

                AppTextField(
                    value = searchCode,
                    onValueChange = {
                        searchCode = it.uppercase()
                        errorMessage = null
                    },
                    label = "Código da denúncia",
                    placeholder = "DLX-250515-000123",
                    leadingIcon = Icons.Default.Search,
                    errorMessage = errorMessage,
                    singleLine = true
                )

                // Recent reports if available
                if (recentReports.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Denúncias Recentes no Dispositivo:",
                        style = AppTypography.Subtitle.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val topRecents = recentReports.sortedByDescending { it.createdAt }.take(4)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        topRecents.forEach { report ->
                            Surface(
                                onClick = { onReportSelected(report.code) },
                                shape = RoundedCornerShape(10.dp),
                                color = AppColors.SurfaceMuted,
                                border = BorderStroke(1.dp, AppColors.Border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = report.code,
                                            style = AppTypography.Subtitle.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = AppColors.Primary,
                                                fontSize = 15.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${report.neighborhood} • ${report.wasteType}",
                                            style = AppTypography.Caption,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StatusBadge(status = report.status)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            AppButton(
                text = "Consultar",
                onClick = {
                    val code = searchCode.trim().uppercase()
                    if (code.isBlank()) {
                        errorMessage = "Por favor, digite um código ou escolha uma recente."
                    } else {
                        onReportSelected(code)
                    }
                }
            )
        },
        dismissButton = {
            AppButton(
                text = "Fechar",
                onClick = onDismiss,
                variant = ButtonVariant.GHOST
            )
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = AppColors.Surface
    )
}
