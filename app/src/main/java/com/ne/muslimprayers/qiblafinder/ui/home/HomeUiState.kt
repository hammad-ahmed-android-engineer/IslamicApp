package com.ne.muslimprayers.qiblafinder.ui.home

import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val schedule: PrayerDaySchedule? = null,
    val alarms: List<PrayerAlarmItem> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,
    val locationName: String = "Detecting Location...",
    val hijriDate: String = "",
    val gregorianDate: String = "",
    val countdownText: String = "",
    val nextPrayerName: String = "",
    val currentPrayer: com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer? = null,
    val nextPrayer: com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer? = null,
    val dailyHadith: Hadith? = null,
    val dailyDua: DuaItem? = null,
    val todaySalahRecord: com.ne.muslimprayers.qiblafinder.domain.model.DailySalahRecord? = null
)
