package com.example.noorapp.ui.compass.components

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
import androidx.compose.ui.res.stringResource
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.SuccessGreen
import com.example.noorapp.domain.model.QiblaState
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ReadingsCard(
    state: QiblaState,
    isFacingQibla: Boolean
) {
    AppCard(
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppText.BodySmall(
                    text = stringResource(R.string.compass_qibla_bearing_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppText.TitleMedium(
                    text = String.format(
                        Locale.US,
                        stringResource(R.string.compass_degree_decimal_format),
                        state.qiblaBearing
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                modifier = Modifier
                    .width(AppDimensions.borderWidthThin)
                    .height(AppDimensions.paddingExtraLarge)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppText.BodySmall(
                    text = stringResource(R.string.compass_device_heading_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AppText.TitleMedium(
                    text = stringResource(
                        R.string.compass_degree_int_format,
                        state.compassAzimuth.roundToInt()
                    ),
                    color = if (isFacingQibla) SuccessGreen else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
