package com.ne.muslimprayers.qiblafinder.ui.alarm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun AdhanAudioInfoCard(
    masterEnabled: Boolean = true,
    onToggleMaster: (Boolean) -> Unit = {},
    onEnableAll: () -> Unit = {},
    onDisableAll: () -> Unit = {},
    isPlayingPreview: Boolean = false,
    previewPrayerType: PrayerType? = null,
    onTogglePreview: (PrayerType) -> Unit = {}
) {
    IslamicCard(
        backgroundColor = PureAlabaster,
        borderColor = if (masterEnabled) MedinaSage.copy(alpha = 0.35f) else DividerColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.avatarSize)
                        .clip(CircleShape)
                        .background(if (masterEnabled) MedinaSageLight else WarmParchment),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (masterEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = if (masterEnabled) MedinaSage else TextSecondary,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
                Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))
                Column(modifier = Modifier.weight(1f)) {
                    AppText.TitleMedium(
                        text = "Prayer Alerts",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    AppText.Caption(
                        text = if (masterEnabled) "Daily prayer notifications active" else "All prayer notifications muted",
                        color = if (masterEnabled) MedinaSage else TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                Surface(
                    onClick = { onTogglePreview(PrayerType.DHUHR) },
                    shape = RoundedCornerShape(AppDimensions.cornerMediumSmall),
                    color = if (isPlayingPreview) DesertOchre else WarmParchment,
                    border = BorderStroke(
                        AppDimensions.borderWidthHairline,
                        if (isPlayingPreview) DesertOchre else DividerColor
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = AppDimensions.paddingRegular,
                            vertical = AppDimensions.paddingExtraSmall
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                    ) {
                        Icon(
                            imageVector = if (isPlayingPreview) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Test Sound",
                            tint = if (isPlayingPreview) PureAlabaster else MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconExtraSmall)
                        )
                        AppText.Caption(
                            text = if (isPlayingPreview) "Stop" else "Test",
                            fontWeight = FontWeight.Bold,
                            color = if (isPlayingPreview) PureAlabaster else TextPrimary
                        )
                    }
                }

                Switch(
                    checked = masterEnabled,
                    onCheckedChange = onToggleMaster,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PureAlabaster,
                        checkedTrackColor = MedinaSage,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DividerColor
                    )
                )
            }
        }
    }
}

@Composable
fun SunriseSunsetHeaderCard(
    sunriseTime: String,
    sunsetTime: String
) {
    IslamicCard(
        backgroundColor = PureAlabaster,
        borderColor = DesertOchre.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Sunrise Column
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.badgeCircleMedium)
                        .clip(CircleShape)
                        .background(DesertOchreLight.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Sunrise",
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
                Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                    ) {
                        AppText.Caption(
                            text = "Sunrise",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText.Caption(
                            text = "الشروق",
                            color = DesertOchre,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                    AppText.TitleMedium(
                        text = sunriseTime,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            VerticalDivider(
                modifier = Modifier.height(AppDimensions.buttonSmallHeight),
                thickness = AppDimensions.borderWidthThin,
                color = DividerColor
            )

            // Sunset Column
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.badgeCircleMedium)
                        .clip(CircleShape)
                        .background(MedinaSageLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WbTwilight,
                        contentDescription = "Sunset",
                        tint = MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
                Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                    ) {
                        AppText.Caption(
                            text = "Sunset",
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText.Caption(
                            text = "الغروب",
                            color = MedinaSage,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                    AppText.TitleMedium(
                        text = sunsetTime,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
