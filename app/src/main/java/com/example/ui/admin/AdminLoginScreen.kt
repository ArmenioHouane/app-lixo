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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.AppTextField
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("admin@cmmaputo.gov.mz") }
    var password by remember { mutableStateOf("maputo2026") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Acesso Administrativo",
            subtitle = "Centro de Operações Urbanas",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = AppColors.Surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AppColors.Border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .background(AppColors.PrimaryLight, RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AppColors.PrimaryDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Autenticação Municipal",
                        style = AppTypography.Title.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Insira as suas credenciais operacionais para aceder ao centro de controlo.",
                        style = AppTypography.Caption.copy(
                            color = AppColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = "Email institucional",
                        placeholder = "operador@cmmaputo.gov.mz",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    AppTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = "Palavra-passe",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        errorMessage = errorMessage
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppButton(
                        text = "Entrar no Painel",
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                onLoginSuccess()
                            } else {
                                errorMessage = "Por favor, preencha todos os campos."
                            }
                        },
                        leadingIcon = Icons.Default.AdminPanelSettings,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "admin_login_submit_button"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppButton(
                            text = "Admin",
                            onClick = {
                                email = "admin@cmmaputo.gov.mz"
                                password = "maputo_admin"
                            },
                            variant = ButtonVariant.GHOST,
                            modifier = Modifier.weight(1f)
                        )
                        AppButton(
                            text = "Operador",
                            onClick = {
                                email = "operacoes@cmmaputo.gov.mz"
                                password = "operador_maputo"
                            },
                            variant = ButtonVariant.GHOST,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Conselho Municipal de Maputo • Gestão Urbana",
                style = AppTypography.Caption.copy(color = AppColors.TextMuted, fontSize = 11.sp)
            )
        }
    }
}
