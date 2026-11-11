package com.example.noorapp.ui.prayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.R
import com.example.noorapp.core.common.PrayerCalculationMethod
import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.usecase.GetPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.GetPrayerScheduleUseCase
import com.example.noorapp.domain.usecase.SchedulePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.SyncPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.TogglePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.location.GetCurrentLocationUseCase
import com.example.noorapp.domain.usecase.settings.ObservePrayerSettingsUseCase
import com.example.noorapp.domain.usecase.settings.UpdatePrayerSettingsUseCase
import com.example.noorapp.ui.more.components.AsrSchoolOption
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
import javax.inject.Inject

@HiltViewModel
class PrayersViewModel @Inject constructor(
    private val getPrayerScheduleUseCase: GetPrayerScheduleUseCase,
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val togglePrayerAlarmUseCase: TogglePrayerAlarmUseCase,
    private val syncPrayerAlarmsUseCase: SyncPrayerAlarmsUseCase,
    private val schedulePrayerAlarmUseCase: SchedulePrayerAlarmUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val observePrayerSettingsUseCase: ObservePrayerSettingsUseCase,
    private val updatePrayerSettingsUseCase: UpdatePrayerSettingsUseCase
) : ViewModel() {

    private val initialSettings = observePrayerSettingsUseCase.current()

    private val _uiState = MutableStateFlow(
        PrayersUiState(
            notificationsEnabled = initialSettings.notificationsEnabled,
            selectedMethodIndex = initialSettings.methodIndex,
            selectedAsrJuristic = initialSettings.asrJuristic,
            selectedHijriOffset = initialSettings.hijriOffset,
            isMethodManuallySelected = initialSettings.isMethodManuallySelected,
            isAsrManuallySelected = initialSettings.isAsrManuallySelected
        )
    )
    val uiState: StateFlow<PrayersUiState> = _uiState.asStateFlow()

    val calculationMethodResIds: List<Int> = PrayerCalculationMethod.entries.map { it.displayNameRes }

    val asrSchools = listOf(
        AsrSchoolOption(
            titleRes = R.string.prayers_asr_standard_title,
            descRes = R.string.prayers_asr_standard_desc
        ),
        AsrSchoolOption(
            titleRes = R.string.prayers_asr_hanafi_title,
            descRes = R.string.prayers_asr_hanafi_desc
        )
    )

    val hijriOffsets = listOf(-2, -1, 0, 1, 2)

    private var scheduleJob: Job? = null

    init {
        observeSettings()
        observeAlarms()
        loadData()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observePrayerSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        notificationsEnabled = settings.notificationsEnabled,
                        selectedMethodIndex = settings.methodIndex,
                        selectedAsrJuristic = settings.asrJuristic,
                        selectedHijriOffset = settings.hijriOffset,
                        isMethodManuallySelected = settings.isMethodManuallySelected,
                        isAsrManuallySelected = settings.isAsrManuallySelected
                    )
                }
            }
        }
        viewModelScope.launch {
            observePrayerSettingsUseCase()
                .map { Triple(it.methodIndex, it.asrJuristic, it.hijriOffset) }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    loadData()
                }
        }
    }

    private fun observeAlarms() {
        viewModelScope.launch {
            syncPrayerAlarmsUseCase.initDefaultIfEmpty()
            getPrayerAlarmsUseCase().collect { alarms ->
                _uiState.update { it.copy(alarms = alarms) }
            }
        }
    }

    fun loadData() {
        scheduleJob?.cancel()
        scheduleJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val location = getCurrentLocationUseCase(includeCityName = true)
            val lat = location.latitude
            val lng = location.longitude
            val city = location.cityName

            _uiState.update { it.copy(cityName = city, errorMessage = null) }

            getPrayerScheduleUseCase(lat, lng).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (_uiState.value.schedule == null) {
                            _uiState.update { it.copy(isLoading = true) }
                        }
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
                            syncPrayerAlarmsUseCase.syncWithSchedule(schedule)
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

    fun selectCalculationMethod(index: Int) {
        updatePrayerSettingsUseCase.setCalculationMethod(index)
        _uiState.update { it.copy(showMethodDialog = false) }
    }

    fun resetMethodToAutoDetect() {
        viewModelScope.launch {
            val savedCoords = getCurrentLocationUseCase.getLastSavedCoordinates()
            val coords = savedCoords ?: getCurrentLocationUseCase(includeCityName = false).let {
                it.latitude to it.longitude
            }
            updatePrayerSettingsUseCase.resetMethodToAutoDetect(
                latitude = coords.first,
                longitude = coords.second
            )
            _uiState.update { it.copy(showMethodDialog = false) }
        }
    }

    fun selectAsrJuristic(index: Int) {
        updatePrayerSettingsUseCase.setAsrJuristic(index)
        _uiState.update { it.copy(showAsrDialog = false) }
    }

    fun selectHijriOffset(offset: Int) {
        updatePrayerSettingsUseCase.setHijriOffset(offset)
        _uiState.update { it.copy(showHijriOffsetDialog = false) }
    }

    fun toggleMethodDialog(show: Boolean) {
        _uiState.update { it.copy(showMethodDialog = show) }
    }

    fun toggleAsrDialog(show: Boolean) {
        _uiState.update { it.copy(showAsrDialog = show) }
    }

    fun toggleHijriOffsetDialog(show: Boolean) {
        _uiState.update { it.copy(showHijriOffsetDialog = show) }
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

            val nextState = !isCurrentlyActive
            if (nextState && !_uiState.value.notificationsEnabled) {
                updatePrayerSettingsUseCase.setNotificationsEnabled(true)
            }

            val item = PrayerAlarmItem(
                id = alarmId,
                prayerType = prayerType,
                prayerTime = time,
                isAlarmActive = nextState
            )

            togglePrayerAlarmUseCase(item, nextState)

            if (nextState) {
                schedulePrayerAlarmUseCase.schedule(item)
            } else {
                schedulePrayerAlarmUseCase.cancel(alarmId)
            }
        }
    }
}
