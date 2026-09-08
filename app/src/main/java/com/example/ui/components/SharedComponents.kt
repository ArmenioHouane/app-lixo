package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Report
import com.example.domain.model.ReportHistory
import com.example.domain.model.ReportStatus
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography
import com.example.util.DateFormatter

// ==========================================
// 1. SCREEN HEADER
// ==========================================
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    leadingBadgeIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppColors.Primary,
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (navigationIcon != null && onNavigationClick != null) {
                        IconButton(
                            onClick = onNavigationClick,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("header_back_button")
                        ) {
                            Icon(
                                imageVector = navigationIcon,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    } else if (leadingBadgeIcon != null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppColors.PrimaryDark)
                        ) {
                            Icon(
                                imageVector = leadingBadgeIcon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column {
                        Text(
                            text = title,
                            style = AppTypography.Title.copy(color = Color.White),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (subtitle != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = AppTypography.Caption.copy(color = Color.White.copy(alpha = 0.85f)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (actions != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        actions()
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. APP CARD (White, 16dp radius, 1dp border, No shadow)
// ==========================================
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    trailingChevron: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        color = AppColors.Surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AppColors.Border),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (title != null || subtitle != null || leadingIcon != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (leadingIcon != null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.PrimaryLight)
                            ) {
                                Icon(
                                    imageVector = leadingIcon,
                                    contentDescription = null,
                                    tint = AppColors.PrimaryDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Column {
                            if (title != null) {
                                Text(
                                    text = title,
                                    style = AppTypography.Subtitle
                                )
                            }
                            if (subtitle != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = subtitle,
                                    style = AppTypography.Caption
                                )
                            }
                        }
                    }

                    if (trailingChevron) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            content()
        }
    }
}

// ==========================================
// 3. APP BUTTON (Primary, Secondary, Ghost, Danger)
// ==========================================
enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    GHOST,
    DANGER
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    testTag: String = "app_button"
) {
    val (bgColor, contentColor, borderStroke) = when (variant) {
        ButtonVariant.PRIMARY -> Triple(AppColors.Primary, Color.White, null)
        ButtonVariant.SECONDARY -> Triple(AppColors.PrimaryLight, AppColors.PrimaryDark, BorderStroke(1.dp, AppColors.Primary))
        ButtonVariant.GHOST -> Triple(Color.Transparent, AppColors.PrimaryDark, BorderStroke(1.dp, AppColors.Border))
        ButtonVariant.DANGER -> Triple(AppColors.Danger, Color.White, null)
    }

    val finalAlpha = if (enabled && !isLoading) 1f else 0.55f

    // Box com contentAlignment garante o texto centrado (horizontal e verticalmente).
    // Modo compacto: quando o chamador fixa uma altura pequena (ex.: 36.dp em cartões),
    // o texto/ícone/padding reduzem automaticamente para caber centrado no botão.
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor.copy(alpha = finalAlpha))
            .then(
                if (borderStroke != null) {
                    Modifier.border(borderStroke, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Altura não limitada (Infinity) => false => botão normal de 52dp
        val isCompact = maxHeight < 52.dp

        val textStyle = if (isCompact) {
            AppTypography.Label.copy(fontWeight = FontWeight.Bold)
        } else {
            AppTypography.Subtitle.copy(fontWeight = FontWeight.Bold)
        }
        val verticalPadding = if (isCompact) 4.dp else 10.dp
        val iconSize = if (isCompact) 16.dp else 20.dp
        val spinnerSize = if (isCompact) 16.dp else 22.dp

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .then(
                    if (isCompact) {
                        Modifier
                    } else {
                        Modifier.defaultMinSize(minHeight = 52.dp)
                    }
                )
                .padding(horizontal = 18.dp, vertical = verticalPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.5.dp,
                    color = contentColor,
                    modifier = Modifier.size(spinnerSize)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "A processar...",
                    style = textStyle.copy(color = contentColor)
                )
            } else {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    style = textStyle.copy(color = contentColor),
                    textAlign = TextAlign.Center,
                    maxLines = if (isCompact) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==========================================
// 4. APP CHIP (36dp, Pill, Surface(onClick))
// ==========================================
@Composable
fun AppChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null,
    selectedColor: Color = AppColors.Primary,
    selectedTextColor: Color = Color.White
) {
    val bgColor = if (selected) selectedColor else AppColors.Surface
    val textColor = if (selected) selectedTextColor else AppColors.TextSecondary
    val border = if (selected) null else BorderStroke(1.dp, AppColors.Border)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = bgColor,
        border = border,
        modifier = modifier
            .height(36.dp)
            .defaultMinSize(minWidth = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Text(
                text = text,
                style = AppTypography.Label.copy(
                    color = textColor,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            )
            if (count != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($count)",
                    style = AppTypography.Caption.copy(
                        color = if (selected) textColor.copy(alpha = 0.85f) else AppColors.TextMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

// ==========================================
// 5. STATUS BADGE
// ==========================================
@Composable
fun StatusBadge(
    status: ReportStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        ReportStatus.REGISTERED -> Pair(AppColors.StatusRegisteredBg, AppColors.StatusRegistered)
        ReportStatus.UNDER_ANALYSIS -> Pair(AppColors.StatusUnderAnalysisBg, AppColors.StatusUnderAnalysis)
        ReportStatus.IN_PROGRESS -> Pair(AppColors.StatusInProgressBg, AppColors.StatusInProgress)
        ReportStatus.RESOLVED -> Pair(AppColors.StatusResolvedBg, AppColors.StatusResolved)
        ReportStatus.CLOSED -> Pair(AppColors.StatusClosedBg, AppColors.StatusClosed)
        ReportStatus.REJECTED -> Pair(AppColors.StatusRejectedBg, AppColors.StatusRejected)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status.label,
                style = AppTypography.Label.copy(color = textColor, fontSize = 11.sp)
            )
        }
    }
}

// ==========================================
// 6. INFO ROW
// ==========================================
@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector? = null,
    showDivider: Boolean = true,
    valueColor: Color = AppColors.TextPrimary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = label,
                style = AppTypography.Caption,
                modifier = Modifier.width(130.dp)
            )
            Text(
                text = value,
                style = AppTypography.Body.copy(
                    fontWeight = FontWeight.Medium,
                    color = valueColor
                ),
                modifier = Modifier.weight(1f)
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppColors.Border.copy(alpha = 0.6f))
            )
        }
    }
}

// ==========================================
// 7. APP TEXT FIELD
// ==========================================
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    charLimit: Int? = null,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = AppTypography.Label.copy(color = AppColors.TextPrimary),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            if (charLimit != null) {
                Text(
                    text = "${value.length}/$charLimit",
                    style = AppTypography.Caption.copy(
                        color = if (value.length > charLimit) AppColors.Danger else AppColors.TextMuted,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = {
                if (charLimit == null || it.length <= charLimit) {
                    onValueChange(it)
                }
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = AppTypography.Body.copy(color = AppColors.TextMuted)
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            readOnly = readOnly,
            isError = errorMessage != null,
            shape = RoundedCornerShape(12.dp),
            textStyle = AppTypography.Body,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppColors.Surface,
                unfocusedContainerColor = AppColors.Surface,
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = AppColors.Border,
                errorBorderColor = AppColors.Danger,
                cursorColor = AppColors.Primary
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = AppTypography.Caption.copy(color = AppColors.Danger, fontSize = 12.sp),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

// ==========================================
// 8. WIZARD PROGRESS (Horizontal 5 steps)
// ==========================================
@Composable
fun WizardProgress(
    currentStep: Int,
    totalSteps: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        for (i in 1..totalSteps) {
            val isCompleted = i < currentStep
            val isCurrent = i == currentStep

            val circleColor = when {
                isCompleted -> AppColors.PrimaryLight
                isCurrent -> Color.White
                else -> Color.White.copy(alpha = 0.35f)
            }
            val checkOrTextColor = when {
                isCompleted -> AppColors.PrimaryDark
                isCurrent -> AppColors.Primary
                else -> Color.Transparent
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(circleColor)
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluído",
                        tint = checkOrTextColor,
                        modifier = Modifier.size(12.dp)
                    )
                } else if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AppColors.Primary)
                    )
                }
            }

            if (i < totalSteps) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(2.dp)
                        .background(
                            if (i < currentStep) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

// ==========================================
// 9. TIMELINE ITEM
// ==========================================
@Composable
fun TimelineItem(
    history: ReportHistory,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    val nodeColor = when (history.newStatus) {
        ReportStatus.REGISTERED -> AppColors.StatusRegistered
        ReportStatus.UNDER_ANALYSIS -> AppColors.StatusUnderAnalysis
        ReportStatus.IN_PROGRESS -> AppColors.StatusInProgress
        ReportStatus.RESOLVED -> AppColors.StatusResolved
        ReportStatus.CLOSED -> AppColors.StatusClosed
        ReportStatus.REJECTED -> AppColors.StatusRejected
    }

    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(nodeColor)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(64.dp)
                        .background(AppColors.Border)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 4.dp else 20.dp)
        ) {
            Text(
                text = DateFormatter.formatDateTime(history.changedAt),
                style = AppTypography.Caption.copy(fontSize = 12.sp, color = AppColors.TextMuted)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = history.newStatus.label,
                style = AppTypography.Subtitle.copy(fontWeight = FontWeight.Bold, color = nodeColor)
            )
            if (!history.observation.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = history.observation,
                    style = AppTypography.Body.copy(color = AppColors.TextSecondary)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Autor: ${history.author} • ${history.source}",
                style = AppTypography.Caption.copy(fontSize = 11.sp, color = AppColors.TextMuted)
            )
        }
    }
}

// ==========================================
// 10. REPORT CARD (List Item)
// ==========================================
@Composable
fun ReportCard(
    report: Report,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = report.code,
                style = AppTypography.Subtitle.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
            )
            StatusBadge(status = report.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${report.neighborhood} • ${report.wasteType}",
            style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = report.reference,
            style = AppTypography.Caption,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${DateFormatter.formatDateTime(report.createdAt)} • ${report.origin.name}",
                style = AppTypography.Caption.copy(color = AppColors.TextMuted, fontSize = 11.sp)
            )

            AppButton(
                text = "Ver detalhes",
                onClick = onClick,
                variant = ButtonVariant.SECONDARY,
                modifier = Modifier.height(36.dp)
            )
        }
    }
}

