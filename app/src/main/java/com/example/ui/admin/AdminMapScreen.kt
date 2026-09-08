package com.example.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ReportRepository
import com.example.domain.model.Report
import com.example.domain.model.ReportStatus
import com.example.ui.components.AppButton
import com.example.ui.components.AppCard
import com.example.ui.components.AppChip
import com.example.ui.components.ButtonVariant
import com.example.ui.components.ScreenHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AppColors
import com.example.ui.theme.AppTypography

@Composable
fun AdminMapScreen(
    repository: ReportRepository,
    onNavigateDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    isAdminMode: Boolean = true
) {
    val reports by repository.getAllReports().collectAsState(initial = emptyList())
    var selectedReport by remember { mutableStateOf<Report?>(null) }
    var statusFilter by remember { mutableStateOf<ReportStatus?>(null) }

    // Interactive Pan & Zoom state for realistic map
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    val filteredReports = remember(reports, statusFilter) {
        if (statusFilter == null) reports
        else reports.filter { it.status == statusFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        ScreenHeader(
            title = if (isAdminMode) "Georreferenciação Urbana" else "Mapa de Ocorrências",
            subtitle = if (isAdminMode) "Mapa cartográfico de Maputo" else "Pontos de ocorrência na cidade de Maputo",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = onNavigateBack
        )

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppChip(
                text = "Todos os pontos",
                selected = statusFilter == null,
                onClick = { statusFilter = null },
                count = reports.size
            )
            for (status in ReportStatus.values()) {
                val count = reports.count { it.status == status }
                AppChip(
                    text = status.label,
                    selected = statusFilter == status,
                    onClick = { statusFilter = status },
                    count = count
                )
            }
        }

        // Map Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE9F1EC))
                .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
        ) {
            // Maputo City Cartographic Coordinates Bounds:
            val minLat = -25.99
            val maxLat = -25.88
            val minLng = 32.53
            val maxLng = 32.63

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            zoomScale = (zoomScale * zoom).coerceIn(0.8f, 3.5f)
                            panOffsetX += pan.x
                            panOffsetY += pan.y
                        }
                    }
                    .pointerInput(filteredReports, zoomScale, panOffsetX, panOffsetY) {
                        detectTapGestures { tapOffset ->
                            val width = size.width
                            val height = size.height
                            var closest: Report? = null
                            var minDistance = 70f

                            for (r in filteredReports) {
                                val lat = r.latitude ?: -25.95
                                val lng = r.longitude ?: 32.57

                                val normX = ((lng - minLng) / (maxLng - minLng)).toFloat().coerceIn(0.05f, 0.95f)
                                val normY = (1f - ((lat - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0.05f, 0.95f)

                                // Transform according to zoom & pan
                                val centerX = width / 2f
                                val centerY = height / 2f
                                val basePointX = normX * width
                                val basePointY = normY * height

                                val pointX = centerX + (basePointX - centerX) * zoomScale + panOffsetX
                                val pointY = centerY + (basePointY - centerY) * zoomScale + panOffsetY

                                val dx = tapOffset.x - pointX
                                val dy = tapOffset.y - pointY
                                val dist = kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                                if (dist < minDistance) {
                                    minDistance = dist
                                    closest = r
                                }
                            }

                            if (closest != null) {
                                selectedReport = closest
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val centerX = w / 2f
                val centerY = h / 2f

                fun transformX(normX: Float): Float = centerX + (normX * w - centerX) * zoomScale + panOffsetX
                fun transformY(normY: Float): Float = centerY + (normY * h - centerY) * zoomScale + panOffsetY

                // 1. Water Body: Baía de Maputo
                val waterColor = Color(0xFFCCE4F0)
                val bayPath = Path().apply {
                    moveTo(transformX(0.70f), transformY(0f))
                    cubicTo(
                        transformX(0.55f), transformY(0.35f),
                        transformX(0.40f), transformY(0.70f),
                        transformX(0.75f), transformY(1.0f)
                    )
                    lineTo(transformX(1.3f), transformY(1.0f))
                    lineTo(transformX(1.3f), transformY(0f))
                    close()
                }
                drawPath(path = bayPath, color = waterColor)

                // 2. Katembe Shoreline (South side)
                val katembePath = Path().apply {
                    moveTo(transformX(0.20f), transformY(0.95f))
                    quadraticBezierTo(
                        transformX(0.45f), transformY(0.85f),
                        transformX(0.75f), transformY(1.0f)
                    )
                    lineTo(transformX(0.75f), transformY(1.3f))
                    lineTo(transformX(0.20f), transformY(1.3f))
                    close()
                }
                drawPath(path = katembePath, color = Color(0xFFDFE9E2))

                // 3. Ponte Maputo-Katembe (Bridge)
                val bridgeStart = Offset(transformX(0.48f), transformY(0.68f))
                val bridgeEnd = Offset(transformX(0.46f), transformY(0.86f))
                drawLine(
                    color = Color(0xFF6B7280),
                    start = bridgeStart,
                    end = bridgeEnd,
                    strokeWidth = 4f * zoomScale
                )
                drawLine(
                    color = Color(0xFFDC2626),
                    start = bridgeStart,
                    end = bridgeEnd,
                    strokeWidth = 2f * zoomScale
                )

                // 4. Primary Avenues & Arteries (Rede Viária de Maputo)
                val roadPrimaryColor = Color(0xFFFFFFFF)
                val roadSecondaryColor = Color(0xFFD3E0D6)

                // Av. Julius Nyerere (North to South along the coast)
                val nyererePath = Path().apply {
                    moveTo(transformX(0.62f), transformY(0.10f))
                    cubicTo(
                        transformX(0.58f), transformY(0.30f),
                        transformX(0.52f), transformY(0.50f),
                        transformX(0.50f), transformY(0.68f)
                    )
                }
                drawPath(nyererePath, color = roadPrimaryColor, style = Stroke(width = 6f * zoomScale))

                // Av. Eduardo Mondlane (East to West)
                val mondlanePath = Path().apply {
                    moveTo(transformX(0.15f), transformY(0.58f))
                    lineTo(transformX(0.58f), transformY(0.58f))
                }
                drawPath(mondlanePath, color = roadPrimaryColor, style = Stroke(width = 6f * zoomScale))

                // Av. 24 de Julho (Parallel East-West)
                val vinteQuatroPath = Path().apply {
                    moveTo(transformX(0.15f), transformY(0.64f))
                    lineTo(transformX(0.54f), transformY(0.64f))
                }
                drawPath(vinteQuatroPath, color = roadPrimaryColor, style = Stroke(width = 5f * zoomScale))

                // Estrada Circular de Maputo (Outer ring)
                val circularPath = Path().apply {
                    moveTo(transformX(0.05f), transformY(0.25f))
                    quadraticBezierTo(
                        transformX(0.20f), transformY(0.12f),
                        transformX(0.45f), transformY(0.08f)
                    )
                }
                drawPath(circularPath, color = roadSecondaryColor, style = Stroke(width = 4f * zoomScale))

                // Secondary connectors
                val connector1 = Path().apply {
                    moveTo(transformX(0.35f), transformY(0.15f))
                    lineTo(transformX(0.40f), transformY(0.70f))
                }
                drawPath(connector1, color = roadSecondaryColor, style = Stroke(width = 3.5f * zoomScale))

                // 5. Render Incident Pins
                for (r in filteredReports) {
                    val lat = r.latitude ?: -25.95
                    val lng = r.longitude ?: 32.57

                    val normX = ((lng - minLng) / (maxLng - minLng)).toFloat().coerceIn(0.05f, 0.95f)
                    val normY = (1f - ((lat - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0.05f, 0.95f)

                    val cx = transformX(normX)
                    val cy = transformY(normY)

                    val isSelected = selectedReport?.code == r.code

                    val pinColor = when (r.status) {
                        ReportStatus.REGISTERED -> Color(0xFF1E88E5)
                        ReportStatus.UNDER_ANALYSIS -> Color(0xFFF59E0B)
                        ReportStatus.IN_PROGRESS -> Color(0xFFEA580C)
                        ReportStatus.RESOLVED -> Color(0xFF1B7A43)
                        ReportStatus.CLOSED -> Color(0xFF6B7280)
                        ReportStatus.REJECTED -> Color(0xFFDC2626)
                    }

                    if (isSelected) {
                        // Pulse ring
                        drawCircle(
                            color = pinColor.copy(alpha = 0.35f),
                            radius = 28f * zoomScale,
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = pinColor.copy(alpha = 0.6f),
                            radius = 18f * zoomScale,
                            center = Offset(cx, cy)
                        )
                    }

                    // Outer white circle
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 13f * zoomScale else 9f * zoomScale,
                        center = Offset(cx, cy)
                    )

                    // Pin center dot
                    drawCircle(
                        color = pinColor,
                        radius = if (isSelected) 10f * zoomScale else 6.5f * zoomScale,
                        center = Offset(cx, cy)
                    )
                }
            }

            // Map Overlay Badge
            Surface(
                color = AppColors.Surface.copy(alpha = 0.95f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AppColors.Border),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Maputo • ${filteredReports.size} ocorrências",
                        style = AppTypography.Caption.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                    )
                }
            }

            // Map Controls (Zoom In, Zoom Out, Reset Center)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { zoomScale = (zoomScale * 1.3f).coerceAtMost(3.5f) },
                    shape = CircleShape,
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Border),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = AppColors.TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Surface(
                    onClick = { zoomScale = (zoomScale / 1.3f).coerceAtLeast(0.8f) },
                    shape = CircleShape,
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Border),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            tint = AppColors.TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Surface(
                    onClick = {
                        zoomScale = 1.0f
                        panOffsetX = 0f
                        panOffsetY = 0f
                    },
                    shape = CircleShape,
                    color = AppColors.Surface,
                    border = BorderStroke(1.dp, AppColors.Border),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Recentrar",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Bottom Details Card for selected point
        if (selectedReport != null) {
            val r = selectedReport!!
            AppCard(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = r.code,
                            style = AppTypography.Title.copy(
                                fontWeight = FontWeight.Bold,
                                color = AppColors.Primary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${r.neighborhood} • ${r.wasteType}",
                            style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    StatusBadge(status = r.status)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = r.reference,
                    style = AppTypography.Body.copy(color = AppColors.TextSecondary),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppButton(
                        text = if (isAdminMode) "Abrir Despacho" else "Ver Detalhes",
                        onClick = { onNavigateDetail(r.code) },
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Fechar",
                        onClick = { selectedReport = null },
                        variant = ButtonVariant.GHOST,
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }
        } else {
            Surface(
                color = AppColors.SurfaceMuted,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AppColors.Border),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAdminMode) {
                            "Navegue pelo mapa ou toque num ponto colorido para consultar e despachar a ocorrência."
                        } else {
                            "Navegue pelo mapa ou toque num ponto colorido para consultar os detalhes da denúncia."
                        },
                        style = AppTypography.Caption.copy(
                            color = AppColors.TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
