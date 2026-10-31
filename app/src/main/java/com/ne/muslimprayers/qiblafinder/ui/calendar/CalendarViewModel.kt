package com.ne.muslimprayers.qiblafinder.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.IslamicCalendarInfo
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetIslamicCalendarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class CalendarUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val calendarDays: List<IslamicCalendarInfo> = emptyList(),
    val selectedDay: IslamicCalendarInfo? = null,
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getIslamicCalendarUseCase: GetIslamicCalendarUseCase,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendar(
            year = _uiState.value.selectedYear,
            month = _uiState.value.selectedMonth
        )
    }

    fun loadCalendar(year: Int, month: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, selectedYear = year, selectedMonth = month) }

            val location = locationHelper.getCurrentLocation()
            if (location == null) {
                val permissionGranted = locationHelper.hasLocationPermission()
                val gpsEnabled = locationHelper.isLocationEnabled()
                val msg = when {
                    !permissionGranted -> "Location permission is required to calculate Islamic calendar prayer times."
                    !gpsEnabled -> "GPS is turned off. Please turn on device location to view the calendar."
                    else -> "Unable to determine your current location. Please check location settings and tap retry."
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = msg
                    )
                }
                return@launch
            }

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
