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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AppButton
import com.example.ui.components.AppTextField
import com.example.ui.components.ScreenHeader
import com.example.ui.components.WizardProgress
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

private val MaputoNeighborhoods = listOf(
    "Alto-Maé",
    "Benfica",
    "Central \"B\"",
    "Chamanculo",
    "Coop",
    "Laulane",
    "Malhangalene",
    "Maxaquene",
    "Mavalane",
    "Polana Cimento",
    "Sommerchield",
    "Zimpeto",
    "Catembe",
    "Outro bairro"
)

@Composable
fun Step1NeighborhoodScreen(
    viewModel: WizardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) MaputoNeighborhoods
        else MaputoNeighborhoods.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Screen Header with Wizard Progress
        ScreenHeader(
            title = "Nova denúncia",
            subtitle = "Passo 1 de 5",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        WizardProgress(currentStep = 1, totalSteps = 5)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Onde está o problema?",
                style = AppTypography.Display
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Selecione o bairro onde o lixo se encontra.",
                style = AppTypography.Body.copy(color = AppColors.TextSecondary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Pesquisar bairro",
                placeholder = "Ex: Chamanculo, Alto-Maé...",
                leadingIcon = Icons.Default.Search,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { neighborhood ->
                    val isSelected = draft.selectedNeighborhood == neighborhood

                    Surface(
                        color = if (isSelected) AppColors.PrimaryLight else AppColors.Surface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AppColors.Primary else AppColors.Border
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectNeighborhood(neighborhood) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) AppColors.Primary else Color.Transparent
                                        )
                                        .then(
                                            if (!isSelected) {
                                                Modifier.background(Color.Transparent, CircleShape)
                                                    .then(Modifier.size(20.dp))
                                            } else Modifier
                                        )
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selecionado",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(AppColors.Surface)
                                                .then(
                                                    Modifier.background(Color.Transparent)
                                                )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = neighborhood,
                                    style = AppTypography.Body.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) AppColors.PrimaryDark else AppColors.TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                if (draft.selectedNeighborhood == "Outro bairro") {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        AppTextField(
                            value = draft.customNeighborhood,
                            onValueChange = { viewModel.setCustomNeighborhood(it) },
                            label = "Nome do bairro",
                            placeholder = "Digite o nome do seu bairro",
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val isContinueEnabled = draft.selectedNeighborhood != "Outro bairro" ||
                    draft.customNeighborhood.isNotBlank()

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
