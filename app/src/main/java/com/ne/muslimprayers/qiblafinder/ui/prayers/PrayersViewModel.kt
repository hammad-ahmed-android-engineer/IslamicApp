package com.ne.muslimprayers.qiblafinder.ui.prayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerAlarmsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerScheduleUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.TogglePrayerAlarmUseCase
import com.ne.muslimprayers.qiblafinder.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrayersUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val schedule: PrayerDaySchedule? = null,
    val alarms: List<PrayerAlarmItem> = emptyList(),
    val cityName: String = ""
)

@HiltViewModel
class PrayersViewModel @Inject constructor(
    private val getPrayerScheduleUseCase: GetPrayerScheduleUseCase,
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val togglePrayerAlarmUseCase: TogglePrayerAlarmUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val locationHelper: LocationHelper,
    private val prayerAlarmRepository: PrayerAlarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrayersUiState())
    val uiState: StateFlow<PrayersUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val location = locationHelper.getCurrentLocation()
            if (location == null) {
                val permissionGranted = locationHelper.hasLocationPermission()
                val gpsEnabled = locationHelper.isLocationEnabled()
                val msg = when {
                    !permissionGranted -> "Location permission is required to calculate accurate prayer times."
                    !gpsEnabled -> "GPS is turned off. Please turn on device location to calculate prayer times."
                    else -> "Unable to determine your current location. Please check location settings and tap retry."
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = msg,
                        cityName = "Location unavailable"
                    )
                }
                return@launch
            }

            val lat = location.latitude
            val lng = location.longitude
            val city = locationHelper.getCityAndCountry(lat, lng)

            _uiState.update { it.copy(cityName = city) }

            launch {
                getPrayerAlarmsUseCase().collect { alarms ->
                    _uiState.update { it.copy(alarms = alarms) }
                }
            }

            getPrayerScheduleUseCase(lat, lng).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val schedule = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                schedule = schedule
                            )
                        }
                        if (schedule != null) {
                            prayerAlarmRepository.syncPrayerAlarmsWithSchedule(schedule)
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

    fun toggleAlarm(prayerType: PrayerType, time: String, isCurrentlyActive: Boolean) {
        viewModelScope.launch {
            val alarmId = when (prayerType) {
                PrayerType.FAJR -> 0
                PrayerType.DHUHR -> 1
                PrayerType.ASR -> 2
                PrayerType.MAGHRIB -> 3
                PrayerType.ISHA -> 4
                else -> return@launch
            }

            val item = PrayerAlarmItem(
                id = alarmId,
                prayerType = prayerType,
                prayerTime = time,
                isAlarmActive = !isCurrentlyActive
            )

            togglePrayerAlarmUseCase(item, !isCurrentlyActive)

            if (!isCurrentlyActive) {
                alarmScheduler.scheduleAlarm(item)
            } else {
                alarmScheduler.cancelAlarm(alarmId)
            }
        }
    }
}
