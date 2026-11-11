package com.example.noorapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.core.common.Resource
import com.example.noorapp.core.utils.DateTimeUtils
import com.example.noorapp.domain.model.Hadith
import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerDaySchedule
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.model.SinglePrayer
import com.example.noorapp.domain.usecase.GetPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.GetPrayerScheduleUseCase
import com.example.noorapp.domain.usecase.SchedulePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.SyncPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.TogglePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.dua.GetDuasUseCase
import com.example.noorapp.domain.usecase.hadith.GetHadithCollectionUseCase
import com.example.noorapp.domain.usecase.location.GetCurrentLocationUseCase
import com.example.noorapp.domain.usecase.salah.GetDailySalahUseCase
import com.example.noorapp.domain.usecase.salah.UpdatePrayerStatusUseCase
import com.example.noorapp.domain.usecase.settings.ObservePrayerSettingsUseCase
import com.example.noorapp.domain.usecase.settings.UpdatePrayerSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPrayerScheduleUseCase: GetPrayerScheduleUseCase,
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val togglePrayerAlarmUseCase: TogglePrayerAlarmUseCase,
    private val syncPrayerAlarmsUseCase: SyncPrayerAlarmsUseCase,
    private val schedulePrayerAlarmUseCase: SchedulePrayerAlarmUseCase,
    private val getHadithCollectionUseCase: GetHadithCollectionUseCase,
    private val getDuasUseCase: GetDuasUseCase,
    private val getDailySalahUseCase: GetDailySalahUseCase,
    private val updatePrayerStatusUseCase: UpdatePrayerStatusUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val observePrayerSettingsUseCase: ObservePrayerSettingsUseCase,
    private val updatePrayerSettingsUseCase: UpdatePrayerSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            notificationsEnabled = observePrayerSettingsUseCase.current().notificationsEnabled
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var scheduleFetchJob: Job? = null
    private var locationRefreshJob: Job? = null
    private var hasAttemptedInitialLoad: Boolean = false
    private var lastLoadedDayKey: String = DateTimeUtils.formatDayKey()
    private var lastAppliedSettingsTriple: Triple<Int, Int, Int> = run {
        val s = observePrayerSettingsUseCase.current()
        Triple(s.methodIndex, s.asrJuristic, s.hijriOffset)
    }

    private val todayDate: String
        get() = DateTimeUtils.formatDayKey()

    init {
        loadDailyContent()
        observeTodaySalahRecord()
        observePrayerAlarms()
        observePrayerSettingsChanges()
        refreshLocationAndSchedule()
    }

    private fun observePrayerAlarms() {
        viewModelScope.launch {
            syncPrayerAlarmsUseCase.initDefaultIfEmpty()
            getPrayerAlarmsUseCase().collect { alarms ->
                _uiState.update { it.copy(alarms = alarms) }
            }
        }
    }

    private fun observePrayerSettingsChanges() {
        viewModelScope.launch {
            observePrayerSettingsUseCase().collect { settings ->
                _uiState.update { it.copy(notificationsEnabled = settings.notificationsEnabled) }
            }
        }
        viewModelScope.launch {
            observePrayerSettingsUseCase()
                .map { Triple(it.methodIndex, it.asrJuristic, it.hijriOffset) }
                .distinctUntilChanged()
                .drop(1)
                .collect { triple ->
                    lastAppliedSettingsTriple = triple
                    val lat = _uiState.value.userLatitude
                    val lng = _uiState.value.userLongitude
                    if (lat != 0.0 || lng != 0.0) {
                        fetchPrayerSchedule(lat, lng)
                    } else {
                        refreshLocationAndSchedule()
                    }
                }
        }
    }

    fun togglePrayerAlarm(prayer: SinglePrayer) {
        viewModelScope.launch {
            val alarmId = when (prayer.type) {
                PrayerType.FAJR -> 0
                PrayerType.DHUHR -> 1
                PrayerType.ASR -> 2
                PrayerType.MAGHRIB -> 3
                PrayerType.ISHA -> 4
                else -> return@launch
            }
            val existing = _uiState.value.alarms.find { it.prayerType == prayer.type }
            val currentActive = existing?.isAlarmActive ?: prayer.isAlarmActive
            val nextActive = !currentActive

            if (nextActive && !_uiState.value.notificationsEnabled) {
                updatePrayerSettingsUseCase.setNotificationsEnabled(true)
            }

            val updatedItem = PrayerAlarmItem(
                id = alarmId,
                prayerType = prayer.type,
                prayerTime = prayer.time24,
                isAlarmActive = nextActive
            )
            togglePrayerAlarmUseCase(updatedItem, nextActive)
            if (nextActive) {
                schedulePrayerAlarmUseCase.schedule(updatedItem)
            } else {
                schedulePrayerAlarmUseCase.cancel(alarmId)
            }
        }
    }

    private fun observeTodaySalahRecord() {
        viewModelScope.launch {
            getDailySalahUseCase(todayDate).collect { record ->
                _uiState.update { it.copy(todaySalahRecord = record) }
            }
        }
    }

    fun selectHadith(hadith: Hadith?) {
        _uiState.update { it.copy(selectedHadith = hadith) }
    }

    fun togglePrayerOffered(prayerType: PrayerType, offered: Boolean) {
        viewModelScope.launch {
            updatePrayerStatusUseCase(todayDate, prayerType, offered)
        }
    }

    private fun loadDailyContent() {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        viewModelScope.launch {
            getHadithCollectionUseCase().collect { result ->
                if (result is Resource.Success) {
                    val hadiths = result.data.orEmpty()
                    if (hadiths.isNotEmpty()) {
                        val dailyHadith = hadiths[dayOfYear % hadiths.size]
                        _uiState.update { it.copy(dailyHadith = dailyHadith) }
                    }
                }
            }
        }

        viewModelScope.launch {
            getDuasUseCase().collect { result ->
                if (result is Resource.Success) {
                    val duas = result.data.orEmpty()
                    if (duas.isNotEmpty()) {
                        val dailyDua = duas[dayOfYear % duas.size]
                        _uiState.update { it.copy(dailyDua = dailyDua) }
                    }
                }
            }
        }
    }

    /**
     * Called when the screen resumes from background or user reopens the app.
     * Immediately synchronizes the countdown timer with real wall-clock time (`System.currentTimeMillis()`).
     */
    fun onScreenResumed() {
        val currentDayKey = DateTimeUtils.formatDayKey()
        val s = observePrayerSettingsUseCase.current()
        val currentSettingsTriple = Triple(s.methodIndex, s.asrJuristic, s.hijriOffset)

        if (currentDayKey != lastLoadedDayKey || currentSettingsTriple != lastAppliedSettingsTriple) {
            lastLoadedDayKey = currentDayKey
            lastAppliedSettingsTriple = currentSettingsTriple
            val lat = _uiState.value.userLatitude
            val lng = _uiState.value.userLongitude
            if (lat != 0.0 || lng != 0.0) {
                fetchPrayerSchedule(lat, lng)
            } else {
                refreshLocationAndSchedule()
            }
            return
        }

        val currentSchedule = _uiState.value.schedule
        if (currentSchedule != null) {
            if (timerJob?.isActive != true) {
                updateCountdownFromWallClock(currentSchedule)
                startCountdownTimer(currentSchedule)
            }
        } else if (!hasAttemptedInitialLoad && !_uiState.value.isLoading && _uiState.value.errorMessage == null) {
            refreshLocationAndSchedule()
        }
    }

    fun refreshLocationAndSchedule() {
        hasAttemptedInitialLoad = true
        locationRefreshJob?.cancel()
        locationRefreshJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val location = getCurrentLocationUseCase(includeCityName = true)

            _uiState.update {
                it.copy(
                    userLatitude = location.latitude,
                    userLongitude = location.longitude,
                    locationName = location.cityName,
                    errorMessage = null
                )
            }

            fetchPrayerSchedule(location.latitude, location.longitude)
        }
    }

    private fun fetchPrayerSchedule(lat: Double, lng: Double) {
        scheduleFetchJob?.cancel()
        scheduleFetchJob = viewModelScope.launch {
            getPrayerScheduleUseCase(lat, lng).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (_uiState.value.schedule == null) {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                    is Resource.Success -> {
                        val schedule = result.data
                        lastLoadedDayKey = DateTimeUtils.formatDayKey()
                        if (schedule != null) {
                            updateCountdownFromWallClock(schedule)
                            syncPrayerAlarmsUseCase.syncWithSchedule(schedule)
                            startCountdownTimer(schedule)
                        } else {
                            _uiState.update { it.copy(isLoading = false) }
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

    private fun updateCountdownFromWallClock(baseSchedule: PrayerDaySchedule): PrayerDaySchedule {
        val nowMillis = System.currentTimeMillis()
        val liveSchedule = getPrayerScheduleUseCase.recalculateForNow(baseSchedule, nowMillis)
        val remainingMillis = (liveSchedule.nextPrayerTargetEpochMillis - nowMillis).coerceAtLeast(0L)
        val formattedCountdown = DateTimeUtils.formatCountdown(remainingMillis)

        _uiState.update {
            it.copy(
                isLoading = false,
                schedule = liveSchedule,
                hijriDate = liveSchedule.dateHijri,
                gregorianDate = liveSchedule.dateGregorian,
                nextPrayerName = liveSchedule.nextPrayer?.type?.displayName ?: "",
                nextPrayer = liveSchedule.nextPrayer,
                currentPrayer = liveSchedule.currentPrayer,
                countdownText = formattedCountdown
            )
        }
        return liveSchedule
    }

    /**
     * Wall-clock synchronized countdown timer.
     * Always computes remaining time as `targetEpochMillis - System.currentTimeMillis()`,
     * so closing/backgrounding the app and reopening it later immediately shows the exact
     * remaining clock time without drift.
     */
    private fun startCountdownTimer(schedule: PrayerDaySchedule) {
        timerJob?.cancel()
        var activeSchedule = schedule

        timerJob = viewModelScope.launch {
            while (isActive) {
                activeSchedule = updateCountdownFromWallClock(activeSchedule)

                // Check if date rolled over at midnight
                val currentDayKey = DateTimeUtils.formatDayKey()
                if (currentDayKey != lastLoadedDayKey) {
                    lastLoadedDayKey = currentDayKey
                    refreshLocationAndSchedule()
                    break
                }

                // Sleep until the next exact wall-clock second boundary
                val now = System.currentTimeMillis()
                val sleepMillis = (1000L - (now % 1000L)).coerceIn(200L, 1000L)
                delay(sleepMillis)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        scheduleFetchJob?.cancel()
        locationRefreshJob?.cancel()
    }
}
