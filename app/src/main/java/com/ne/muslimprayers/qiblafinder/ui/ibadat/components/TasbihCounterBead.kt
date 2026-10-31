package com.ne.muslimprayers.qiblafinder.ui.ibadat.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.theme.ssp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun TasbihCounterBead(
    count: Int,
    target: Int,
    arabicPhrase: String,
    scale: Float,
    onTap: () -> Unit,
    size: Int
) {
    val sweepAngle = if (target > 0) (count.toFloat() / target.toFloat()) * 360f else 0f
    val strokeWidthPx = with(LocalDensity.current) { 5.sdp.toPx() }

    // 3D Pearl Sphere Gradients
    val pearlGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF7F0E6),
            Color(0xFFEADBCE),
            Color(0xFFD6C3B2)
        )
    )

    Box(
        modifier = Modifier
            .size(size.sdp)
            .scale(scale)
            .shadow(18.sdp, CircleShape, ambientColor = Color(0x33C88A32), spotColor = Color(0x40C88A32))
            .clip(CircleShape)
            .background(pearlGradient)
            .border(4.sdp, DesertOchre, CircleShape)
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = DesertOchre,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.sdp)
        ) {
            AppText.ArabicText(
                text = arabicPhrase.ifBlank { "سُبْحَانَ اللَّهِ" },
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.sdp))

            Text(
                text = "$count",
                fontSize = 46.ssp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            AppText.TitleMedium(
                text = "/ $target",
                color = DesertOchre,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.sdp))

            AppText.Caption(
                text = "TAP TO COUNT",
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