// ==========================================
// 11. INLINE NOTICE
// ==========================================
@Composable
fun InlineNotice(
    message: String,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
    icon: ImageVector? = null
) {
    val bgColor = if (isDanger) AppColors.DangerLight else AppColors.PrimaryLight
    val contentColor = if (isDanger) AppColors.Danger else AppColors.PrimaryDark
    val finalIcon = icon ?: if (isDanger) Icons.Default.Warning else Icons.Default.Info

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = finalIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                style = AppTypography.Caption.copy(color = contentColor, fontWeight = FontWeight.Medium)
            )
        }
    }
}

// ==========================================
// 12. EMPTY STATE
// ==========================================
@Composable
fun EmptyState(
    title: String,
    message: String,
    icon: ImageVector,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AppColors.SurfaceMuted)
                .border(1.dp, AppColors.Border, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = AppTypography.Title.copy(textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = AppTypography.Body.copy(
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            AppButton(
                text = actionText,
                onClick = onActionClick,
                variant = ButtonVariant.SECONDARY
            )
        }
    }
}

// ==========================================
// 13. CONFIRMATION DIALOG
// ==========================================
@Composable
fun ConfirmationDialog(
    title: String,
    description: String,
    confirmText: String,
    cancelText: String = "Cancelar",
    isDanger: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = AppTypography.Title
            )
        },
        text = {
            Text(
                text = description,
                style = AppTypography.Body.copy(color = AppColors.TextSecondary)
            )
        },
        confirmButton = {
            AppButton(
                text = confirmText,
                onClick = onConfirm,
                variant = if (isDanger) ButtonVariant.DANGER else ButtonVariant.PRIMARY
            )
        },
        dismissButton = {
            AppButton(
                text = cancelText,
                onClick = onDismiss,
                variant = ButtonVariant.GHOST
            )
        },
        containerColor = AppColors.Surface,
        shape = RoundedCornerShape(16.dp)
    )
}
