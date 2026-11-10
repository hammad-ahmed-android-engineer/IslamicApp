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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.common.PrayerNotificationMode
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.utils.DateTimeUtils
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun PrayerAlarmRowCard(
    alarm: PrayerAlarmItem,
    notificationMode: PrayerNotificationMode = PrayerNotificationMode.ADHAN,
    masterEnabled: Boolean = true,
    onToggle: () -> Unit,
    onSelectMode: (PrayerNotificationMode) -> Unit = {}
) {
    val isEffectiveActive = masterEnabled && alarm.isAlarmActive
    val timeFormatted = DateTimeUtils.formatTo12Hour(alarm.prayerTime)

    val arabicName = when (alarm.prayerType) {
        PrayerType.FAJR -> "الفجر"
        PrayerType.SUNRISE -> "الشروق"
        PrayerType.DHUHR -> "الظهر"
        PrayerType.ASR -> "العصر"
        PrayerType.MAGHRIB -> "المغرب"
        PrayerType.ISHA -> "العشاء"
    }

    IslamicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = if (isEffectiveActive) MedinaSage.copy(alpha = 0.45f) else DividerColor,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingMediumSmall,
        elevation = AppDimensions.elevationSubtle
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                            .background(if (isEffectiveActive) MedinaSageLight else WarmParchment),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEffectiveActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (isEffectiveActive) MedinaSage else TextSecondary,
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
                                text = alarm.prayerType.displayName,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            AppText.BodySmall(
                                text = arabicName,
                                color = DesertOchre,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        AppText.TitleMedium(
                            text = timeFormatted,
                            fontWeight = FontWeight.Bold,
                            color = if (isEffectiveActive) MedinaSage else TextSecondary
                        )
                    }
                }

                Switch(
                    checked = isEffectiveActive,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PureAlabaster,
                        checkedTrackColor = MedinaSage,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DividerColor
                    )
                )
            }

            if (isEffectiveActive) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                ) {
                    PrayerNotificationMode.entries.forEach { mode ->
                        val isSelected = notificationMode == mode
                        val modeIcon = when (mode) {
                            PrayerNotificationMode.ADHAN -> Icons.Default.VolumeUp
                            PrayerNotificationMode.TONE -> Icons.Default.Notifications
                            PrayerNotificationMode.SILENT -> Icons.Default.VolumeOff
                        }
                        val shortTitle = when (mode) {
                            PrayerNotificationMode.ADHAN -> "Short Adhan"
                            PrayerNotificationMode.TONE -> "Alarm Tone"
                            PrayerNotificationMode.SILENT -> "Silent"
                        }

                        Surface(
                            onClick = { onSelectMode(mode) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(AppDimensions.cornerSmall),
                            color = if (isSelected) MedinaSage else WarmParchment,
                            border = BorderStroke(
                                width = AppDimensions.borderWidthHairline,
                                color = if (isSelected) MedinaSage else DividerColor
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    vertical = AppDimensions.paddingExtraSmall,
                                    horizontal = AppDimensions.paddingTiny
                                ),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = modeIcon,
                                    contentDescription = shortTitle,
                                    tint = if (isSelected) PureAlabaster else TextSecondary,
                                    modifier = Modifier.size(AppDimensions.iconExtraSmall)
                                )
                                Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                                AppText.Caption(
                                    text = shortTitle,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PureAlabaster else TextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
