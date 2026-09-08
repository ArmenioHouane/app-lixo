package com.example.ui.citizen.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ScreenHeader
import com.example.ui.components.WizardProgress
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography
import kotlinx.coroutines.delay

@Composable
fun Step4OtpScreen(
    viewModel: WizardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    var countdownSeconds by remember { mutableIntStateOf(24) }

    LaunchedEffect(countdownSeconds) {
        if (countdownSeconds > 0) {
            delay(1000L)
            countdownSeconds -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Nova denúncia",
            subtitle = "Passo 4 de 5",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        WizardProgress(currentStep = 4, totalSteps = 5)

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AppCard(
                title = "Verificação de número",
                subtitle = "Código enviado para ${draft.phoneNumber}"
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // OTP centered input field
                OutlinedTextField(
                    value = draft.otpCode,
                    onValueChange = { viewModel.setOtpCode(it) },
                    placeholder = {
                        Text(
                            text = "1 2 3 4 5 6",
                            style = AppTypography.Otp.copy(
                                color = AppColors.TextMuted,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = AppTypography.Otp.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.PrimaryDark
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AppColors.PrimaryLight.copy(alpha = 0.5f),
                        unfocusedContainerColor = AppColors.Surface,
                        focusedBorderColor = AppColors.Primary,
                        unfocusedBorderColor = AppColors.Border,
                        cursorColor = AppColors.Primary
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp)
                        .testTag("otp_input_field")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Insira o código de 6 dígitos recebido por SMS.",
                        style = AppTypography.Caption.copy(
                            color = AppColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                text = "Validar código",
                onClick = {
                    if (viewModel.verifyOtp()) {
                        onNavigateNext()
                    }
                },
                enabled = draft.otpCode.length >= 4,
                modifier = Modifier.fillMaxWidth(),
                testTag = "validate_otp_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            val resendText = if (countdownSeconds > 0) {
                "Reenviar código (${countdownSeconds}s)"
            } else {
                "Reenviar código"
            }

            AppButton(
                text = resendText,
                onClick = {
                    countdownSeconds = 30
                },
                variant = ButtonVariant.GHOST,
                enabled = countdownSeconds == 0,
                leadingIcon = Icons.Default.Refresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                testTag = "resend_otp_button"
            )
        }
    }
}
