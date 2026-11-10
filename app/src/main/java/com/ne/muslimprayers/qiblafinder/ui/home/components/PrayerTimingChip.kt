package com.ne.muslimprayers.qiblafinder.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun PrayerTimingChip(
    prayer: SinglePrayer,
    isAlarmActive: Boolean = prayer.isAlarmActive,
    onToggleAlarm: (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (prayer.type) {
        PrayerType.FAJR -> Icons.Default.WbTwilight
        PrayerType.SUNRISE -> Icons.Default.WbSunny
        PrayerType.DHUHR -> Icons.Default.WbSunny
        PrayerType.ASR -> Icons.Default.WbSunny
        PrayerType.MAGHRIB -> Icons.Default.NightsStay
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
        modifier = modifier
            .width(AppDimensions.prayerChipWidth)
            .clickableDebounced { onClick() },
        backgroundColor = if (prayer.isNext) MedinaSage else PureAlabaster,
        borderColor = if (prayer.isNext) MedinaSage else DividerColor,
        borderWidth = if (prayer.isNext) AppDimensions.borderWidthMedium else AppDimensions.borderWidthThin,
        cornerRadius = AppDimensions.cornerExtraLarge,
        elevation = if (prayer.isNext) AppDimensions.elevationMedium else AppDimensions.elevationSubtle,
        contentPadding = AppDimensions.paddingRegular
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Row: Prayer Icon + Alarm Enable/Disable Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.paddingExtraLarge)
                        .clip(CircleShape)
                        .background(if (prayer.isNext) DesertOchre.copy(alpha = 0.25f) else MedinaSageLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (prayer.isNext) DesertOchre else MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconRegular)
                    )
                }

                if (prayer.type != PrayerType.SUNRISE && onToggleAlarm != null) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.actionCircleSize)
                            .clip(CircleShape)
                            .background(
                                when {
                                    prayer.isNext && isAlarmActive -> DesertOchre.copy(alpha = 0.3f)
                                    prayer.isNext -> PureAlabaster.copy(alpha = 0.15f)
                                    isAlarmActive -> MedinaSageLight
                                    else -> DividerColor.copy(alpha = 0.35f)
                                }
                            )
                            .clickableDebounced { onToggleAlarm() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAlarmActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = if (isAlarmActive) "Disable ${prayer.type.displayName} Alarm" else "Enable ${prayer.type.displayName} Alarm",
                            tint = when {
                                prayer.isNext && isAlarmActive -> DesertOchreLight
                                prayer.isNext -> PureAlabaster.copy(alpha = 0.7f)
                                isAlarmActive -> MedinaSage
                                else -> TextSecondary
                            },
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

            // Prayer Name & Arabic
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AppText.BodySmall(
                    text = prayer.type.displayName,
                    fontWeight = FontWeight.Bold,
                    color = if (prayer.isNext) PureAlabaster else TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(AppDimensions.accentBarWidth))
                AppText.Caption(
                    text = arabicName,
                    color = if (prayer.isNext) DesertOchre else TextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))

            // Time Formatted
            AppText.TitleMedium(
                text = prayer.timeFormatted,
                fontWeight = FontWeight.Bold,
                color = if (prayer.isNext) DesertOchre else MedinaSage,
                maxLines = 1
            )
        }
    }
}
