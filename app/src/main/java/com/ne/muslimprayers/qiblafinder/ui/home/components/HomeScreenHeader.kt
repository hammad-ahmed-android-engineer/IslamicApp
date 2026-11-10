package com.ne.muslimprayers.qiblafinder.ui.home.components

import androidx.compose.foundation.background
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun HomeScreenHeader(
    locationName: String,
    onRefreshLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    IslamicCard(
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
                        text = "Assalamu Alaikum",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                            .background(MedinaSageLight)
                            .padding(horizontal = AppDimensions.paddingExtraSmall, vertical = AppDimensions.paddingMicro)
                    ) {
                        AppText.Caption(
                            text = "السلام عليكم",
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
                        contentDescription = "Current Location",
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                    AppText.BodySmall(
                        text = locationName.ifBlank { "Detecting location..." },
                        color = TextSecondary,
                        maxLines = 1,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))

            // Refresh button
            Box(
                modifier = Modifier
                    .size(AppDimensions.buttonSmallHeight)
                    .clip(CircleShape)
                    .background(MedinaSageLight)
                    .clickableDebounced { onRefreshLocation() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Location & Timings",
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconRegular)
                )
            }
        }
    }
}
