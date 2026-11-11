package com.example.noorapp.ui.calendar

import com.example.noorapp.domain.model.IslamicCalendarInfo
import java.util.Calendar

data class CalendarUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val calendarDays: List<IslamicCalendarInfo> = emptyList(),
    val selectedDay: IslamicCalendarInfo? = null,
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
)

