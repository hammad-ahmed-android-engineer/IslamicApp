package com.ne.muslimprayers.qiblafinder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.PrayerSettingsManager
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.core.utils.DateTimeUtils
import com.ne.muslimprayers.qiblafinder.data.repository.PrayerScheduleRepositoryImpl
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
import com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerAlarmsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerScheduleUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.TogglePrayerAlarmUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.dua.GetDuasUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.hadith.GetHadithCollectionUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.salah.UpdatePrayerStatusUseCase
import com.ne.muslimprayers.qiblafinder.service.AlarmScheduler
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
    private val alarmScheduler: AlarmScheduler,
    private val getHadithCollectionUseCase: GetHadithCollectionUseCase,
    private val getDuasUseCase: GetDuasUseCase,
    private val prayerAlarmRepository: PrayerAlarmRepository,
    private val salahTrackerRepository: SalahTrackerRepository,
    private val updatePrayerStatusUseCase: UpdatePrayerStatusUseCase,
    private val locationHelper: LocationHelper,
    private val prayerSettingsManager: PrayerSettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            notificationsEnabled = prayerSettingsManager.getCurrentSettings().notificationsEnabled
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var scheduleFetchJob: Job? = null
    private var lastLoadedDayKey: String = DateTimeUtils.formatDayKey()
    private var lastAppliedSettingsTriple: Triple<Int, Int, Int> = run {
        val s = prayerSettingsManager.getCurrentSettings()
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
            prayerAlarmRepository.initDefaultAlarmsIfEmpty()
            getPrayerAlarmsUseCase().collect { alarms ->
                _uiState.update { it.copy(alarms = alarms) }
            }
        }
    }

    private fun observePrayerSettingsChanges() {
        viewModelScope.launch {
            prayerSettingsManager.settingsState.collect { settings ->
                _uiState.update { it.copy(notificationsEnabled = settings.notificationsEnabled) }
            }
        }
        viewModelScope.launch {
            prayerSettingsManager.settingsState
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
                prayerSettingsManager.setNotificationsEnabled(true)
            }

            val updatedItem = PrayerAlarmItem(
                id = alarmId,
                prayerType = prayer.type,
                prayerTime = prayer.time24,
                isAlarmActive = nextActive
            )
            togglePrayerAlarmUseCase(updatedItem, nextActive)
            if (nextActive) {
                alarmScheduler.scheduleAlarm(updatedItem)
            } else {
                alarmScheduler.cancelAlarm(alarmId)
            }
        }
    }

    private fun observeTodaySalahRecord() {
        viewModelScope.launch {
            salahTrackerRepository.getRecordForDate(todayDate).collect { record ->
                _uiState.update { it.copy(todaySalahRecord = record) }
            }
        }
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
        val s = prayerSettingsManager.getCurrentSettings()
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
            updateCountdownFromWallClock(currentSchedule)
            startCountdownTimer(currentSchedule)
        } else {
            refreshLocationAndSchedule()
        }
    }

    fun refreshLocationAndSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val location = locationHelper.getCurrentLocation() ?: locationHelper.getEffectiveLocation()
            val latitude = location.latitude
            val longitude = location.longitude
            val locationName = locationHelper.getCityAndCountry(latitude, longitude)

            _uiState.update {
                it.copy(
                    userLatitude = latitude,
                    userLongitude = longitude,
                    locationName = locationName,
                    errorMessage = null
                )
            }

            fetchPrayerSchedule(latitude, longitude)
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
                            prayerAlarmRepository.syncPrayerAlarmsWithSchedule(schedule)
                            startCountdownTimer(schedule)
                        } else {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Failed to load timings"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateCountdownFromWallClock(baseSchedule: PrayerDaySchedule): PrayerDaySchedule {
        val nowMillis = System.currentTimeMillis()
        val liveSchedule = PrayerScheduleRepositoryImpl.recalculateScheduleForNow(baseSchedule, nowMillis)
        val remainingMillis = (liveSchedule.nextPrayerTargetEpochMillis - nowMillis).coerceAtLeast(0L)
        val formattedCountdown = DateTimeUtils.formatCountdown(remainingMillis)

        _uiState.update {
            it.copy(
                isLoading = false,
                schedule = liveSchedule,
                hijriDate = liveSchedule.dateHijri,
                gregorianDate = liveSchedule.dateGregorian,
                nextPrayerName = liveSchedule.nextPrayer?.type?.displayName ?: "Next Prayer",
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
    }
}
