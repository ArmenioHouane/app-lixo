package com.example.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ReportRepository
import com.example.domain.model.Report
import com.example.domain.model.ReportPriority
import com.example.domain.model.ReportStatus
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.AppChip
import com.example.ui.components.AppTextField
import com.example.ui.components.ButtonVariant
import com.example.ui.components.EmptyState
import com.example.ui.components.InfoRow
import com.example.ui.components.ScreenHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TimelineItem
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography
import com.example.util.DateFormatter
import com.example.util.MediaStoreHelper
import kotlinx.coroutines.launch

@Composable
fun AdminReportsScreen(
    repository: ReportRepository,
    onNavigateDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<ReportStatus?>(null) }

    val rawReports by repository.getReportsFiltered(selectedStatus, searchQuery).collectAsState(initial = emptyList())
    val allReports by repository.getAllReports().collectAsState(initial = emptyList())
    val reports = remember(rawReports) { rawReports.sortedByDescending { it.createdAt } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Gestão de Denúncias",
            subtitle = "Filtrar, atribuir e despachar casos",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Pesquisa avançada",
                placeholder = "Pesquisar por código, bairro ou tipo...",
                leadingIcon = Icons.Default.Search,
                singleLine = true
            )
        }

        // Horizontal status filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppChip(
                text = "Todas",
                selected = selectedStatus == null,
                onClick = { selectedStatus = null },
                count = allReports.size
            )
            for (status in ReportStatus.values()) {
                val count = allReports.count { it.status == status }
                AppChip(
                    text = status.label,
                    selected = selectedStatus == status,
                    onClick = { selectedStatus = status },
                    count = count
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (reports.isEmpty()) {
            EmptyState(
                title = "Nenhuma denúncia encontrada",
                message = "Nenhum resultado corresponde aos critérios de pesquisa seleccionados.",
                icon = Icons.Default.Inbox,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(reports, key = { it.code }) { report ->
                    AdminReportListItem(
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

@Composable
fun AdminReportListItem(
    report: Report,
    onClick: () -> Unit
) {
    AppCard(onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = report.code,
                    style = AppTypography.Subtitle.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                PriorityBadge(priority = report.priority)
            }

            StatusBadge(status = report.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${report.neighborhood} • ${report.wasteType}",
            style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = report.reference,
            style = AppTypography.Caption,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = report.assignedTeam ?: "Sem equipa atribuída",
                    style = AppTypography.Caption.copy(
                        color = if (report.assignedTeam != null) AppColors.PrimaryDark else AppColors.TextMuted,
                        fontWeight = if (report.assignedTeam != null) FontWeight.Medium else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                )
            }

            Text(
                text = "${DateFormatter.formatDate(report.createdAt)} • ${report.origin.name}",
                style = AppTypography.Caption.copy(color = AppColors.TextMuted, fontSize = 11.sp)
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: ReportPriority) {
    val (bgColor, textColor) = when (priority) {
        ReportPriority.LOW -> Pair(AppColors.Border.copy(alpha = 0.5f), AppColors.TextSecondary)
        ReportPriority.MEDIUM -> Pair(AppColors.StatusUnderAnalysisBg, AppColors.StatusUnderAnalysis)
        ReportPriority.HIGH -> Pair(AppColors.StatusInProgressBg, AppColors.StatusInProgress)
        ReportPriority.URGENT -> Pair(AppColors.StatusRejectedBg, AppColors.StatusRejected)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = priority.label,
            style = AppTypography.Label.copy(color = textColor, fontSize = 10.sp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

// ==========================================
// ADMIN REPORT DETAIL SCREEN
// ==========================================
private val MunicipalTeams = listOf(
    "Equipa KaMpfumu A",
    "Equipa KaMaxakeni B",
    "Equipa KaMavota C",
    "Equipa KaMubukwana D",
    "Brigada de Intervenção Rápida"
)

@Composable
fun AdminReportDetailScreen(
    reportCode: String,
    repository: ReportRepository,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val report by repository.getReportByCode(reportCode).collectAsState(initial = null)
    val history by repository.getReportHistory(reportCode).collectAsState(initial = emptyList())
    val context = LocalContext.current

    var showStatusDialog by remember { mutableStateOf(false) }
    var showTeamDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Despacho Operacional",
            subtitle = reportCode,
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        if (report == null) {
            EmptyState(
                title = "Denúncia não encontrada",
                message = "Código inexistente no sistema.",
                icon = Icons.Default.Inbox,
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
                // Header status action card
                AppCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = r.code,
                                style = AppTypography.Title.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.Primary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            PriorityBadge(priority = r.priority)
                        }

                        StatusBadge(status = r.status)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppButton(
                            text = "Alterar Estado",
                            onClick = { showStatusDialog = true },
                            leadingIcon = Icons.Default.Edit,
                            modifier = Modifier.weight(1f),
                            testTag = "admin_change_status_button"
                        )

                        AppButton(
                            text = "Atribuir Equipa",
                            onClick = { showTeamDialog = true },
                            variant = ButtonVariant.SECONDARY,
                            leadingIcon = Icons.Default.AssignmentInd,
                            modifier = Modifier.weight(1f),
                            testTag = "admin_assign_team_button"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Visual Evidence Card if present
                if (r.mediaUri != null) {
                    val fileName = r.mediaUri?.substringAfterLast("/") ?: ""
                    val isFileAvailable = MediaStoreHelper.fileExists(context, r.mediaUri)

                    AppCard(
                        title = "Evidência Multimédia Anexada",
                        subtitle = if (isFileAvailable) {
                            "Submetido pelo munícipe no local"
                        } else {
                            "Ficheiro já não disponível no dispositivo do munícipe"
                        }
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (r.mediaType == "VIDEO") "Vídeo do Foco de Lixo Anexado" else "Fotografia do Foco de Lixo Anexada",
                                        style = AppTypography.Subtitle.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.PrimaryDark
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = fileName,
                                        style = AppTypography.Caption.copy(
                                            fontSize = 11.sp,
                                            color = AppColors.TextSecondary
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isFileAvailable) {
                                            "Ficheiro local disponível para consulta e vistoria"
                                        } else {
                                            "Ficheiro local indisponível para vistoria"
                                        },
                                        style = AppTypography.Caption.copy(
                                            fontSize = 11.sp,
                                            color = if (isFileAvailable) AppColors.TextSecondary else AppColors.Danger
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Detail Information
                AppCard(title = "Dados da ocorrência") {
                    InfoRow(label = "Bairro", value = r.neighborhood, icon = Icons.Default.Place)
                    InfoRow(label = "Tipo", value = r.wasteType, icon = Icons.Default.Category)
                    InfoRow(label = "Referência", value = r.reference, icon = Icons.Default.ShortText)
                    InfoRow(label = "Telefone cidadão", value = r.phoneNumber, icon = Icons.Default.Phone)
                    InfoRow(
                        label = "Equipa atribuída",
                        value = r.assignedTeam ?: "Nenhuma (pendente)",
                        icon = Icons.Default.Group,
                        valueColor = if (r.assignedTeam != null) AppColors.PrimaryDark else AppColors.Danger
                    )
                    InfoRow(
                        label = "Coordenadas",
                        value = "${r.latitude ?: -25.96}, ${r.longitude ?: 32.57}",
                        icon = Icons.Default.LocationOn
                    )
                    InfoRow(
                        label = "Criada em",
                        value = DateFormatter.formatDateTime(r.createdAt),
                        icon = Icons.Default.CalendarMonth
                    )
                    InfoRow(
                        label = "Origem",
                        value = r.origin.name,
                        icon = Icons.Default.Public,
                        showDivider = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Real-time Audit Timeline
                AppCard(
                    title = "Histórico de auditoria",
                    subtitle = "Registo de todas as transições de estado"
                ) {
                    if (history.isEmpty()) {
                        Text(
                            text = "Sem histórico disponível.",
                            style = AppTypography.Caption.copy(color = AppColors.TextMuted)
                        )
                    } else {
                        history.forEachIndexed { index, item ->
                            TimelineItem(
                                history = item,
                                isLast = index == history.size - 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Dialog for Status change
            if (showStatusDialog) {
                StatusChangeDialog(
                    currentStatus = r.status,
                    onDismiss = { showStatusDialog = false },
                    onConfirm = { newStatus, observation ->
                        showStatusDialog = false
                        scope.launch {
                            repository.updateReportStatus(
                                reportCode = r.code,
                                newStatus = newStatus,
                                observation = observation,
                                author = "Operador Municipal (Centro CMM)",
                                source = "Admin"
                            )
                        }
                    }
                )
            }

            // Dialog for Team Assignment
            if (showTeamDialog) {
                TeamAssignmentDialog(
                    currentTeam = r.assignedTeam,
                    onDismiss = { showTeamDialog = false },
                    onConfirm = { selectedTeam ->
                        showTeamDialog = false
                        scope.launch {
                            repository.updateReportTeam(r.code, selectedTeam)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StatusChangeDialog(
    currentStatus: ReportStatus,
    onDismiss: () -> Unit,
    onConfirm: (ReportStatus, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var observation by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Actualizar Estado", style = AppTypography.Title)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "Seleccione o novo estado da ocorrência:",
                    style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                ReportStatus.values().forEach { status ->
                    val isSelected = selectedStatus == status
                    Surface(
                        color = if (isSelected) AppColors.PrimaryLight else AppColors.Surface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedStatus = status }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            StatusBadge(status = status)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                AppTextField(
                    value = observation,
                    onValueChange = {
                        observation = it
                        errorMessage = null
                    },
                    label = "Observação operacional",
                    placeholder = "Ex: Equipa despachada para o local com camião compactador...",
                    singleLine = false,
                    maxLines = 3,
                    errorMessage = errorMessage
                )
            }
        },
        confirmButton = {
            AppButton(
                text = "Confirmar",
                onClick = {
                    if (observation.isBlank()) {
                        errorMessage = "Insira uma observação para auditoria."
                    } else {
                        onConfirm(selectedStatus, observation)
                    }
                }
            )
        },
        dismissButton = {
            AppButton(
                text = "Cancelar",
                onClick = onDismiss,
                variant = ButtonVariant.GHOST
            )
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = AppColors.Surface
    )
}

@Composable
fun TeamAssignmentDialog(
    currentTeam: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedTeam by remember { mutableStateOf(currentTeam ?: MunicipalTeams.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Atribuir Equipa de Limpeza", style = AppTypography.Title)
        },
        text = {
            Column {
                Text(
                    text = "Seleccione a brigada responsável pela recolha:",
                    style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                MunicipalTeams.forEach { team ->
                    val isSelected = selectedTeam == team
                    Surface(
                        color = if (isSelected) AppColors.PrimaryLight else AppColors.Surface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedTeam = team }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = if (isSelected) AppColors.PrimaryDark else AppColors.TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = team,
                                style = AppTypography.Body.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.PrimaryDark else AppColors.TextPrimary
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            AppButton(
                text = "Atribuir",
                onClick = { onConfirm(selectedTeam) }
            )
        },
        dismissButton = {
            AppButton(
                text = "Cancelar",
                onClick = onDismiss,
                variant = ButtonVariant.GHOST
            )
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = AppColors.Surface
    )
}
