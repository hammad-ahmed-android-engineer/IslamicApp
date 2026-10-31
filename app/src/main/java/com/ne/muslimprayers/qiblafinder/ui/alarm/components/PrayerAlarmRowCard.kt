package com.ne.muslimprayers.qiblafinder.ui.alarm.components

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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.utils.DateTimeUtils
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun PrayerAlarmRowCard(
    alarm: PrayerAlarmItem,
    onToggle: () -> Unit
) {
    val isFajr = alarm.prayerType == PrayerType.FAJR
    val soundName = if (isFajr) "Adhan Fajr" else "Adhan Makkah"
    val timeFormatted = DateTimeUtils.formatTo12Hour(alarm.prayerTime)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.sdp))
            .border(
                width = 1.sdp,
                color = if (alarm.isAlarmActive) MedinaSage else DividerColor,
                shape = RoundedCornerShape(20.sdp)
            ),
        shape = RoundedCornerShape(20.sdp),
        color = PureAlabaster,
        tonalElevation = 1.sdp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.sdp),
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
                            if (alarm.isAlarmActive) MedinaSageLight else DividerColor.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (alarm.isAlarmActive) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = if (alarm.isAlarmActive) MedinaSage else TextSecondary,
                        modifier = Modifier.size(24.sdp)
                    )
                }

                Spacer(modifier = Modifier.width(14.sdp))

                Column {
                    AppText.TitleMedium(
                        text = alarm.prayerType.displayName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.sdp))
                    AppText.HeadingMedium(
                        text = timeFormatted,
                        color = MedinaSage
                    )
                    AppText.Caption(
                        text = "Sound: $soundName",
                        color = TextSecondary
                    )
                }
            }

            Switch(
                checked = alarm.isAlarmActive,
                onCheckedChange = { onToggle() },
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
