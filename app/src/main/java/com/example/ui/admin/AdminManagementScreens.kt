package com.example.ui.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DatabaseSeeder
import com.example.data.repository.ReportRepository
import com.example.domain.model.SystemSettings
import com.example.domain.model.User
import com.example.domain.model.UserRole
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.AppTextField
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.InfoRow
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography
import com.example.util.DateFormatter
import kotlinx.coroutines.launch

// ==========================================
// 1. ADMIN USERS SCREEN
// ==========================================
@Composable
fun AdminUsersScreen(
    repository: ReportRepository,
    onNavigateBack: () -> Unit
) {
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    var showAddUserDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Equipa & Fiscais",
            subtitle = "Gestão de acessos municipais",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack,
            actions = {
                AppButton(
                    text = "Adicionar",
                    onClick = { showAddUserDialog = true },
                    variant = ButtonVariant.SECONDARY,
                    leadingIcon = Icons.Default.GroupAdd,
                    modifier = Modifier.height(36.dp)
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users, key = { it.id }) { user ->
                AppCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (user.isOnline) AppColors.Success else AppColors.TextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = user.name,
                                style = AppTypography.Subtitle.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        UserRoleBadge(role = user.role)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user.email,
                            style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = user.phone,
                            style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Permissions row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PermissionChip(label = "Denúncias", granted = user.canManageReports)
                        PermissionChip(label = "Utilizadores", granted = user.canManageUsers)
                        PermissionChip(label = "Sistema", granted = user.canConfigureSystem)
                    }
                }
            }
        }
    }

    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onConfirm = { newUser ->
                showAddUserDialog = false
                scope.launch {
                    repository.addUser(newUser)
                }
            }
        )
    }
}

@Composable
fun UserRoleBadge(role: UserRole) {
    val (bgColor, textColor) = when (role) {
        UserRole.ADMIN -> Pair(AppColors.PrimaryLight, AppColors.PrimaryDark)
        UserRole.OPERATOR -> Pair(AppColors.StatusUnderAnalysisBg, AppColors.StatusUnderAnalysis)
        UserRole.SUPERVISOR -> Pair(AppColors.StatusInProgressBg, AppColors.StatusInProgress)
        UserRole.INSPECTOR -> Pair(AppColors.StatusRegisteredBg, AppColors.StatusRegistered)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = role.label,
            style = AppTypography.Label.copy(color = textColor, fontSize = 11.sp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun PermissionChip(label: String, granted: Boolean) {
    Surface(
        color = if (granted) AppColors.PrimaryLight else AppColors.SurfaceMuted,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (granted) AppColors.Primary.copy(alpha = 0.3f) else AppColors.Border)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = if (granted) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (granted) AppColors.PrimaryDark else AppColors.TextMuted,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = AppTypography.Caption.copy(
                    fontSize = 10.sp,
                    color = if (granted) AppColors.PrimaryDark else AppColors.TextMuted
                )
            )
        }
    }
}

