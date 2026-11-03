package com.ne.muslimprayers.qiblafinder.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
import com.ne.muslimprayers.qiblafinder.domain.usecase.GetPrayerScheduleUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.dua.GetDuasUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.hadith.GetHadithCollectionUseCase
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
import com.ne.muslimprayers.qiblafinder.domain.usecase.salah.UpdatePrayerStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPrayerScheduleUseCase: GetPrayerScheduleUseCase,
    private val getHadithCollectionUseCase: GetHadithCollectionUseCase,
    private val getDuasUseCase: GetDuasUseCase,
    private val prayerAlarmRepository: PrayerAlarmRepository,
    private val salahTrackerRepository: SalahTrackerRepository,
    private val updatePrayerStatusUseCase: UpdatePrayerStatusUseCase,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val todayDate: String
        get() = com.ne.muslimprayers.qiblafinder.core.utils.DateTimeUtils.formatDayKey()

    init {
        loadDailyContent()
        observeTodaySalahRecord()
        refreshLocationAndSchedule()
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
        viewModelScope.launch {
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
                                schedule = schedule,
                                hijriDate = schedule?.dateHijri.orEmpty(),
                                gregorianDate = schedule?.dateGregorian.orEmpty(),
                                nextPrayerName = schedule?.nextPrayer?.type?.displayName ?: "Next Prayer",
                                nextPrayer = schedule?.nextPrayer,
                                currentPrayer = schedule?.currentPrayer
                            )
                        }
                        if (schedule != null) {
                            prayerAlarmRepository.syncPrayerAlarmsWithSchedule(schedule)
                            startCountdownTimer(schedule)
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

    private fun startCountdownTimer(schedule: PrayerDaySchedule) {
        timerJob?.cancel()
        var remainingMillis = schedule.remainingMillisToNextPrayer

        timerJob = viewModelScope.launch {
            while (remainingMillis > 0) {
                val formatted = com.ne.muslimprayers.qiblafinder.core.utils.DateTimeUtils.formatCountdown(remainingMillis)
                _uiState.update { it.copy(countdownText = formatted) }

                delay(1000)
                remainingMillis -= 1000
            }

            // Once countdown ends, refresh schedule for next prayer
            _uiState.update { it.copy(countdownText = "Time to Pray!") }
            delay(5000)
            refreshLocationAndSchedule()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
