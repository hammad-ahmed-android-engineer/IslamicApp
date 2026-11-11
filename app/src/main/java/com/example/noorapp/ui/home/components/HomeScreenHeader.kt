package com.example.noorapp.ui.home.components

import androidx.compose.foundation.background
import com.example.noorapp.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCircleIconButton
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary

@Composable
fun HomeScreenHeader(
    locationName: String,
    onRefreshLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        cornerRadius = AppDimensions.cornerExtraLarge,
        elevation = AppDimensions.elevationLow,
        contentPadding = AppDimensions.paddingZero
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingMediumSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppText.TitleMedium(
                        text = stringResource(R.string.home_assalamu_alaikum),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                            .background(MedinaSageLight)
                            .padding(horizontal = AppDimensions.paddingExtraSmall, vertical = AppDimensions.paddingMicro)
                    ) {
                        AppText.Caption(
                            text = stringResource(R.string.home_assalamu_alaikum_arabic),
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage
                        )
                    }
                }

                Spacer(modifier = Modifier.size(AppDimensions.paddingTiny))

                // Location badge with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                        .clickableDebounced { onRefreshLocation() }
                        .padding(vertical = AppDimensions.paddingMicro)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.home_current_location_desc),
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                    AppText.BodySmall(
                        text = locationName.ifBlank { stringResource(R.string.detecting_location) },
                        color = TextSecondary,
                        maxLines = 1,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))

            // Refresh button
            AppCircleIconButton(
                icon = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.home_refresh_location_timings_desc),
                onClick = onRefreshLocation,
                size = AppDimensions.buttonSmallHeight,
                iconSize = AppDimensions.iconRegular,
                tint = MedinaSage,
                containerColor = MedinaSageLight,
                borderColor = MedinaSageLight
            )
        }
    }
}
