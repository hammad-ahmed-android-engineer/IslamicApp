package com.example.noorapp.ui.alarm

import com.example.noorapp.core.common.PrayerNotificationMode
import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerType

data class PrayerAlarmUiState(
    val alarms: List<PrayerAlarmItem> = emptyList(),
    val notificationModes: Map<PrayerType, PrayerNotificationMode> = emptyMap(),
    val masterNotificationsEnabled: Boolean = true,
    val sunriseTime: String = "",
    val sunsetTime: String = "",
    val isPlayingPreview: Boolean = false,
    val previewPrayerType: PrayerType? = null,
    val isLoading: Boolean = false
)

