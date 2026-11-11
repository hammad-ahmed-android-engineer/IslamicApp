package com.example.noorapp.ui.home

import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.model.DuaItem
import com.example.noorapp.domain.model.Hadith
import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerDaySchedule
import com.example.noorapp.domain.model.SinglePrayer

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val schedule: PrayerDaySchedule? = null,
    val alarms: List<PrayerAlarmItem> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,
    val locationName: String = "",
    val hijriDate: String = "",
    val gregorianDate: String = "",
    val countdownText: String = "",
    val nextPrayerName: String = "",
    val currentPrayer: SinglePrayer? = null,
    val nextPrayer: SinglePrayer? = null,
    val dailyHadith: Hadith? = null,
    val dailyDua: DuaItem? = null,
    val selectedHadith: Hadith? = null,
    val todaySalahRecord: DailySalahRecord? = null
)
