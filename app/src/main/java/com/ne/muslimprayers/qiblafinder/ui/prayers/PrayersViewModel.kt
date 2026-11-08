package com.ne.muslimprayers.qiblafinder.ui.prayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.PrayerCalculationMethod
import com.ne.muslimprayers.qiblafinder.core.common.PrayerSettingsManager
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

@HiltViewModel
class PrayersViewModel @Inject constructor(
    private val getPrayerScheduleUseCase: GetPrayerScheduleUseCase,
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val togglePrayerAlarmUseCase: TogglePrayerAlarmUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val locationHelper: LocationHelper,
    private val prayerAlarmRepository: PrayerAlarmRepository,
    private val prayerSettingsManager: PrayerSettingsManager
) : ViewModel() {

    private val initialSettings = prayerSettingsManager.getCurrentSettings()

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

    val calculationMethods: List<String> = PrayerCalculationMethod.entries.map { it.displayName }

    val asrSchools = listOf(
        "Standard (Shafi'i, Maliki, Hanbali)" to "Shadow length equals object length (1x)",
        "Hanafi School" to "Shadow length equals twice object length (2x)"
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
            prayerSettingsManager.settingsState.collect { settings ->
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
            prayerSettingsManager.settingsState
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
            prayerAlarmRepository.initDefaultAlarmsIfEmpty()
            getPrayerAlarmsUseCase().collect { alarms ->
                _uiState.update { it.copy(alarms = alarms) }
            }
        }
    }

    fun loadData() {
        scheduleJob?.cancel()
        scheduleJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val location = locationHelper.getCurrentLocation() ?: locationHelper.getEffectiveLocation()
            val lat = location.latitude
            val lng = location.longitude
            val city = locationHelper.getCityAndCountry(lat, lng)

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

    fun selectCalculationMethod(index: Int) {
        prayerSettingsManager.setManualCalculationMethod(index)
        _uiState.update { it.copy(showMethodDialog = false) }
    }

    fun resetMethodToAutoDetect() {
        viewModelScope.launch {
            val loc = locationHelper.getLastSavedLocation() ?: locationHelper.getCurrentLocation()
            prayerSettingsManager.resetToAutoDetect(
                latitude = loc?.latitude ?: 0.0,
                longitude = loc?.longitude ?: 0.0
            )
            _uiState.update { it.copy(showMethodDialog = false) }
        }
    }

    fun selectAsrJuristic(index: Int) {
        prayerSettingsManager.setManualAsrJuristic(index)
        _uiState.update { it.copy(showAsrDialog = false) }
    }

    fun selectHijriOffset(offset: Int) {
        prayerSettingsManager.setHijriOffset(offset)
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
                prayerSettingsManager.setNotificationsEnabled(true)
            }

            val item = PrayerAlarmItem(
                id = alarmId,
                prayerType = prayerType,
                prayerTime = time,
                isAlarmActive = nextState
            )

            togglePrayerAlarmUseCase(item, nextState)

            if (nextState) {
                alarmScheduler.scheduleAlarm(item)
            } else {
                alarmScheduler.cancelAlarm(alarmId)
            }
        }
    }
}
