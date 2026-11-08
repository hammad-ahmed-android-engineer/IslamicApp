package com.ne.muslimprayers.qiblafinder.ui.alarm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ne.muslimprayers.qiblafinder.core.common.PrayerNotificationMode
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.utils.DateTimeUtils
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun PrayerAlarmRowCard(
    alarm: PrayerAlarmItem,
    notificationMode: PrayerNotificationMode = PrayerNotificationMode.ADHAN,
    masterEnabled: Boolean = true,
    onToggle: () -> Unit,
    onSelectMode: (PrayerNotificationMode) -> Unit = {}
) {
    val isEffectiveActive = masterEnabled && alarm.isAlarmActive
    val isFajr = alarm.prayerType == PrayerType.FAJR
    val adhanLabel = if (isFajr) "Adhan Fajr" else "Adhan Makkah"
    val timeFormatted = DateTimeUtils.formatTo12Hour(alarm.prayerTime)

    val arabicName = when (alarm.prayerType) {
        PrayerType.FAJR -> "الفجر"
        PrayerType.SUNRISE -> "الشروق"
        PrayerType.DHUHR -> "الظهر"
        PrayerType.ASR -> "العصر"
        PrayerType.MAGHRIB -> "المغرب"
        PrayerType.ISHA -> "العشاء"
    }

    val statusSubtitle = when {
        !isEffectiveActive -> "Notification disabled for ${alarm.prayerType.displayName}"
        notificationMode == PrayerNotificationMode.ADHAN -> "Alert: $adhanLabel (Audio + Notification)"
        notificationMode == PrayerNotificationMode.TONE -> "Alert: Standard Notification Tone"
        else -> "Alert: Silent Visual Notification"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.sdp))
            .border(
                width = 1.sdp,
                color = if (isEffectiveActive) MedinaSage else DividerColor,
                shape = RoundedCornerShape(20.sdp)
            ),
        shape = RoundedCornerShape(20.sdp),
        color = PureAlabaster,
        tonalElevation = 1.sdp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.sdp)
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
                            .size(46.sdp)
                            .clip(CircleShape)
                            .background(
                                if (isEffectiveActive) MedinaSageLight else DividerColor.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEffectiveActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (isEffectiveActive) MedinaSage else TextSecondary,
                            modifier = Modifier.size(24.sdp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.sdp))

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.sdp)
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
                        Spacer(modifier = Modifier.height(2.sdp))
                        AppText.HeadingMedium(
                            text = timeFormatted,
                            color = if (isEffectiveActive) MedinaSage else TextSecondary
                        )
                        AppText.Caption(
                            text = statusSubtitle,
                            color = TextSecondary
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

            // Per-Prayer Specific Notification Mode Selector (Full Adhan / Tone / Silent)
            if (isEffectiveActive) {
                Spacer(modifier = Modifier.height(12.sdp))
                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.sdp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
                    PrayerNotificationMode.entries.forEach { mode ->
                        val isSelected = notificationMode == mode
                        val modeIcon = when (mode) {
                            PrayerNotificationMode.ADHAN -> Icons.Default.VolumeUp
                            PrayerNotificationMode.TONE -> Icons.Default.Notifications
                            PrayerNotificationMode.SILENT -> Icons.Default.VolumeOff
                        }
                        val shortTitle = when (mode) {
                            PrayerNotificationMode.ADHAN -> "Adhan Audio"
                            PrayerNotificationMode.TONE -> "Beep / Tone"
                            PrayerNotificationMode.SILENT -> "Silent"
                        }

                        Surface(
                            onClick = { onSelectMode(mode) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.sdp),
                            color = if (isSelected) MedinaSage else WarmParchment,
                            border = BorderStroke(
                                width = if (isSelected) 1.dp else 0.8.dp,
                                color = if (isSelected) MedinaSage else DividerColor
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.sdp, horizontal = 6.sdp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = modeIcon,
                                    contentDescription = shortTitle,
                                    tint = if (isSelected) PureAlabaster else TextSecondary,
                                    modifier = Modifier.size(14.sdp)
                                )
                                Spacer(modifier = Modifier.width(4.sdp))
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
