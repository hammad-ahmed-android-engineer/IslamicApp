package com.ne.muslimprayers.qiblafinder.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.QuickActionCard

@Composable
fun QuickActionsSection(
    onNavigateToCompass: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToPrayersTiming: () -> Unit,
    onNavigateToPrayerAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AppText.HeadingMedium(
            text = "Islamic Tools",
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 10.sdp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.sdp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionCard(
                title = "Qibla Finder",
                subtitle = "3D Compass",
                icon = Icons.Default.CompassCalibration,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToCompass
            )
            QuickActionCard(
                title = "Prayer Times",
                subtitle = "Full Schedule",
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToPrayersTiming
            )
        }

        Spacer(modifier = Modifier.height(10.sdp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.sdp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionCard(
                title = "Hijri Calendar",
                subtitle = "Islamic Dates",
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToCalendar
            )
            QuickActionCard(
                title = "Adhan Alarms",
                subtitle = "Audio Alerts",
                icon = Icons.Default.NotificationsActive,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToPrayerAlarm
            )
        }
    }
}
