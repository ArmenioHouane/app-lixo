package com.example.ui.citizen.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.InfoRow
import com.example.ui.components.ScreenHeader
import com.example.ui.components.WizardProgress
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

@Composable
fun Step5ConfirmScreen(
    viewModel: WizardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateSuccess: (String) -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    val displayNeighborhood = if (draft.selectedNeighborhood == "Outro bairro") {
        draft.customNeighborhood.ifBlank { "Bairro Personalizado" }
    } else {
        draft.selectedNeighborhood
    }

    val displayWasteType = if (draft.selectedWasteType == "Outro tipo") {
        draft.customWasteType.ifBlank { "Resíduos Diversos" }
    } else {
        draft.selectedWasteType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Nova denúncia",
            subtitle = "Passo 5 de 5",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        WizardProgress(currentStep = 5, totalSteps = 5)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Confirme os dados",
                style = AppTypography.Display
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Revise as informações antes de enviar.",
                style = AppTypography.Body.copy(color = AppColors.TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppCard {
                InfoRow(
                    label = "Bairro",
                    value = displayNeighborhood,
                    icon = Icons.Default.Place
                )
                InfoRow(
                    label = "Tipo de problema",
                    value = displayWasteType,
                    icon = Icons.Default.Category
                )
                InfoRow(
                    label = "Referência",
                    value = draft.reference,
                    icon = Icons.Default.ShortText
                )
                InfoRow(
                    label = "Telefone",
                    value = draft.phoneNumber,
                    icon = Icons.Default.Phone
                )
                if (draft.mediaUri != null) {
                    InfoRow(
                        label = "Evidência",
                        value = buildString {
                            append(if (draft.mediaType == "VIDEO") "Vídeo anexado" else "Fotografia anexada")
                            append(" • ")
                            append(draft.mediaUri?.substringAfterLast("/") ?: "ficheiro local")
                        },
                        icon = Icons.Default.CameraAlt,
                        valueColor = AppColors.PrimaryDark
                    )
                }
                InfoRow(
                    label = "Verificação",
                    value = "OTP confirmado",
                    icon = Icons.Default.CheckCircle,
                    valueColor = AppColors.Success,
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "Confirmar denúncia",
                onClick = {
                    viewModel.submitReport { code ->
                        onNavigateSuccess(code)
                    }
                },
                isLoading = draft.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                testTag = "confirm_report_button"
            )
        }
    }
}
