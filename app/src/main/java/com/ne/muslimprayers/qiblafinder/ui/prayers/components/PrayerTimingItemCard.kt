package com.ne.muslimprayers.qiblafinder.ui.prayers.components

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
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

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

    val arabicName = when (prayer.type) {
        PrayerType.FAJR -> "الفجر"
        PrayerType.SUNRISE -> "الشروق"
        PrayerType.DHUHR -> "الظهر"
        PrayerType.ASR -> "العصر"
        PrayerType.MAGHRIB -> "المغرب"
        PrayerType.ISHA -> "العشاء"
    }

    IslamicCard(
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
                Box(
                    modifier = Modifier
                        .size(AppDimensions.avatarSize)
                        .clip(CircleShape)
                        .background(
                            if (prayer.isNext) MedinaSage
                            else if (prayer.type == PrayerType.SUNRISE) DesertOchreLight.copy(alpha = 0.7f)
                            else MedinaSageLight
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (prayer.isNext) PureAlabaster
                        else if (prayer.type == PrayerType.SUNRISE) DesertOchre
                        else MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }

                Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                    ) {
                        AppText.TitleMedium(
                            text = prayer.type.displayName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        AppText.BodySmall(
                            text = arabicName,
                            color = DesertOchre,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (prayer.isNext) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AppDimensions.cornerExtraSmall))
                                    .background(MedinaSage)
                                    .padding(
                                        horizontal = AppDimensions.paddingExtraSmall,
                                        vertical = AppDimensions.paddingMicro
                                    )
                            ) {
                                AppText.Caption(
                                    text = "NEXT",
                                    color = PureAlabaster,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                    AppText.Caption(
                        text = if (prayer.type == PrayerType.SUNRISE) {
                            "Shuruq • Sunrise time"
                        } else if (isAlarmActive) {
                            "Alarm & notification active"
                        } else {
                            "Alarm muted"
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
                    Surface(
                        onClick = onToggleAlarm,
                        shape = RoundedCornerShape(AppDimensions.cornerMediumSmall),
                        color = if (isAlarmActive) MedinaSage else WarmParchment,
                        border = BorderStroke(
                            width = AppDimensions.borderWidthThin,
                            color = if (isAlarmActive) MedinaSage else DividerColor
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
                                imageVector = if (isAlarmActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = if (isAlarmActive) "Alarm On" else "Alarm Off",
                                tint = if (isAlarmActive) PureAlabaster else TextSecondary,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                            AppText.Caption(
                                text = if (isAlarmActive) "ON" else "OFF",
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
