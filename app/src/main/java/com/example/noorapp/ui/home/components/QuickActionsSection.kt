package com.example.noorapp.ui.home.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.QuickActionCard
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.TextPrimary

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
            text = stringResource(R.string.home_islamic_tools),
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = AppDimensions.paddingRegular)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionCard(
                title = stringResource(R.string.home_tool_qibla_title),
                subtitle = stringResource(R.string.home_tool_qibla_subtitle),
                icon = Icons.Default.CompassCalibration,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToCompass
            )
            QuickActionCard(
                title = stringResource(R.string.home_tool_prayers_title),
                subtitle = stringResource(R.string.home_tool_prayers_subtitle),
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToPrayersTiming
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionCard(
                title = stringResource(R.string.home_tool_calendar_title),
                subtitle = stringResource(R.string.home_tool_calendar_subtitle),
                icon = Icons.Default.CalendarMonth,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToCalendar
            )
            QuickActionCard(
                title = stringResource(R.string.home_tool_alarms_title),
                subtitle = stringResource(R.string.home_tool_alarms_subtitle),
                icon = Icons.Default.NotificationsActive,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToPrayerAlarm
            )
        }
    }
}
