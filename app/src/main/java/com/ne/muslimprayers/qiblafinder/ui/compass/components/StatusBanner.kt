package com.ne.muslimprayers.qiblafinder.ui.compass.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.GoldPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.SuccessGreen
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun StatusBanner(
    isFacingQibla: Boolean,
    relativeAngle: Float,
    qiblaBearing: Float,
    statusBorderColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.sdp))
            .border(1.sdp, statusBorderColor, RoundedCornerShape(16.sdp)),
        shape = RoundedCornerShape(16.sdp),
        color = if (isFacingQibla) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.sdp, vertical = 12.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isFacingQibla) Icons.Default.CheckCircle else Icons.Default.Explore,
                contentDescription = null,
                tint = if (isFacingQibla) SuccessGreen else GoldPrimary,
                modifier = Modifier.size(26.sdp)
            )
            Spacer(modifier = Modifier.width(12.sdp))
            Column(modifier = Modifier.weight(1f)) {
                AppText.TitleMedium(
                    text = if (isFacingQibla) "YOU ARE FACING QIBLA!" else "ALIGN COMPASS TO QIBLA",
                    fontWeight = FontWeight.Bold,
                    color = if (isFacingQibla) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                AppText.BodySmall(
                    text = if (isFacingQibla)
                        "Perfectly aligned with Holy Kaaba in Makkah"
                    else
                        "Turn device %d° %s to face Qibla (Bearing: %.1f°)".format(
                            Locale.US,
                            kotlin.math.abs(relativeAngle.roundToInt()),
                            if (relativeAngle > 0) "Right" else "Left",
                            qiblaBearing
                        ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
