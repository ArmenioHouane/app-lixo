package com.example.ui.citizen.wizard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.AppTextField
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ScreenHeader
import com.example.ui.components.WizardProgress
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography
import com.example.util.MediaStoreHelper

@Composable
fun Step3ReferenceScreen(
    viewModel: WizardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val draft by viewModel.draft.collectAsState()
    val context = LocalContext.current

    // ---------- ANEXAR (Galeria: Photo Picker, imagem ou vídeo) ----------
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        // Dinâmico: só actualiza o estado quando o utilizador realmente escolhe um ficheiro.
        if (uri != null) {
            val isVideo = context.contentResolver.getType(uri)?.startsWith("video/") == true
            viewModel.setMedia(uri.toString(), if (isVideo) "VIDEO" else "IMAGE")
        }
    }

    // ---------- CAPTURAR (Câmara real: foto ou vídeo) ----------
    var pendingCaptureFile by remember { mutableStateOf<java.io.File?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        // Dinâmico: o estado só muda se a captura for concluída com sucesso.
        if (success && pendingCaptureFile != null) {
            viewModel.setMedia(
                Uri.fromFile(pendingCaptureFile).toString(),
                "IMAGE"
            )
        }
        pendingCaptureFile = null
    }

    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success: Boolean ->
        if (success && pendingCaptureFile != null) {
            viewModel.setMedia(
                Uri.fromFile(pendingCaptureFile).toString(),
                "VIDEO"
            )
        }
        pendingCaptureFile = null
    }

    fun launchCapture(isVideo: Boolean) {
        val (file, uri) = MediaStoreHelper.createMediaFile(context, isVideo)
        pendingCaptureFile = file
        if (isVideo) {
            captureVideoLauncher.launch(uri)
        } else {
            takePictureLauncher.launch(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = "Nova denúncia",
            subtitle = "Passo 3 de 5: Localização e Evidências",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        WizardProgress(currentStep = 3, totalSteps = 5)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ponto de referência",
                style = AppTypography.Display
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Descreva a localização e, opcionalmente, anexe uma fotografia ou vídeo do local.",
                style = AppTypography.Body.copy(color = AppColors.TextSecondary)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Placeholder explicitly empty as requested by user
            AppTextField(
                value = draft.reference,
                onValueChange = { viewModel.setReference(it) },
                label = "Ponto de referência do local *",
                placeholder = "",
                singleLine = false,
                maxLines = 4,
                charLimit = 120
            )

            Spacer(modifier = Modifier.height(16.dp))

            // MEDIA ATTACHMENT SECTION (Anexar da galeria / Capturar com a câmara)
            AppCard(
                title = "Evidência Multimédia (Opcional)",
                subtitle = "Anexe da galeria ou capture no local"
            ) {
                if (draft.mediaUri == null) {
                    Text(
                        text = "Escolha como pretende partilhar a evidência visual da denúncia.",
                        style = AppTypography.Caption.copy(color = AppColors.TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. ANEXAR — abre a galeria (Photo Picker: imagem ou vídeo)
                        Surface(
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.Surface,
                            border = BorderStroke(1.dp, AppColors.Border),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Anexar ficheiro",
                                    tint = AppColors.TextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Anexar",
                                    style = AppTypography.Caption.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.TextPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "Galeria",
                                    style = AppTypography.Caption.copy(
                                        color = AppColors.TextMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // 2. CAPTURAR — abre a câmara em tempo real
                        Surface(
                            onClick = { launchCapture(isVideo = false) },
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.PrimaryLight,
                            border = BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Capturar foto",
                                    tint = AppColors.PrimaryDark,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Capturar",
                                    style = AppTypography.Caption.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.PrimaryDark,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = "Câmara",
                                    style = AppTypography.Caption.copy(
                                        color = AppColors.PrimaryDark.copy(alpha = 0.7f),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Alternativa rápida: gravar vídeo com a câmara
                    Surface(
                        onClick = { launchCapture(isVideo = true) },
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.PrimaryLight,
                        border = BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Gravar vídeo",
                                tint = AppColors.PrimaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gravar vídeo com a câmara",
                                style = AppTypography.Caption.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.PrimaryDark,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                } else {
                    // Media Attached Preview Card (estado dinâmico: ficheiro recebido)
                    Surface(
                        color = AppColors.PrimaryLight,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AppColors.Primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.Primary)
                            ) {
                                Icon(
                                    imageVector = if (draft.mediaType == "VIDEO") Icons.Default.PlayCircleOutline else Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AppColors.PrimaryDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (draft.mediaType == "VIDEO") "Vídeo Anexado" else "Fotografia Anexada",
                                        style = AppTypography.Caption.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.PrimaryDark
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = draft.mediaUri?.substringAfterLast("/") ?: "",
                                    style = AppTypography.Caption.copy(
                                        fontSize = 11.sp,
                                        color = AppColors.TextSecondary
                                    ),
                                    maxLines = 1
                                )
                            }

                            IconButton(
                                onClick = { viewModel.clearMedia() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remover anexo",
                                    tint = AppColors.Danger
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = draft.phoneNumber,
                onValueChange = { viewModel.setPhoneNumber(it) },
                label = "Número de telefone do munícipe *",
                placeholder = "+258 84 123 4567",
                leadingIcon = Icons.Default.Phone,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "Avançar para Verificação",
                onClick = onNavigateNext,
                enabled = draft.reference.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            )
        }
    }
}
