package com.example.noorapp.ui.salahtracker

import androidx.annotation.StringRes
import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.model.PrayerType

data class DayTrackerUiModel(
    val dateString: String, // "yyyy-MM-dd"
    val dayName: String, // "Mon", "Tue", ...
    val dayNumber: Int, // 1..31
    val isToday: Boolean,
    val isSelected: Boolean,
    val completedCount: Int,
    val totalCount: Int = 5
) {
    val isAllCompleted: Boolean get() = completedCount >= totalCount
}

data class SalahPrayerItemUi(
    val type: PrayerType,
    @StringRes val englishNameRes: Int,
    @StringRes val arabicNameRes: Int,
    @StringRes val descriptionRes: Int,
    val isOffered: Boolean
)

data class SalahTrackerUiState(
    val selectedDate: String = "",
    val selectedDateFormatted: String = "",
    val weekOffset: Int = 0,
    val weekRangeFormatted: String = "",
    val weekDays: List<DayTrackerUiModel> = emptyList(),
    val currentDayRecord: DailySalahRecord = DailySalahRecord(date = ""),
    val prayersList: List<SalahPrayerItemUi> = emptyList(),
    val weeklyCompletedCount: Int = 0,
    val weeklyTotalCount: Int = 35,
    val weeklyPercentage: Float = 0f,
    val prayerWeeklyBreakdown: Map<PrayerType, Pair<Int, Int>> = emptyMap(),
    val isToday: Boolean = true
)
