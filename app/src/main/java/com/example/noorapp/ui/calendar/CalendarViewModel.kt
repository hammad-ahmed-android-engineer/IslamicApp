package com.example.noorapp.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.IslamicCalendarInfo
import com.example.noorapp.domain.usecase.GetIslamicCalendarUseCase
import com.example.noorapp.domain.usecase.location.GetCurrentLocationUseCase
import com.example.noorapp.domain.usecase.settings.ObservePrayerSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getIslamicCalendarUseCase: GetIslamicCalendarUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val observePrayerSettingsUseCase: ObservePrayerSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private var calendarLoadJob: Job? = null

    init {
        loadCalendar(
            year = _uiState.value.selectedYear,
            month = _uiState.value.selectedMonth
        )
        viewModelScope.launch {
            observePrayerSettingsUseCase()
                .map { Triple(it.methodIndex, it.asrJuristic, it.hijriOffset) }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    loadCalendar(
                        year = _uiState.value.selectedYear,
                        month = _uiState.value.selectedMonth
                    )
                }
        }
    }

    fun loadCalendar(year: Int, month: Int) {
        calendarLoadJob?.cancel()
        calendarLoadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, selectedYear = year, selectedMonth = month) }

            val location = getCurrentLocationUseCase(includeCityName = false)
            val lat = location.latitude
            val lng = location.longitude

            getIslamicCalendarUseCase(lat, lng, year, month).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val days = result.data.orEmpty()
                        val currentDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        val selected = days.find { it.gregorianDay == currentDayOfMonth } ?: days.firstOrNull()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                calendarDays = days,
                                selectedDay = selected
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectDay(day: IslamicCalendarInfo) {
        _uiState.update { it.copy(selectedDay = day) }
    }
}
