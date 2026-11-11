package com.example.noorapp.ui.alarm.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppIconBadge
import com.example.noorapp.core.designsystem.AppSwitch
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppVerticalDivider
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.domain.model.PrayerType

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
    AppCard(
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
                AppIconBadge(
                    icon = if (masterEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    tint = if (masterEnabled) MedinaSage else TextSecondary,
                    backgroundColor = if (masterEnabled) MedinaSageLight else WarmParchment
                )
                Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))
                Column(modifier = Modifier.weight(1f)) {
                    AppText.TitleMedium(
                        text = stringResource(R.string.prayer_alarm_alerts_title),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    AppText.Caption(
                        text = if (masterEnabled) {
                            stringResource(R.string.prayer_alarm_alerts_active)
                        } else {
                            stringResource(R.string.prayer_alarm_alerts_muted)
                        },
                        color = if (masterEnabled) MedinaSage else TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                AppCard(
                    onClick = { onTogglePreview(PrayerType.DHUHR) },
                    cornerRadius = AppDimensions.cornerMediumSmall,
                    backgroundColor = if (isPlayingPreview) DesertOchre else WarmParchment,
                    borderColor = if (isPlayingPreview) DesertOchre else DividerColor,
                    borderWidth = AppDimensions.borderWidthHairline,
                    contentPadding = AppDimensions.paddingZero,
                    elevation = AppDimensions.elevationNone
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
                            contentDescription = stringResource(R.string.prayer_alarm_test_sound_desc),
                            tint = if (isPlayingPreview) PureAlabaster else MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconExtraSmall)
                        )
                        AppText.Caption(
                            text = if (isPlayingPreview) {
                                stringResource(R.string.prayer_alarm_stop)
                            } else {
                                stringResource(R.string.prayer_alarm_test)
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (isPlayingPreview) PureAlabaster else TextPrimary
                        )
                    }
                }

                AppSwitch(
                    checked = masterEnabled,
                    onCheckedChange = onToggleMaster
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
    AppCard(
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
                        contentDescription = stringResource(R.string.prayer_sunrise),
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
                            text = stringResource(R.string.prayer_sunrise),
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText.Caption(
                            text = stringResource(R.string.prayer_sunrise_arabic),
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

            AppVerticalDivider(
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
                        contentDescription = stringResource(R.string.prayer_sunset),
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
                            text = stringResource(R.string.prayer_sunset),
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        AppText.Caption(
                            text = stringResource(R.string.prayer_sunset_arabic),
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
