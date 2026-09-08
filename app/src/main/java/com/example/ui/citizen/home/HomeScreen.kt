package com.example.ui.citizen.home

import android.app.Activity
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ReportRepository
import com.example.ui.citizen.reports.SearchReportDialog
import com.example.ui.components.AppCard
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

@Composable
fun HomeScreen(
    repository: ReportRepository,
    onNavigateNewReport: () -> Unit,
    onNavigateReports: () -> Unit,
    onNavigateReportDetail: (String) -> Unit,
    onNavigateInfo: () -> Unit,
    onNavigateMap: () -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateAdmin: () -> Unit = {}
) {
    val context = LocalContext.current
    var showSearchDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Screen Header
        ScreenHeader(
            title = "Denúncia de Lixo Maputo",
            subtitle = "Conselho Municipal • Salubridade Urbana",
            leadingBadgeIcon = Icons.Default.DeleteSweep
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            AppCard(
                title = "Serviços ao Munícipe",
                subtitle = "Selecione uma das opções para prosseguir"
            ) {
                HomeActionRow(
                    title = "Nova denúncia",
                    subtitle = "Registar acumulação de resíduos no seu bairro",
                    icon = Icons.Default.PostAdd,
                    onClick = onNavigateNewReport,
                    testTag = "home_new_report_action"
                )

                Spacer(modifier = Modifier.height(12.dp))

                HomeActionRow(
                    title = "Consultar denúncia",
                    subtitle = "Pesquisar por código ou consultar recentes",
                    icon = Icons.Default.Search,
                    onClick = { showSearchDialog = true },
                    testTag = "home_search_report_action"
                )

                Spacer(modifier = Modifier.height(12.dp))

                HomeActionRow(
                    title = "Mapa de Ocorrências",
                    subtitle = "Visualização cartográfica dos pontos de lixo",
                    icon = Icons.Default.Map,
                    onClick = onNavigateMap,
                    testTag = "home_map_action"
                )

                Spacer(modifier = Modifier.height(12.dp))

                HomeActionRow(
                    title = "Informações do Sistema",
                    subtitle = "Estatísticas de recolha e dados municipais",
                    icon = Icons.Default.Info,
                    onClick = onNavigateInfo,
                    testTag = "home_info_action"
                )

                Spacer(modifier = Modifier.height(12.dp))

                HomeActionRow(
                    title = "Terminar Sessão",
                    subtitle = "Sair da área do munícipe",
                    icon = Icons.Default.ExitToApp,
                    isDanger = true,
                    onClick = { showExitDialog = true },
                    testTag = "home_exit_action"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Municipal institutional footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Conselho Municipal de Maputo • Salubridade Urbana",
                    style = AppTypography.Caption.copy(
                        color = AppColors.TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showSearchDialog) {
        SearchReportDialog(
            repository = repository,
            onDismiss = { showSearchDialog = false },
            onReportSelected = { code ->
                showSearchDialog = false
                onNavigateReportDetail(code)
            }
        )
    }

    if (showExitDialog) {
        ConfirmationDialog(
            title = "Terminar Sessão?",
            description = "Tem a certeza de que deseja encerrar a sua sessão de munícipe e regressar ao ecrã inicial?",
            confirmText = "Terminar Sessão",
            cancelText = "Cancelar",
            isDanger = true,
            onConfirm = {
                showExitDialog = false
                onNavigateLogin()
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

@Composable
fun HomeActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDanger: Boolean = false,
    testTag: String = "home_action_row"
) {
    val iconBgColor = if (isDanger) AppColors.DangerLight else AppColors.PrimaryLight
    val iconColor = if (isDanger) AppColors.Danger else AppColors.PrimaryDark
    val titleColor = if (isDanger) AppColors.Danger else AppColors.TextPrimary

    Surface(
        color = AppColors.Surface,
        shape = RoundedCornerShape(14.dp),
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
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        style = AppTypography.Subtitle.copy(
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        style = AppTypography.Caption.copy(
                            color = AppColors.TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
