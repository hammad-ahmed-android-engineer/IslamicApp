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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.common.PrayerNotificationMode
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppIconBadge
import com.example.noorapp.core.designsystem.AppSelectableCard
import com.example.noorapp.core.designsystem.AppSwitch
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.DateTimeUtils
import com.example.noorapp.core.utils.arabicTitleRes
import com.example.noorapp.core.utils.titleRes
import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerType

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

    val arabicName = stringResource(alarm.prayerType.arabicTitleRes())

    AppCard(
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
                    AppIconBadge(
                        icon = if (isEffectiveActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        tint = if (isEffectiveActive) MedinaSage else TextSecondary,
                        backgroundColor = if (isEffectiveActive) MedinaSageLight else WarmParchment
                    )

                    Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                        ) {
                            AppText.TitleMedium(
                                text = stringResource(alarm.prayerType.titleRes()),
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

                AppSwitch(
                    checked = isEffectiveActive,
                    onCheckedChange = { onToggle() }
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
                            PrayerNotificationMode.ADHAN -> stringResource(R.string.prayer_alarm_mode_short_adhan)
                            PrayerNotificationMode.TONE -> stringResource(R.string.prayer_alarm_mode_tone)
                            PrayerNotificationMode.SILENT -> stringResource(R.string.prayers_alarm_mode_silent)
                        }

                        AppSelectableCard(
                            isSelected = isSelected,
                            onClick = { onSelectMode(mode) },
                            modifier = Modifier.weight(1f),
                            selectedBackgroundColor = MedinaSage,
                            unselectedBackgroundColor = WarmParchment,
                            selectedBorderColor = MedinaSage,
                            unselectedBorderColor = DividerColor,
                            borderWidth = AppDimensions.borderWidthHairline,
                            cornerRadius = AppDimensions.cornerSmall,
                            contentPadding = AppDimensions.paddingZero,
                            elevation = AppDimensions.elevationNone
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
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
