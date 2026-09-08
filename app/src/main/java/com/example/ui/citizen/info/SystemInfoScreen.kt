package com.example.ui.citizen.info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ReportRepository
import com.example.domain.model.ReportStatus
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography
import kotlinx.coroutines.launch

@Composable
fun SystemInfoScreen(
    repository: ReportRepository,
    onNavigateBack: () -> Unit
) {
    val metrics by repository.getDashboardMetrics().collectAsState(initial = null)
    val settings by repository.getSettings().collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Informações do sistema",
            subtitle = "Conselho Municipal de Maputo",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // About Card
            AppCard(title = "Sobre o sistema") {
                Text(
                    text = "O Denúncia de Lixo Maputo permite registar e acompanhar casos de lixo na cidade de Maputo, com cobertura para todos os distritos municipais.",
                    style = AppTypography.Body.copy(color = AppColors.TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MiniInfoBox(
                        label = "Código do serviço",
                        value = settings?.serviceCode ?: "*384*73407#",
                        modifier = Modifier.weight(1f)
                    )
                    MiniInfoBox(
                        label = "Estado",
                        value = "Operacional",
                        valueColor = AppColors.Success,
                        modifier = Modifier.weight(1f)
                    )
                    MiniInfoBox(
                        label = "Versão",
                        value = settings?.appVersion ?: "1.0.0",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Local statistics card
            AppCard(title = "Estatísticas municipais") {
                val m = metrics
                if (m != null) {
                    Text(
                        text = "Por estado",
                        style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    for (status in ReportStatus.values()) {
                        val count = m.statusDistribution[status] ?: 0
                        val dotColor = when (status) {
                            ReportStatus.REGISTERED -> AppColors.StatusRegistered
                            ReportStatus.UNDER_ANALYSIS -> AppColors.StatusUnderAnalysis
                            ReportStatus.IN_PROGRESS -> AppColors.StatusInProgress
                            ReportStatus.RESOLVED -> AppColors.StatusResolved
                            ReportStatus.CLOSED -> AppColors.StatusClosed
                            ReportStatus.REJECTED -> AppColors.StatusRejected
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = status.label,
                                    style = AppTypography.Body.copy(color = AppColors.TextSecondary)
                                )
                            }
                            Text(
                                text = "$count",
                                style = AppTypography.Body.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Top bairros",
                        style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    for ((neighborhood, count) in m.topNeighborhoods) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = neighborhood,
                                style = AppTypography.Body.copy(color = AppColors.TextSecondary)
                            )
                            Text(
                                text = "$count",
                                style = AppTypography.Body.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Danger Reset Card
            Surface(
                color = AppColors.DangerLight.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AppColors.Danger.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Repor dados locais",
                        style = AppTypography.Subtitle.copy(
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Danger
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Atenção: esta ação não pode ser desfeita.",
                        style = AppTypography.Caption.copy(color = AppColors.Danger)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AppButton(
                        text = "Limpar base local",
                        onClick = { showResetDialog = true },
                        variant = ButtonVariant.DANGER,
                        leadingIcon = Icons.Default.DeleteSweep,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showResetDialog) {
        ConfirmationDialog(
            title = "Limpar todos os dados?",
            description = "Esta acção irá eliminar permanentemente todas as denúncias, histórico e registos locais. Não poderá ser desfeita.",
            confirmText = "Sim, limpar tudo",
            cancelText = "Cancelar",
            isDanger = true,
            onConfirm = {
                showResetDialog = false
                scope.launch {
                    repository.deleteAllData()
                }
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
fun MiniInfoBox(
    label: String,
    value: String,
    valueColor: Color = AppColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppColors.SurfaceMuted,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, AppColors.Border),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = label,
                style = AppTypography.Caption.copy(
                    color = AppColors.TextMuted,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = AppTypography.Label.copy(
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
