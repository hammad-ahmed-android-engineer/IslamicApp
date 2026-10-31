package com.ne.muslimprayers.qiblafinder.ui.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun HomeScreenHeader(
    locationName: String,
    onRefreshLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.sdp))
            .border(1.sdp, DividerColor, RoundedCornerShape(20.sdp)),
        shape = RoundedCornerShape(20.sdp),
        color = PureAlabaster,
        tonalElevation = 2.sdp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp, vertical = 12.sdp),
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
                    Spacer(modifier = Modifier.width(6.sdp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.sdp))
                            .background(MedinaSageLight)
                            .padding(horizontal = 6.sdp, vertical = 2.sdp)
                    ) {
                        AppText.Caption(
                            text = "السلام عليكم",
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage
                        )
                    }
                }

                Spacer(modifier = Modifier.size(4.sdp))

                // Location badge with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.sdp))
                        .clickableDebounced { onRefreshLocation() }
                        .padding(vertical = 2.sdp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Current Location",
                        tint = DesertOchre,
                        modifier = Modifier.size(15.sdp)
                    )
                    Spacer(modifier = Modifier.width(4.sdp))
                    AppText.BodySmall(
                        text = locationName.ifBlank { "Detecting location..." },
                        color = TextSecondary,
                        maxLines = 1,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.sdp))

            // Refresh button
            Box(
                modifier = Modifier
                    .size(36.sdp)
                    .clip(CircleShape)
                    .background(MedinaSageLight)
                    .clickableDebounced { onRefreshLocation() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Location & Timings",
                    tint = MedinaSage,
                    modifier = Modifier.size(18.sdp)
                )
            }
        }
    }
}
