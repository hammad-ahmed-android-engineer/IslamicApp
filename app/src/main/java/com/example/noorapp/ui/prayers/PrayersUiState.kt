package com.example.noorapp.ui.prayers

import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerDaySchedule

data class PrayersUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val schedule: PrayerDaySchedule? = null,
    val alarms: List<PrayerAlarmItem> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val cityName: String = "",
    val selectedMethodIndex: Int = 2,
    val selectedAsrJuristic: Int = 0,
    val selectedHijriOffset: Int = 0,
    val isMethodManuallySelected: Boolean = false,
    val isAsrManuallySelected: Boolean = false,
    val showMethodDialog: Boolean = false,
    val showAsrDialog: Boolean = false,
    val showHijriOffsetDialog: Boolean = false
)

