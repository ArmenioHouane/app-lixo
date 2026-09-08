package com.example.ui.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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

enum class UserProfile(
    val title: String,
    val description: String,
    val roleBadge: String,
    val icon: ImageVector,
    val defaultPhone: String
) {
    CITIZEN(
        title = "Munícipe / Cidadão",
        description = "Criar denúncias de lixo, consultar estado de ocorrências nos bairros de Maputo.",
        roleBadge = "Área do Cidadão",
        icon = Icons.Default.PersonOutline,
        defaultPhone = "+258 84 123 4567"
    ),
    ADMIN(
        title = "Administrador Municipal",
        description = "Centro de Operações Urbanas, supervisão geral, métricas e definições do sistema.",
        roleBadge = "Área Administrativa",
        icon = Icons.Default.AdminPanelSettings,
        defaultPhone = "+258 84 900 1122"
    )
}

@Composable
fun ProfileLoginScreen(
    onLoginSuccessCitizen: () -> Unit,
    onLoginSuccessAdmin: () -> Unit
) {
    var selectedProfile by remember { mutableStateOf(UserProfile.CITIZEN) }
    var phoneNumber by remember { mutableStateOf(UserProfile.CITIZEN.defaultPhone) }
    var otpCode by remember { mutableStateOf("") }
    var isOtpStep by remember { mutableStateOf(false) }
    var otpErrorMessage by remember { mutableStateOf<String?>(null) }
    val mockGeneratedOtp = remember { "482103" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Acesso ao Sistema",
            subtitle = "Conselho Municipal de Maputo",
            leadingBadgeIcon = Icons.Default.DeleteSweep
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (!isOtpStep) {
                // STEP 1: Select profile & phone number
                Text(
                    text = "Selecione o seu Perfil",
                    style = AppTypography.Display
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Escolha o tipo de acesso pretendido para entrar na plataforma municipal.",
                    style = AppTypography.Body.copy(color = AppColors.TextSecondary)
                )

                Spacer(modifier = Modifier.height(20.dp))

                UserProfile.values().forEach { profile ->
                    val isSelected = selectedProfile == profile
                    val borderColor = if (isSelected) AppColors.Primary else AppColors.Border
                    val bgColor = if (isSelected) AppColors.PrimaryLight else AppColors.Surface

                    Surface(
                        onClick = {
                            selectedProfile = profile
                            phoneNumber = profile.defaultPhone
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = bgColor,
                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("profile_card_${profile.name}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) AppColors.Primary else AppColors.SurfaceMuted)
                            ) {
                                Icon(
                                    imageVector = profile.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else AppColors.TextSecondary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = profile.title,
                                        style = AppTypography.Subtitle.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) AppColors.PrimaryDark else AppColors.TextPrimary
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selecionado",
                                            tint = AppColors.Primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = profile.description,
                                    style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AppTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Número de Telefone para Autenticação *",
                    placeholder = "+258 84 123 4567",
                    leadingIcon = Icons.Default.Phone,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppButton(
                    text = "Continuar para Verificação OTP",
                    onClick = {
                        isOtpStep = true
                        otpErrorMessage = null
                    },
                    enabled = phoneNumber.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
                    testTag = "btn_request_otp"
                )
            } else {
                // STEP 2: Mock OTP verification
                Text(
                    text = "Verificação OTP",
                    style = AppTypography.Display
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Confirme o código de segurança enviado para o perfil ${selectedProfile.title}.",
                    style = AppTypography.Body.copy(color = AppColors.TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated SMS banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.PrimaryLight,
                    border = BorderStroke(1.dp, AppColors.Primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = null,
                            tint = AppColors.PrimaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SMS Municipal de Segurança",
                                style = AppTypography.Caption.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.PrimaryDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Código de Confirmação: $mockGeneratedOtp (válido por 10 min)",
                                style = AppTypography.Body.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.TextPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AppTextField(
                    value = otpCode,
                    onValueChange = {
                        otpCode = it.filter { ch -> ch.isDigit() }.take(6)
                        otpErrorMessage = null
                    },
                    label = "Código de 6 dígitos *",
                    placeholder = "482103",
                    leadingIcon = Icons.Default.Lock,
                    errorMessage = otpErrorMessage,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    text = "Preencher Código ($mockGeneratedOtp)",
                    onClick = {
                        otpCode = mockGeneratedOtp
                        otpErrorMessage = null
                    },
                    variant = ButtonVariant.GHOST,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppButton(
                    text = "Validar e Entrar",
                    onClick = {
                        if (otpCode.length >= 4) {
                            if (selectedProfile == UserProfile.CITIZEN) {
                                onLoginSuccessCitizen()
                            } else {
                                onLoginSuccessAdmin()
                            }
                        } else {
                            otpErrorMessage = "Por favor, insira o código de verificação recebido."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_verify_otp")
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppButton(
                    text = "Alterar Perfil ou Telefone",
                    onClick = {
                        isOtpStep = false
                        otpCode = ""
                        otpErrorMessage = null
                    },
                    variant = ButtonVariant.SECONDARY,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}
