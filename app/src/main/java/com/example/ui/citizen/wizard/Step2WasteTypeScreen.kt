package com.example.ui.citizen.wizard

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppButton
import com.example.ui.components.AppTextField
import com.example.ui.components.ScreenHeader
import com.example.ui.components.WizardProgress
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

data class WasteTypeOption(
    val title: String,
    val icon: ImageVector
)

private val WasteTypeOptions = listOf(
    WasteTypeOption("Lixo doméstico", Icons.Default.DeleteOutline),
    WasteTypeOption("Entulho / Construção", Icons.Default.Build),
    WasteTypeOption("Lixo hospitalar", Icons.Default.LocalHospital),
    WasteTypeOption("Lixo industrial", Icons.Default.PrecisionManufacturing),
    WasteTypeOption("Poda de árvores", Icons.Default.Nature),
    WasteTypeOption("Outro tipo", Icons.Default.Category)
)

@Composable
fun Step2WasteTypeScreen(
    viewModel: WizardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Nova denúncia",
            subtitle = "Passo 2 de 5",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        WizardProgress(currentStep = 2, totalSteps = 5)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Qual é o tipo de problema?",
                style = AppTypography.Display
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecione o tipo de lixo ou problema.",
                style = AppTypography.Body.copy(color = AppColors.TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(WasteTypeOptions) { option ->
                    val isSelected = draft.selectedWasteType == option.title

                    Surface(
                        color = if (isSelected) AppColors.PrimaryLight else AppColors.Surface,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.Primary else AppColors.Border
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .clickable { viewModel.selectWasteType(option.title) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            if (isSelected) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.Primary)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selecionado",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center)
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) AppColors.PrimaryDark else AppColors.TextSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = option.title,
                                    style = AppTypography.Body.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isSelected) AppColors.PrimaryDark else AppColors.TextPrimary
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            if (draft.selectedWasteType == "Outro tipo") {
                Spacer(modifier = Modifier.height(8.dp))
                AppTextField(
                    value = draft.customWasteType,
                    onValueChange = { viewModel.setCustomWasteType(it) },
                    label = "Descreva o tipo de lixo",
                    placeholder = "Ex: Descarte de pneus, óleo, etc.",
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val isContinueEnabled = draft.selectedWasteType != "Outro tipo" ||
                    draft.customWasteType.isNotBlank()

            AppButton(
                text = "Continuar",
                onClick = onNavigateNext,
                enabled = isContinueEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            )
        }
    }
}
