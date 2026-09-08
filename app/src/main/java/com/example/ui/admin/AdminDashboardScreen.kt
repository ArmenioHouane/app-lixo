package com.example.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ReportRepository
import com.example.domain.model.ReportOrigin
import com.example.domain.model.ReportStatus
import com.example.ui.components.AppCard
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

@Composable
fun AdminDashboardScreen(
    repository: ReportRepository,
    onNavigateReports: () -> Unit,
    onNavigateMap: () -> Unit,
    onNavigateUsers: () -> Unit,
    onNavigateCitizen: () -> Unit
) {
    val metrics by repository.getDashboardMetrics().collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Centro de Operações Urbanas",
            subtitle = "Conselho Municipal de Maputo",
            actions = {
                Surface(
                    onClick = onNavigateCitizen,
                    color = AppColors.DangerLight,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = "Terminar Sessão",
                        style = AppTypography.Caption.copy(
                            color = AppColors.Danger,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            val m = metrics
            if (m != null) {
                // 4 KPIs in 2x2 grid
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KpiCard(
                        title = "Total Denúncias",
                        value = "${m.totalReports}",
                        growth = "+${m.totalGrowthPercent}%",
                        icon = Icons.Default.Assignment,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Pendentes",
                        value = "${m.pendingReports}",
                        growth = "+${m.pendingGrowthPercent}%",
                        icon = Icons.Default.HourglassTop,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KpiCard(
                        title = "Resolvidas",
                        value = "${m.resolvedReports}",
                        growth = "+${m.resolvedGrowthPercent}%",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Taxa SLA",
                        value = "${m.slaRatePercent}%",
                        growth = "+${m.slaGrowthPercent}%",
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Distribution by Status
                AppCard(
                    title = "Distribuição por Estado",
                    subtitle = "Monitorização de fluxos em tempo real"
                ) {
                    val total = if (m.totalReports > 0) m.totalReports.toFloat() else 1f

                    for (status in ReportStatus.values()) {
                        val count = m.statusDistribution[status] ?: 0
                        val progress = count / total
                        val barColor = when (status) {
                            ReportStatus.REGISTERED -> AppColors.StatusRegistered
                            ReportStatus.UNDER_ANALYSIS -> AppColors.StatusUnderAnalysis
                            ReportStatus.IN_PROGRESS -> AppColors.StatusInProgress
                            ReportStatus.RESOLVED -> AppColors.StatusResolved
                            ReportStatus.CLOSED -> AppColors.StatusClosed
                            ReportStatus.REJECTED -> AppColors.StatusRejected
                        }

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(barColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = status.label,
                                        style = AppTypography.Caption.copy(fontWeight = FontWeight.Medium)
                                    )
                                }
                                Text(
                                    text = "$count (${(progress * 100).toInt()}%)",
                                    style = AppTypography.Caption.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                color = barColor,
                                trackColor = AppColors.Border.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(999.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Critical Neighborhoods and Channels
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppCard(
                        title = "Bairros Críticos",
                        modifier = Modifier.weight(1f)
                    ) {
                        m.topNeighborhoods.take(4).forEach { (bairro, count) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Text(
                                    text = bairro,
                                    style = AppTypography.Caption,
                                    maxLines = 1
                                )
                                Text(
                                    text = "$count",
                                    style = AppTypography.Caption.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    AppCard(
                        title = "Canais",
                        modifier = Modifier.weight(1f)
                    ) {
                        val appCount = m.originDistribution[ReportOrigin.APP] ?: 0
                        val ussdCount = m.originDistribution[ReportOrigin.USSD] ?: 0

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(text = "App Mobile", style = AppTypography.Caption)
                            Text(text = "$appCount", style = AppTypography.Caption.copy(fontWeight = FontWeight.Bold))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(text = "Canal USSD", style = AppTypography.Caption)
                            Text(text = "$ussdCount", style = AppTypography.Caption.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Navigation Shortcuts
                AppCard(title = "Gestão Operacional") {
                    AdminShortcutRow(
                        title = "Gestão de Denúncias",
                        subtitle = "Gerir e atribuir equipas de limpeza",
                        icon = Icons.Default.Assignment,
                        onClick = onNavigateReports,
                        testTag = "admin_goto_reports"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminShortcutRow(
                        title = "Mapa de Georreferenciação",
                        subtitle = "Visualização cartográfica dos focos de lixo",
                        icon = Icons.Default.Map,
                        onClick = onNavigateMap,
                        testTag = "admin_goto_map"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminShortcutRow(
                        title = "Equipa & Fiscais",
                        subtitle = "Controlo de operadores no terreno",
                        icon = Icons.Default.People,
                        onClick = onNavigateUsers,
                        testTag = "admin_goto_users"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    growth: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppColors.Surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AppColors.Border),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.PrimaryLight)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.PrimaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = growth,
                        style = AppTypography.Caption.copy(
                            color = AppColors.Success,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = AppTypography.Display.copy(fontSize = 22.sp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                style = AppTypography.Caption.copy(color = AppColors.TextSecondary),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AdminShortcutRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = AppColors.SurfaceMuted,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.Border),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColors.PrimaryLight)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.PrimaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = subtitle,
                        style = AppTypography.Caption.copy(
                            color = AppColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
