package com.ne.muslimprayers.qiblafinder.ui.compass.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ne.muslimprayers.qiblafinder.core.theme.SuccessGreen
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.QiblaState
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ReadingsCard(
    state: QiblaState,
    isFacingQibla: Boolean
) {
    IslamicCard(
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppText.BodySmall(
                    text = "Qibla Angle",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppText.HeadingLarge(
                    text = "%.1f°".format(Locale.US, state.qiblaBearing),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .width(1.sdp)
                    .height(30.sdp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppText.BodySmall(
                    text = "Heading",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppText.HeadingLarge(
                    text = "${state.compassAzimuth.roundToInt()}°",
                    color = if (isFacingQibla) SuccessGreen else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
