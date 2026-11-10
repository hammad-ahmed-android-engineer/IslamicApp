package com.ne.muslimprayers.qiblafinder.ui.alarm

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.PrayerNotificationMode
import com.ne.muslimprayers.qiblafinder.core.common.PrayerSettingsManager
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerAlarmsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerScheduleUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.TogglePrayerAlarmUseCase
import com.ne.muslimprayers.qiblafinder.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PrayerAlarmUiState(
    val alarms: List<PrayerAlarmItem> = emptyList(),
    val notificationModes: Map<PrayerType, PrayerNotificationMode> = emptyMap(),
    val masterNotificationsEnabled: Boolean = true,
    val sunriseTime: String = "--:-- AM",
    val sunsetTime: String = "--:-- PM",
    val isPlayingPreview: Boolean = false,
    val previewPrayerType: PrayerType? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class PrayerAlarmViewModel @Inject constructor(
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val togglePrayerAlarmUseCase: TogglePrayerAlarmUseCase,
    private val getPrayerScheduleUseCase: GetPrayerScheduleUseCase,
    private val prayerAlarmRepository: PrayerAlarmRepository,
    private val prayerSettingsManager: PrayerSettingsManager,
    private val locationHelper: LocationHelper,
    private val alarmScheduler: AlarmScheduler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val initialSettings = prayerSettingsManager.getCurrentSettings()

    private val _uiState = MutableStateFlow(
        PrayerAlarmUiState(
            masterNotificationsEnabled = initialSettings.notificationsEnabled,
            notificationModes = initialSettings.prayerNotificationModes
        )
    )
    val uiState: StateFlow<PrayerAlarmUiState> = _uiState.asStateFlow()

    private var previewPlayer: MediaPlayer? = null
    private var previewStopJob: Job? = null

    init {
        observeSettings()
        loadAlarms()
        syncWithCurrentSchedule()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            prayerSettingsManager.settingsState.collect { settings ->
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
            prayerAlarmRepository.initDefaultAlarmsIfEmpty()
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
            val location = locationHelper.getCurrentLocation() ?: locationHelper.getEffectiveLocation()
            getPrayerScheduleUseCase(location.latitude, location.longitude).collect { result ->
                if (result is Resource.Success && result.data != null) {
                    val schedule = result.data
                    _uiState.update {
                        it.copy(
                            sunriseTime = schedule.sunrise.timeFormatted,
                            sunsetTime = schedule.maghrib.timeFormatted
                        )
                    }
                    prayerAlarmRepository.syncPrayerAlarmsWithSchedule(schedule)
                }
            }
        }
    }

    fun toggleMasterNotifications(enabled: Boolean) {
        viewModelScope.launch {
            prayerSettingsManager.setNotificationsEnabled(enabled)
            val currentAlarms = _uiState.value.alarms
            for (alarm in currentAlarms) {
                if (enabled && alarm.isAlarmActive) {
                    alarmScheduler.scheduleAlarm(alarm)
                } else {
                    alarmScheduler.cancelAlarm(alarm.id)
                }
            }
        }
    }

    fun setAllAlarmsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !_uiState.value.masterNotificationsEnabled) {
                prayerSettingsManager.setNotificationsEnabled(true)
            }
            val currentAlarms = _uiState.value.alarms
            for (alarm in currentAlarms) {
                val updated = alarm.copy(isAlarmActive = enabled)
                togglePrayerAlarmUseCase(updated, enabled)
                if (enabled) {
                    alarmScheduler.scheduleAlarm(updated)
                } else {
                    alarmScheduler.cancelAlarm(alarm.id)
                }
            }
        }
    }

    fun toggleAlarm(alarmItem: PrayerAlarmItem) {
        viewModelScope.launch {
            val newState = !alarmItem.isAlarmActive
            if (newState && !_uiState.value.masterNotificationsEnabled) {
                prayerSettingsManager.setNotificationsEnabled(true)
            }
            val updated = alarmItem.copy(isAlarmActive = newState)
            togglePrayerAlarmUseCase(updated, newState)

            if (newState) {
                alarmScheduler.scheduleAlarm(updated)
            } else {
                alarmScheduler.cancelAlarm(alarmItem.id)
            }
        }
    }

    fun setPrayerNotificationMode(prayerType: PrayerType, mode: PrayerNotificationMode) {
        prayerSettingsManager.setPrayerNotificationMode(prayerType, mode)
    }

    fun togglePreviewAdhan(prayerType: PrayerType = PrayerType.DHUHR) {
        if (_uiState.value.isPlayingPreview && _uiState.value.previewPrayerType == prayerType) {
            stopPreviewAdhan()
            return
        }
        stopPreviewAdhan()
        try {
            val audioRes = if (prayerType == PrayerType.FAJR) R.raw.adzan_fajr else R.raw.adzan_makkah
            previewPlayer = MediaPlayer.create(context, audioRes)?.apply {
                setOnCompletionListener {
                    _uiState.update { it.copy(isPlayingPreview = false, previewPrayerType = null) }
                }
                start()
            }
            _uiState.update { it.copy(isPlayingPreview = true, previewPrayerType = prayerType) }
            previewStopJob = viewModelScope.launch {
                delay(12_000L)
                stopPreviewAdhan()
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(isPlayingPreview = false, previewPrayerType = null) }
        }
    }

    fun stopPreviewAdhan() {
        try {
            previewStopJob?.cancel()
            previewStopJob = null
            previewPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {
        } finally {
            previewPlayer = null
            _uiState.update { it.copy(isPlayingPreview = false, previewPrayerType = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPreviewAdhan()
    }
}
