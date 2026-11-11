package com.example.noorapp.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.R
import com.example.noorapp.core.common.PrayerNotificationMode
import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.usecase.GetPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.GetPrayerScheduleUseCase
import com.example.noorapp.domain.usecase.SchedulePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.SyncPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.TogglePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.audio.PlayAudioPreviewUseCase
import com.example.noorapp.domain.usecase.location.GetCurrentLocationUseCase
import com.example.noorapp.domain.usecase.settings.ObservePrayerSettingsUseCase
import com.example.noorapp.domain.usecase.settings.UpdatePrayerSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerAlarmViewModel @Inject constructor(
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val togglePrayerAlarmUseCase: TogglePrayerAlarmUseCase,
    private val getPrayerScheduleUseCase: GetPrayerScheduleUseCase,
    private val syncPrayerAlarmsUseCase: SyncPrayerAlarmsUseCase,
    private val schedulePrayerAlarmUseCase: SchedulePrayerAlarmUseCase,
    private val observePrayerSettingsUseCase: ObservePrayerSettingsUseCase,
    private val updatePrayerSettingsUseCase: UpdatePrayerSettingsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val playAudioPreviewUseCase: PlayAudioPreviewUseCase
) : ViewModel() {

    private val initialSettings = observePrayerSettingsUseCase.current()

    private val _uiState = MutableStateFlow(
        PrayerAlarmUiState(
            masterNotificationsEnabled = initialSettings.notificationsEnabled,
            notificationModes = initialSettings.prayerNotificationModes
        )
    )
    val uiState: StateFlow<PrayerAlarmUiState> = _uiState.asStateFlow()

    private var previewStopJob: Job? = null

    init {
        observeSettings()
        loadAlarms()
        syncWithCurrentSchedule()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observePrayerSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        masterNotificationsEnabled = settings.notificationsEnabled,
                        notificationModes = settings.prayerNotificationModes
                    )
                }
            }
        }
    }

    private fun loadAlarms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            syncPrayerAlarmsUseCase.initDefaultIfEmpty()
            getPrayerAlarmsUseCase().collect { list ->
                _uiState.update {
                    it.copy(
                        alarms = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun syncWithCurrentSchedule() {
        viewModelScope.launch {
            val location = getCurrentLocationUseCase(includeCityName = false)
            getPrayerScheduleUseCase(location.latitude, location.longitude).collect { result ->
                if (result is Resource.Success && result.data != null) {
                    val schedule = result.data
                    _uiState.update {
                        it.copy(
                            sunriseTime = schedule.sunrise.timeFormatted,
                            sunsetTime = schedule.maghrib.timeFormatted
                        )
                    }
                    syncPrayerAlarmsUseCase.syncWithSchedule(schedule)
                }
            }
        }
    }

    fun toggleMasterNotifications(enabled: Boolean) {
        viewModelScope.launch {
            updatePrayerSettingsUseCase.setNotificationsEnabled(enabled)
            val currentAlarms = _uiState.value.alarms
            for (alarm in currentAlarms) {
                if (enabled && alarm.isAlarmActive) {
                    schedulePrayerAlarmUseCase.schedule(alarm)
                } else {
                    schedulePrayerAlarmUseCase.cancel(alarm.id)
                }
            }
        }
    }

    fun setAllAlarmsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !_uiState.value.masterNotificationsEnabled) {
                updatePrayerSettingsUseCase.setNotificationsEnabled(true)
            }
            val currentAlarms = _uiState.value.alarms
            for (alarm in currentAlarms) {
                val updated = alarm.copy(isAlarmActive = enabled)
                togglePrayerAlarmUseCase(updated, enabled)
                if (enabled) {
                    schedulePrayerAlarmUseCase.schedule(updated)
                } else {
                    schedulePrayerAlarmUseCase.cancel(alarm.id)
                }
            }
        }
    }

    fun toggleAlarm(alarmItem: PrayerAlarmItem) {
        viewModelScope.launch {
            val newState = !alarmItem.isAlarmActive
            if (newState && !_uiState.value.masterNotificationsEnabled) {
                updatePrayerSettingsUseCase.setNotificationsEnabled(true)
            }
            val updated = alarmItem.copy(isAlarmActive = newState)
            togglePrayerAlarmUseCase(updated, newState)

            if (newState) {
                schedulePrayerAlarmUseCase.schedule(updated)
            } else {
                schedulePrayerAlarmUseCase.cancel(alarmItem.id)
            }
        }
    }

    fun setPrayerNotificationMode(prayerType: PrayerType, mode: PrayerNotificationMode) {
        updatePrayerSettingsUseCase.setPrayerNotificationMode(prayerType, mode)
    }

    fun togglePreviewAdhan(prayerType: PrayerType = PrayerType.DHUHR) {
        if (_uiState.value.isPlayingPreview && _uiState.value.previewPrayerType == prayerType) {
            stopPreviewAdhan()
            return
        }
        stopPreviewAdhan()
        val audioRes = if (prayerType == PrayerType.FAJR) R.raw.adzan_fajr else R.raw.adzan_makkah
        _uiState.update { it.copy(isPlayingPreview = true, previewPrayerType = prayerType) }
        playAudioPreviewUseCase.play(
            audioResId = audioRes,
            onCompleted = {
                _uiState.update { it.copy(isPlayingPreview = false, previewPrayerType = null) }
            },
            onError = {
                _uiState.update { it.copy(isPlayingPreview = false, previewPrayerType = null) }
            }
        )
        previewStopJob = viewModelScope.launch {
            delay(12_000L)
            stopPreviewAdhan()
        }
    }

    fun stopPreviewAdhan() {
        previewStopJob?.cancel()
        previewStopJob = null
        playAudioPreviewUseCase.stop()
        _uiState.update { it.copy(isPlayingPreview = false, previewPrayerType = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopPreviewAdhan()
    }
}
