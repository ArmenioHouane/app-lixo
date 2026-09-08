package com.example.ui.citizen.wizard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppButton
import com.example.ui.components.ButtonVariant
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

@Composable
fun SuccessScreen(
    reportCode: String,
    onViewReport: (String) -> Unit,
    onBackToMenu: () -> Unit
) {
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Big green checkmark
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AppColors.PrimaryLight)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Sucesso",
                tint = AppColors.Primary,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Denúncia registada\ncom sucesso!",
            style = AppTypography.Display.copy(textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "O seu código de denúncia é:",
            style = AppTypography.Body.copy(color = AppColors.TextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Large Code Pill
        Surface(
            color = AppColors.PrimaryLight,
            shape = RoundedCornerShape(999.dp),
            border = BorderStroke(1.5.dp, AppColors.Primary),
            modifier = Modifier
                .clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Código da Denúncia", reportCode)
                    clipboard.setPrimaryClip(clip)
                    isCopied = true
                }
                .testTag("success_code_pill")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = reportCode,
                    style = AppTypography.Title.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.PrimaryDark,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copiar código",
                    tint = AppColors.PrimaryDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (isCopied) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Código copiado para a área de transferência!",
                style = AppTypography.Caption.copy(color = AppColors.Primary, fontSize = 12.sp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Guarde este código para acompanhar o estado da denúncia.",
            style = AppTypography.Caption.copy(
                color = AppColors.TextMuted,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.weight(1.2f))

        AppButton(
            text = "Ver minha denúncia",
            onClick = { onViewReport(reportCode) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "view_my_report_button"
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppButton(
            text = "Voltar ao menu",
            onClick = onBackToMenu,
            variant = ButtonVariant.GHOST,
            modifier = Modifier.fillMaxWidth(),
            testTag = "back_to_menu_button"
        )
    }
}