@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (User) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+258 84 ") }
    var selectedRole by remember { mutableStateOf(UserRole.OPERATOR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Adicionar Operador", style = AppTypography.Title)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nome completo",
                    placeholder = "Ex: Armando Cossa",
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email institucional",
                    placeholder = "nome@cmmaputo.gov.mz",
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                AppTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Telefone",
                    placeholder = "+258 84 000 0000",
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Função no sistema:",
                    style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = selectedRole == role
                        Surface(
                            onClick = { selectedRole = role },
                            color = if (isSelected) AppColors.PrimaryLight else AppColors.Surface,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) AppColors.Primary else AppColors.Border),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = role.label,
                                style = AppTypography.Caption.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AppColors.PrimaryDark else AppColors.TextPrimary
                                ),
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            AppButton(
                text = "Gravar",
                onClick = {
                    if (name.isNotBlank()) {
                        val newUser = User(
                            id = 0,
                            name = name,
                            role = selectedRole,
                            phone = phone,
                            email = email.ifBlank { "utilizador@cmmaputo.gov.mz" },
                            lastAccess = System.currentTimeMillis(),
                            isOnline = true,
                            canManageReports = true,
                            canManageUsers = selectedRole == UserRole.ADMIN,
                            canViewReports = true,
                            canConfigureSystem = selectedRole == UserRole.ADMIN
                        )
                        onConfirm(newUser)
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

// ==========================================
// 2. ADMIN SETTINGS SCREEN
// ==========================================
@Composable
fun AdminSettingsScreen(
    repository: ReportRepository,
    onNavigateBack: () -> Unit
) {
    val currentSettings by repository.getSettings().collectAsState(initial = SystemSettings())
    val scope = rememberCoroutineScope()

    var serviceCode by remember { mutableStateOf(currentSettings.serviceCode) }
    var phpUrl by remember { mutableStateOf(currentSettings.ussdServerUrl) }
    var timeoutSeconds by remember { mutableStateOf(currentSettings.requestTimeoutSeconds.toString()) }
    var savedSuccessMessage by remember { mutableStateOf<String?>(null) }
    var showSeedDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Configurações do Sistema",
            subtitle = "Parâmetros municipais e telemetria",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            AppCard(title = "Canal USSD & Telecom") {
                AppTextField(
                    value = serviceCode,
                    onValueChange = { serviceCode = it },
                    label = "Código de Serviço USSD",
                    placeholder = "*384*73407#",
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                AppTextField(
                    value = phpUrl,
                    onValueChange = { phpUrl = it },
                    label = "URL do Servidor USSD PHP",
                    placeholder = "http://10.0.2.2:8000/ussd.php",
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                AppTextField(
                    value = timeoutSeconds,
                    onValueChange = { timeoutSeconds = it },
                    label = "Timeout de Requisição (segundos)",
                    placeholder = "8",
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppButton(
                    text = "Guardar Configurações",
                    onClick = {
                        scope.launch {
                            val updated = currentSettings.copy(
                                serviceCode = serviceCode,
                                ussdServerUrl = phpUrl,
                                requestTimeoutSeconds = timeoutSeconds.toIntOrNull() ?: 8
                            )
                            repository.saveSettings(updated)
                            savedSuccessMessage = "Configurações guardadas com sucesso!"
                        }
                    },
                    leadingIcon = Icons.Default.Save,
                    modifier = Modifier.fillMaxWidth()
                )

                if (savedSuccessMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = savedSuccessMessage!!,
                        style = AppTypography.Caption.copy(color = AppColors.Success, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppCard(title = "Ambiente & Diagnóstico") {
                InfoRow(label = "Ambiente", value = "Conselho Municipal de Maputo")
                InfoRow(label = "Base de Dados", value = "SQLite / Room com KSP")
                InfoRow(label = "Versão da App", value = currentSettings.appVersion)
                InfoRow(
                    label = "Estado do Sistema",
                    value = "Operacional • Conectado",
                    valueColor = AppColors.Success,
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Database seeder
            AppCard(title = "Dados Operacionais") {
                Text(
                    text = "Repor a base de dados com denúncias em todos os distritos municipais de Maputo e histórico auditado.",
                    style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                )
                Spacer(modifier = Modifier.height(12.dp))
                AppButton(
                    text = "Recarregar Ocorrências",
                    onClick = { showSeedDialog = true },
                    variant = ButtonVariant.SECONDARY,
                    leadingIcon = Icons.Default.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showSeedDialog) {
        ConfirmationDialog(
            title = "Recarregar dados municipais?",
            description = "Esta ação irá recarregar as ocorrências nos bairros de Maputo para validação dos fluxos do Cidadão e Administrador.",
            confirmText = "Recarregar",
            onConfirm = {
                showSeedDialog = false
                // Re-seed DB
            },
            onDismiss = { showSeedDialog = false }
        )
    }
}
