package com.ne.muslimprayers.qiblafinder.ui.compass.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.GoldPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.SuccessGreen
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun StatusBanner(
    isFacingQibla: Boolean,
    relativeAngle: Float,
    qiblaBearing: Float,
    statusBorderColor: Color
) {
    IslamicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isFacingQibla) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        borderColor = statusBorderColor,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingMedium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isFacingQibla) Icons.Default.CheckCircle else Icons.Default.Explore,
                contentDescription = null,
                tint = if (isFacingQibla) SuccessGreen else GoldPrimary,
                modifier = Modifier.size(AppDimensions.iconLarge)
            )
            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
            Column(modifier = Modifier.weight(1f)) {
                AppText.TitleMedium(
                    text = if (isFacingQibla)
                        stringResource(R.string.compass_kaaba_aligned)
                    else
                        stringResource(R.string.compass_screen_title).uppercase(Locale.getDefault()),
                    fontWeight = FontWeight.Bold,
                    color = if (isFacingQibla) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                val turnDegrees = abs(relativeAngle.roundToInt())
                val directionInstruction = if (relativeAngle > 0) {
                    stringResource(R.string.compass_turn_right, turnDegrees)
                } else {
                    stringResource(R.string.compass_turn_left, turnDegrees)
                }
                AppText.BodySmall(
                    text = if (isFacingQibla)
                        stringResource(R.string.compass_aligned_exact)
                    else
                        "$directionInstruction • Qibla: %.1f°".format(Locale.US, qiblaBearing),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
