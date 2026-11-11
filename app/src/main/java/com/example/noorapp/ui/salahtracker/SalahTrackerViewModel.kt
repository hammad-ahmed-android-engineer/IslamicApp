package com.example.noorapp.ui.salahtracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.R
import com.example.noorapp.core.utils.DateTimeUtils
import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.usecase.salah.GetWeeklySalahUseCase
import com.example.noorapp.domain.usecase.salah.SetAllPrayersStatusUseCase
import com.example.noorapp.domain.usecase.salah.UpdatePrayerStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
class SalahTrackerViewModel @Inject constructor(
    private val getWeeklySalahUseCase: GetWeeklySalahUseCase,
    private val updatePrayerStatusUseCase: UpdatePrayerStatusUseCase,
    private val setAllPrayersStatusUseCase: SetAllPrayersStatusUseCase
) : ViewModel() {

    private val dayNameFormat = SimpleDateFormat("EEE", Locale.US)
    private val shortDateFormat = SimpleDateFormat("d MMM", Locale.US)
    private val fullDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US)

    private val _uiState = MutableStateFlow(SalahTrackerUiState())
    val uiState: StateFlow<SalahTrackerUiState> = _uiState.asStateFlow()

    private var weeklyRecordsJob: Job? = null
    private var cachedWeeklyRecords: List<DailySalahRecord> = emptyList()

    init {
        val todayStr = DateTimeUtils.formatDayKey()
        _uiState.update {
            it.copy(
                selectedDate = todayStr,
                weekOffset = 0
            )
        }
        loadWeek(weekOffset = 0, targetSelectedDate = todayStr)
    }

    fun selectDate(dateString: String) {
        _uiState.update { it.copy(selectedDate = dateString) }
        refreshCurrentSelection()
    }

    fun changeWeek(delta: Int) {
        val newOffset = _uiState.value.weekOffset + delta
        loadWeek(weekOffset = newOffset, targetSelectedDate = null)
    }

    fun jumpToToday() {
        val todayStr = DateTimeUtils.formatDayKey()
        loadWeek(weekOffset = 0, targetSelectedDate = todayStr)
    }

    fun togglePrayer(prayerType: PrayerType) {
        val currentDate = _uiState.value.selectedDate
        val currentRecord = _uiState.value.currentDayRecord
        val currentOffered = currentRecord.isPrayerOffered(prayerType)
        val newOffered = !currentOffered

        viewModelScope.launch {
            updatePrayerStatusUseCase(currentDate, prayerType, newOffered)
        }
    }

    fun markAllPrayers(offered: Boolean) {
        val currentDate = _uiState.value.selectedDate
        viewModelScope.launch {
            setAllPrayersStatusUseCase(currentDate, offered)
        }
    }

    private fun loadWeek(weekOffset: Int, targetSelectedDate: String?) {
        val (mondayCal, sundayCal, daysList) = calculateWeekDays(weekOffset)
        val startDateStr = DateTimeUtils.formatDayKey(mondayCal.time)
        val endDateStr = DateTimeUtils.formatDayKey(sundayCal.time)

        val weekRangeStr = "${shortDateFormat.format(mondayCal.time)} – ${shortDateFormat.format(sundayCal.time)}, ${mondayCal.get(Calendar.YEAR)}"

        val todayStr = DateTimeUtils.formatDayKey()
        val newSelectedDate = targetSelectedDate ?: run {
            // If the week contains today, default to today; otherwise, default to monday of that week
            if (daysList.any { it.first == todayStr }) todayStr else daysList.first().first
        }

        _uiState.update {
            it.copy(
                weekOffset = weekOffset,
                selectedDate = newSelectedDate,
                weekRangeFormatted = weekRangeStr
            )
        }

        weeklyRecordsJob?.cancel()
        weeklyRecordsJob = viewModelScope.launch {
            getWeeklySalahUseCase(startDateStr, endDateStr).collect { records ->
                cachedWeeklyRecords = records
                recomputeState(daysList)
            }
        }
    }

    private fun refreshCurrentSelection() {
        val (_, _, daysList) = calculateWeekDays(_uiState.value.weekOffset)
        recomputeState(daysList)
    }

    private fun recomputeState(daysList: List<Pair<String, Calendar>>) {
        val recordsMap = cachedWeeklyRecords.associateBy { it.date }
        val selectedDate = _uiState.value.selectedDate
        val todayStr = DateTimeUtils.formatDayKey()

        // Build 7 Day UI models
        val dayUiModels = daysList.map { (dateStr, cal) ->
            val record = recordsMap[dateStr] ?: DailySalahRecord(date = dateStr)
            DayTrackerUiModel(
                dateString = dateStr,
                dayName = dayNameFormat.format(cal.time),
                dayNumber = cal.get(Calendar.DAY_OF_MONTH),
                isToday = dateStr == todayStr,
                isSelected = dateStr == selectedDate,
                completedCount = record.completedCount,
                totalCount = 5
            )
        }

        val currentRecord = recordsMap[selectedDate] ?: DailySalahRecord(date = selectedDate)

        // Selected Date Formatted
        val parsedDate = DateTimeUtils.parseDayKey(selectedDate) ?: Date()
        val fullFormatted = fullDateFormat.format(parsedDate)

        // Prayers Checklist
        val prayerItems = listOf(
            SalahPrayerItemUi(
                type = PrayerType.FAJR,
                englishNameRes = R.string.prayer_fajr,
                arabicNameRes = R.string.prayer_fajr_arabic,
                descriptionRes = R.string.tracker_fajr_desc,
                isOffered = currentRecord.fajr
            ),
            SalahPrayerItemUi(
                type = PrayerType.DHUHR,
                englishNameRes = R.string.prayer_dhuhr,
                arabicNameRes = R.string.prayer_dhuhr_arabic,
                descriptionRes = R.string.tracker_dhuhr_desc,
                isOffered = currentRecord.dhuhr
            ),
            SalahPrayerItemUi(
                type = PrayerType.ASR,
                englishNameRes = R.string.prayer_asr,
                arabicNameRes = R.string.prayer_asr_arabic,
                descriptionRes = R.string.tracker_asr_desc,
                isOffered = currentRecord.asr
            ),
            SalahPrayerItemUi(
                type = PrayerType.MAGHRIB,
                englishNameRes = R.string.prayer_maghrib,
                arabicNameRes = R.string.prayer_maghrib_arabic,
                descriptionRes = R.string.tracker_maghrib_desc,
                isOffered = currentRecord.maghrib
            ),
            SalahPrayerItemUi(
                type = PrayerType.ISHA,
                englishNameRes = R.string.prayer_isha,
                arabicNameRes = R.string.prayer_isha_arabic,
                descriptionRes = R.string.tracker_isha_desc,
                isOffered = currentRecord.isha
            )
        )

        // Weekly Statistics
        var totalCompletedInWeek = 0
        var fajrCount = 0
        var dhuhrCount = 0
        var asrCount = 0
        var maghribCount = 0
        var ishaCount = 0

        daysList.forEach { (dateStr, _) ->
            val rec = recordsMap[dateStr] ?: DailySalahRecord(date = dateStr)
            totalCompletedInWeek += rec.completedCount
            if (rec.fajr) fajrCount++
            if (rec.dhuhr) dhuhrCount++
            if (rec.asr) asrCount++
            if (rec.maghrib) maghribCount++
            if (rec.isha) ishaCount++
        }

        val percentage = (totalCompletedInWeek.toFloat() / 35f).coerceIn(0f, 1f)
        val breakdown = mapOf(
            PrayerType.FAJR to Pair(fajrCount, 7),
            PrayerType.DHUHR to Pair(dhuhrCount, 7),
            PrayerType.ASR to Pair(asrCount, 7),
            PrayerType.MAGHRIB to Pair(maghribCount, 7),
            PrayerType.ISHA to Pair(ishaCount, 7)
        )

        _uiState.update {
            it.copy(
                selectedDateFormatted = fullFormatted,
                weekDays = dayUiModels,
                currentDayRecord = currentRecord,
                prayersList = prayerItems,
                weeklyCompletedCount = totalCompletedInWeek,
                weeklyPercentage = percentage,
                prayerWeeklyBreakdown = breakdown,
                isToday = selectedDate == todayStr
            )
        }
    }

    private fun calculateWeekDays(weekOffset: Int): Triple<Calendar, Calendar, List<Pair<String, Calendar>>> {
        val now = Calendar.getInstance()
        // Offset weeks
        now.add(Calendar.WEEK_OF_YEAR, weekOffset)

        // Find Monday
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val diffToMonday = when (dayOfWeek) {
            Calendar.SUNDAY -> -6
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> -1
            Calendar.WEDNESDAY -> -2
            Calendar.THURSDAY -> -3
            Calendar.FRIDAY -> -4
            Calendar.SATURDAY -> -5
            else -> 0
        }

        val monday = (now.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, diffToMonday)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val days = mutableListOf<Pair<String, Calendar>>()
        for (i in 0 until 7) {
            val dayCal = (monday.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, i)
            }
            days.add(Pair(DateTimeUtils.formatDayKey(dayCal.time), dayCal))
        }

        val sunday = (monday.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 6)
        }

        return Triple(monday, sunday, days)
    }
}
