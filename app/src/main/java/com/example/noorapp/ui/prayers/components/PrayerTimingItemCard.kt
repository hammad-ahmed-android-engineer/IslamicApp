package com.example.noorapp.ui.prayers.components

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
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
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
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
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
import com.example.noorapp.core.utils.arabicTitleRes
import com.example.noorapp.core.utils.titleRes
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.model.SinglePrayer

@Composable
fun PrayerTimingItemCard(
    prayer: SinglePrayer,
    isAlarmActive: Boolean,
    onToggleAlarm: () -> Unit
) {
    val icon = when (prayer.type) {
        PrayerType.FAJR -> Icons.Default.WbTwilight
        PrayerType.SUNRISE -> Icons.Default.WbSunny
        PrayerType.DHUHR -> Icons.Default.WbSunny
        PrayerType.ASR -> Icons.Default.WbSunny
        PrayerType.MAGHRIB -> Icons.Default.WbTwilight
        PrayerType.ISHA -> Icons.Default.NightsStay
    }

    val arabicName = stringResource(prayer.type.arabicTitleRes())

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (prayer.isNext) MedinaSageLight.copy(alpha = 0.35f) else PureAlabaster,
        borderColor = if (prayer.isNext) MedinaSage else DividerColor,
        borderWidth = if (prayer.isNext) AppDimensions.borderWidthMedium else AppDimensions.borderWidthThin,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingMediumSmall,
        elevation = if (prayer.isNext) AppDimensions.elevationLow else AppDimensions.elevationSubtle
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AppIconBadge(
                    icon = icon,
                    tint = if (prayer.isNext) PureAlabaster
                    else if (prayer.type == PrayerType.SUNRISE) DesertOchre
                    else MedinaSage,
                    backgroundColor = if (prayer.isNext) MedinaSage
                    else if (prayer.type == PrayerType.SUNRISE) DesertOchreLight.copy(alpha = 0.7f)
                    else MedinaSageLight
                )

                Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                    ) {
                        AppText.TitleMedium(
                            text = stringResource(prayer.type.titleRes()),
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        AppText.BodySmall(
                            text = arabicName,
                            color = DesertOchre,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (prayer.isNext) {
                            AppStatusPill(
                                text = stringResource(R.string.prayers_next_badge),
                                textColor = PureAlabaster,
                                backgroundColor = MedinaSage,
                                cornerRadius = AppDimensions.cornerExtraSmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                    AppText.Caption(
                        text = if (prayer.type == PrayerType.SUNRISE) {
                            stringResource(R.string.prayers_shuruq_subtitle)
                        } else if (isAlarmActive) {
                            stringResource(R.string.prayers_alarm_active_subtitle)
                        } else {
                            stringResource(R.string.prayers_alarm_muted_subtitle)
                        },
                        color = if (isAlarmActive && prayer.type != PrayerType.SUNRISE) MedinaSage else TextSecondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
            ) {
                AppText.TitleMedium(
                    text = prayer.timeFormatted,
                    fontWeight = FontWeight.Bold,
                    color = if (prayer.isNext) MedinaSage else TextPrimary
                )

                if (prayer.type != PrayerType.SUNRISE) {
                    AppCard(
                        onClick = onToggleAlarm,
                        cornerRadius = AppDimensions.cornerMediumSmall,
                        backgroundColor = if (isAlarmActive) MedinaSage else WarmParchment,
                        borderColor = if (isAlarmActive) MedinaSage else DividerColor,
                        borderWidth = AppDimensions.borderWidthThin,
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
                                imageVector = if (isAlarmActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = if (isAlarmActive) {
                                    stringResource(R.string.prayers_alarm_on_desc)
                                } else {
                                    stringResource(R.string.prayers_alarm_off_desc)
                                },
                                tint = if (isAlarmActive) PureAlabaster else TextSecondary,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                            AppText.Caption(
                                text = if (isAlarmActive) {
                                    stringResource(R.string.prayers_alarm_on_label)
                                } else {
                                    stringResource(R.string.prayers_alarm_off_label)
                                },
                                fontWeight = FontWeight.Bold,
                                color = if (isAlarmActive) PureAlabaster else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
