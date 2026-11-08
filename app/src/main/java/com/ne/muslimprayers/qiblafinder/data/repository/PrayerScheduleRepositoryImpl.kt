package com.ne.muslimprayers.qiblafinder.data.repository

import com.ne.muslimprayers.qiblafinder.core.common.PrayerSettingsManager
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.core.utils.prayer.HijriCalendarCalculator
import com.ne.muslimprayers.qiblafinder.core.utils.prayer.PrayerTimeCalculator
import com.ne.muslimprayers.qiblafinder.data.local.PrayerLocalCache
import com.ne.muslimprayers.qiblafinder.data.local.dao.PrayerAlarmDao
import com.ne.muslimprayers.qiblafinder.data.remote.api.PrayerApiService
import com.ne.muslimprayers.qiblafinder.data.remote.dto.ScheduleResponse
import com.ne.muslimprayers.qiblafinder.domain.model.IslamicCalendarInfo
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [PrayerScheduleRepository] providing 100% offline-ready prayer schedules
 * using the user's selected (or auto-detected regional) Calculation Method and Asr Juristic school.
 */
@Singleton
class PrayerScheduleRepositoryImpl @Inject constructor(
    private val apiService: PrayerApiService,
    private val prayerAlarmDao: PrayerAlarmDao,
    private val prayerLocalCache: PrayerLocalCache,
    private val prayerSettingsManager: PrayerSettingsManager
) : PrayerScheduleRepository {

    override fun getPrayerSchedule(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int,
        day: Int
    ): Flow<Resource<PrayerDaySchedule>> = flow {
        emit(Resource.Loading(true))

        val effectiveLat = if (latitude == 0.0 && longitude == 0.0) 21.4225 else latitude
        val effectiveLng = if (latitude == 0.0 && longitude == 0.0) 39.8262 else longitude

        // Auto-detect regional method & Asr school if user has not manually locked in a selection
        prayerSettingsManager.autoDetectIfNotSelected(effectiveLat, effectiveLng)
        val settings = prayerSettingsManager.getCurrentSettings()
        val apiMethodId = settings.method.apiMethodId
        val asrSchool = settings.asrJuristic
        val hijriOffset = settings.hijriOffset

        val alarms = prayerAlarmDao.getAllAlarms().firstOrNull().orEmpty()
        val alarmMap = if (alarms.isNotEmpty()) {
            alarms.associate { it.prayerName.uppercase() to it.isActive }
        } else {
            settings.prayerAlarmsEnabled.mapKeys { it.key.name.uppercase() }
        }

        // 1. Check offline cache first for instant emission if available
        val cachedList = prayerLocalCache.getCalendar(
            lat = effectiveLat,
            lng = effectiveLng,
            year = year,
            month = month,
            methodId = apiMethodId,
            school = asrSchool,
            hijriOffset = hijriOffset
        )
        if (!cachedList.isNullOrEmpty()) {
            val dayData = cachedList.find {
                it.dateResponse?.gregorian?.day?.toIntOrNull() == day
            } ?: cachedList.firstOrNull()

            if (dayData != null) {
                val schedule = buildScheduleFromApiData(
                    dayData = dayData,
                    alarmMap = alarmMap,
                    year = year,
                    month = month,
                    day = day,
                    hijriOffset = hijriOffset
                )
                emit(Resource.Success(schedule))
            }
        } else {
            // Emit instant local astronomical calculation immediately while network fetches
            try {
                val localSchedule = buildScheduleFromCalculation(
                    lat = effectiveLat,
                    lng = effectiveLng,
                    year = year,
                    month = month,
                    day = day,
                    alarmMap = alarmMap,
                    settingsManager = prayerSettingsManager
                )
                emit(Resource.Success(localSchedule))
            } catch (_: Exception) {
                // Ignore and continue to network
            }
        }

        // 2. Try remote API with exact calculation method, school, and adjustment
        try {
            val response = apiService.getCalendar(
                latitude = effectiveLat,
                longitude = effectiveLng,
                month = month,
                year = year,
                method = apiMethodId,
                school = asrSchool,
                adjustment = hijriOffset
            )
            val scheduleList = response.data.orEmpty()
            if (scheduleList.isNotEmpty()) {
                prayerLocalCache.saveCalendar(
                    lat = effectiveLat,
                    lng = effectiveLng,
                    year = year,
                    month = month,
                    data = scheduleList,
                    methodId = apiMethodId,
                    school = asrSchool,
                    hijriOffset = hijriOffset
                )
                val dayData = scheduleList.find {
                    it.dateResponse?.gregorian?.day?.toIntOrNull() == day
                } ?: scheduleList.firstOrNull()

                if (dayData != null) {
                    val schedule = buildScheduleFromApiData(
                        dayData = dayData,
                        alarmMap = alarmMap,
                        year = year,
                        month = month,
                        day = day,
                        hijriOffset = hijriOffset
                    )
                    emit(Resource.Success(schedule))
                    return@flow
                }
            }
        } catch (_: Exception) {
            // Network unavailable - already emitted cached or local astronomical calculation
        }
    }

    override fun getIslamicCalendar(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int
    ): Flow<Resource<List<IslamicCalendarInfo>>> = flow {
        emit(Resource.Loading(true))

        val effectiveLat = if (latitude == 0.0 && longitude == 0.0) 21.4225 else latitude
        val effectiveLng = if (latitude == 0.0 && longitude == 0.0) 39.8262 else longitude

        prayerSettingsManager.autoDetectIfNotSelected(effectiveLat, effectiveLng)
        val settings = prayerSettingsManager.getCurrentSettings()
        val apiMethodId = settings.method.apiMethodId
        val asrSchool = settings.asrJuristic
        val hijriOffset = settings.hijriOffset

        // 1. Try remote API first and update cache
        try {
            val response = apiService.getCalendar(
                latitude = effectiveLat,
                longitude = effectiveLng,
                month = month,
                year = year,
                method = apiMethodId,
                school = asrSchool,
                adjustment = hijriOffset
            )
            val scheduleList = response.data.orEmpty()
            if (scheduleList.isNotEmpty()) {
                prayerLocalCache.saveCalendar(
                    lat = effectiveLat,
                    lng = effectiveLng,
                    year = year,
                    month = month,
                    data = scheduleList,
                    methodId = apiMethodId,
                    school = asrSchool,
                    hijriOffset = hijriOffset
                )
                val calendarList = mapScheduleListToCalendarInfo(scheduleList, month, year, hijriOffset)
                emit(Resource.Success(calendarList))
                return@flow
            }
        } catch (_: Exception) {
            // Network failure - fall through to offline cache & calculation
        }

        // 2. Try offline cache
        val cachedList = prayerLocalCache.getCalendar(
            lat = effectiveLat,
            lng = effectiveLng,
            year = year,
            month = month,
            methodId = apiMethodId,
            school = asrSchool,
            hijriOffset = hijriOffset
        )
        if (!cachedList.isNullOrEmpty()) {
            val calendarList = mapScheduleListToCalendarInfo(cachedList, month, year, hijriOffset)
            emit(Resource.Success(calendarList))
            return@flow
        }

        // 3. Complete Offline Monthly Generation via Astronomical Formulas
        try {
            val calendarList = generateMonthlyCalendarOffline(
                lat = effectiveLat,
                lng = effectiveLng,
                year = year,
                month = month,
                settingsManager = prayerSettingsManager
            )
            emit(Resource.Success(calendarList))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to generate Islamic calendar"))
        }
    }

    private fun buildScheduleFromApiData(
        dayData: ScheduleResponse,
        alarmMap: Map<String, Boolean>,
        year: Int = Calendar.getInstance().get(Calendar.YEAR),
        month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
        day: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
        hijriOffset: Int = 0
    ): PrayerDaySchedule {
        val timings = dayData.timingResponse
        val cleanTime = { raw: String? -> raw?.substringBefore(" ")?.trim() ?: "00:00" }

        val fajrTime = cleanTime(timings?.fajr)
        val sunriseTime = cleanTime(timings?.sunrise)
        val dhuhrTime = cleanTime(timings?.dhuhr)
        val asrTime = cleanTime(timings?.asr)
        val maghribTime = cleanTime(timings?.maghrib)
        val ishaTime = cleanTime(timings?.isha)

        val prayers = listOf(
            SinglePrayer(PrayerType.FAJR, fajrTime, formatTo12Hour(fajrTime), isAlarmActive = alarmMap["FAJR"] ?: false),
            SinglePrayer(PrayerType.SUNRISE, sunriseTime, formatTo12Hour(sunriseTime)),
            SinglePrayer(PrayerType.DHUHR, dhuhrTime, formatTo12Hour(dhuhrTime), isAlarmActive = alarmMap["DHUHR"] ?: false),
            SinglePrayer(PrayerType.ASR, asrTime, formatTo12Hour(asrTime), isAlarmActive = alarmMap["ASR"] ?: false),
            SinglePrayer(PrayerType.MAGHRIB, maghribTime, formatTo12Hour(maghribTime), isAlarmActive = alarmMap["MAGHRIB"] ?: false),
            SinglePrayer(PrayerType.ISHA, ishaTime, formatTo12Hour(ishaTime), isAlarmActive = alarmMap["ISHA"] ?: false)
        )

        val timingState = calculateNextAndCurrentPrayer(prayers)
        val updatedPrayers = prayers.map { it.copy(isNext = it.type == timingState.nextPrayer.type) }

        val hijri = dayData.dateResponse?.hijri
        val gregorian = dayData.dateResponse?.gregorian

        val calculatedHijri = HijriCalendarCalculator.calculate(
            year = year,
            month = month,
            day = day,
            hijriOffsetDays = hijriOffset
        )

        // When user applies a Hijri adjustment (or if API hijri is missing), use the offset-adjusted calculation
        val hijriFormatted = if (hijriOffset != 0 || hijri == null) {
            calculatedHijri.formatted
        } else {
            "${hijri.day} ${hijri.month?.en ?: ""} ${hijri.year} AH"
        }

        val gregorianFormatted = if (gregorian != null) {
            "${gregorian.weekday?.en ?: ""}, ${gregorian.day} ${gregorian.month?.en ?: ""} ${gregorian.year}"
        } else ""

        return PrayerDaySchedule(
            dateGregorian = gregorianFormatted,
            dateHijri = hijriFormatted,
            fajr = updatedPrayers[0],
            sunrise = updatedPrayers[1],
            dhuhr = updatedPrayers[2],
            asr = updatedPrayers[3],
            maghrib = updatedPrayers[4],
            isha = updatedPrayers[5],
            nextPrayer = timingState.nextPrayer.copy(isNext = true),
            currentPrayer = timingState.currentPrayer,
            remainingMillisToNextPrayer = timingState.remainingMillis,
            nextPrayerTargetEpochMillis = timingState.targetEpochMillis
        )
    }

    private fun buildScheduleFromCalculation(
        lat: Double,
        lng: Double,
        year: Int,
        month: Int,
        day: Int,
        alarmMap: Map<String, Boolean>,
        settingsManager: PrayerSettingsManager
    ): PrayerDaySchedule {
        val settings = settingsManager.getCurrentSettings()
        val times = PrayerTimeCalculator.calculate(
            latitude = lat,
            longitude = lng,
            year = year,
            month = month,
            day = day,
            method = settings.method,
            asrJuristic = settings.asrJuristic
        )
        val hijri = HijriCalendarCalculator.calculate(
            year = year,
            month = month,
            day = day,
            hijriOffsetDays = settings.hijriOffset
        )

        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }
        val gregorianFormatted = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US).format(cal.time)

        val prayers = listOf(
            SinglePrayer(PrayerType.FAJR, times.fajr, formatTo12Hour(times.fajr), isAlarmActive = alarmMap["FAJR"] ?: false),
            SinglePrayer(PrayerType.SUNRISE, times.sunrise, formatTo12Hour(times.sunrise)),
            SinglePrayer(PrayerType.DHUHR, times.dhuhr, formatTo12Hour(times.dhuhr), isAlarmActive = alarmMap["DHUHR"] ?: false),
            SinglePrayer(PrayerType.ASR, times.asr, formatTo12Hour(times.asr), isAlarmActive = alarmMap["ASR"] ?: false),
            SinglePrayer(PrayerType.MAGHRIB, times.maghrib, formatTo12Hour(times.maghrib), isAlarmActive = alarmMap["MAGHRIB"] ?: false),
            SinglePrayer(PrayerType.ISHA, times.isha, formatTo12Hour(times.isha), isAlarmActive = alarmMap["ISHA"] ?: false)
        )

        val timingState = calculateNextAndCurrentPrayer(prayers)
        val updatedPrayers = prayers.map { it.copy(isNext = it.type == timingState.nextPrayer.type) }

        return PrayerDaySchedule(
            dateGregorian = gregorianFormatted,
            dateHijri = hijri.formatted,
            fajr = updatedPrayers[0],
            sunrise = updatedPrayers[1],
            dhuhr = updatedPrayers[2],
            asr = updatedPrayers[3],
            maghrib = updatedPrayers[4],
            isha = updatedPrayers[5],
            nextPrayer = timingState.nextPrayer.copy(isNext = true),
            currentPrayer = timingState.currentPrayer,
            remainingMillisToNextPrayer = timingState.remainingMillis,
            nextPrayerTargetEpochMillis = timingState.targetEpochMillis
        )
    }

    private fun mapScheduleListToCalendarInfo(
        scheduleList: List<ScheduleResponse>,
        month: Int,
        year: Int,
        hijriOffset: Int = 0
    ): List<IslamicCalendarInfo> {
        return scheduleList.mapNotNull { item ->
            val g = item.dateResponse?.gregorian
            val h = item.dateResponse?.hijri
            val gDay = g?.day?.toIntOrNull() ?: return@mapNotNull null
            val gMonth = g.month?.number ?: month
            val gYear = g.year?.toIntOrNull() ?: year
            val gFormatted = "${g.weekday?.en ?: ""}, $gDay ${g.month?.en ?: ""} $gYear"

            val calcHijri = HijriCalendarCalculator.calculate(
                year = gYear,
                month = gMonth,
                day = gDay,
                hijriOffsetDays = hijriOffset
            )

            val hDay = if (hijriOffset != 0 || h == null) calcHijri.day else (h.day?.toIntOrNull() ?: calcHijri.day)
            val hMonthName = if (hijriOffset != 0 || h == null) calcHijri.monthName else (h.month?.en ?: calcHijri.monthName)
            val hYear = if (hijriOffset != 0 || h == null) calcHijri.year else (h.year?.toIntOrNull() ?: calcHijri.year)
            val hFormatted = "$hDay $hMonthName $hYear AH"

            val gWeekday = g.weekday?.en ?: ""
            val holidays = if (hijriOffset != 0 || h == null) {
                calcHijri.holidays
            } else {
                h.holidays?.filterNotNull().orEmpty().ifEmpty { calcHijri.holidays }
            }

            val timings = item.timingResponse
            val cleanTime = { raw: String? -> raw?.substringBefore(" ")?.trim() ?: "00:00" }

            val prayers = listOf(
                SinglePrayer(PrayerType.FAJR, cleanTime(timings?.fajr), formatTo12Hour(cleanTime(timings?.fajr))),
                SinglePrayer(PrayerType.SUNRISE, cleanTime(timings?.sunrise), formatTo12Hour(cleanTime(timings?.sunrise))),
                SinglePrayer(PrayerType.DHUHR, cleanTime(timings?.dhuhr), formatTo12Hour(cleanTime(timings?.dhuhr))),
                SinglePrayer(PrayerType.ASR, cleanTime(timings?.asr), formatTo12Hour(cleanTime(timings?.asr))),
                SinglePrayer(PrayerType.MAGHRIB, cleanTime(timings?.maghrib), formatTo12Hour(cleanTime(timings?.maghrib))),
                SinglePrayer(PrayerType.ISHA, cleanTime(timings?.isha), formatTo12Hour(cleanTime(timings?.isha)))
            )

            IslamicCalendarInfo(
                gregorianDay = gDay,
                gregorianMonth = gMonth,
                gregorianYear = gYear,
                gregorianFormatted = gFormatted,
                hijriDay = hDay,
                hijriMonthName = hMonthName,
                hijriYear = hYear,
                hijriFormatted = hFormatted,
                weekday = gWeekday,
                holidays = holidays,
                prayerTimings = prayers
            )
        }
    }

    private fun generateMonthlyCalendarOffline(
        lat: Double,
        lng: Double,
        year: Int,
        month: Int,
        settingsManager: PrayerSettingsManager
    ): List<IslamicCalendarInfo> {
        val settings = settingsManager.getCurrentSettings()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val sdfWeekday = SimpleDateFormat("EEEE", Locale.US)
        val sdfFormatted = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US)

        val list = mutableListOf<IslamicCalendarInfo>()
        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val times = PrayerTimeCalculator.calculate(
                latitude = lat,
                longitude = lng,
                year = year,
                month = month,
                day = day,
                method = settings.method,
                asrJuristic = settings.asrJuristic
            )
            val hijri = HijriCalendarCalculator.calculate(
                year = year,
                month = month,
                day = day,
                hijriOffsetDays = settings.hijriOffset
            )

            val prayers = listOf(
                SinglePrayer(PrayerType.FAJR, times.fajr, formatTo12Hour(times.fajr)),
                SinglePrayer(PrayerType.SUNRISE, times.sunrise, formatTo12Hour(times.sunrise)),
                SinglePrayer(PrayerType.DHUHR, times.dhuhr, formatTo12Hour(times.dhuhr)),
                SinglePrayer(PrayerType.ASR, times.asr, formatTo12Hour(times.asr)),
                SinglePrayer(PrayerType.MAGHRIB, times.maghrib, formatTo12Hour(times.maghrib)),
                SinglePrayer(PrayerType.ISHA, times.isha, formatTo12Hour(times.isha))
            )

            list.add(
                IslamicCalendarInfo(
                    gregorianDay = day,
                    gregorianMonth = month,
                    gregorianYear = year,
                    gregorianFormatted = sdfFormatted.format(cal.time),
                    hijriDay = hijri.day,
                    hijriMonthName = hijri.monthName,
                    hijriYear = hijri.year,
                    hijriFormatted = hijri.formatted,
                    weekday = sdfWeekday.format(cal.time),
                    holidays = hijri.holidays,
                    prayerTimings = prayers
                )
            )
        }
        return list
    }

    private fun formatTo12Hour(time24: String): String {
        return try {
            val sdf24 = SimpleDateFormat("HH:mm", Locale.US)
            val sdf12 = SimpleDateFormat("hh:mm a", Locale.US)
            val date = sdf24.parse(time24)
            if (date != null) sdf12.format(date) else time24
        } catch (_: Exception) {
            time24
        }
    }

    data class PrayerTimingWindow(
        val nextPrayer: SinglePrayer,
        val currentPrayer: SinglePrayer?,
        val remainingMillis: Long,
        val targetEpochMillis: Long
    )

    companion object {
        /**
         * Computes the active prayer, next prayer, exact wall-clock target epoch millis,
         * and remaining milliseconds using exact wall-clock time (`nowMillis`).
         *
         * Only the 5 obligatory Salawat (Fajr, Dhuhr, Asr, Maghrib, Isha) are targeted as the
         * upcoming Salah countdown; Sunrise marks the end of Fajr time.
         */
        fun calculateNextAndCurrentPrayer(
            prayers: List<SinglePrayer>,
            nowMillis: Long = System.currentTimeMillis()
        ): PrayerTimingWindow {
            val nowCal = Calendar.getInstance().apply { timeInMillis = nowMillis }

            fun epochForTimeToday(timeStr: String, dayOffset: Int = 0): Long {
                val parts = timeStr.trim().split(":")
                val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                return (nowCal.clone() as Calendar).apply {
                    if (dayOffset != 0) {
                        add(Calendar.DAY_OF_YEAR, dayOffset)
                    }
                    set(Calendar.HOUR_OF_DAY, h)
                    set(Calendar.MINUTE, m)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            }

            val obligatoryPrayers = prayers.filter { it.type != PrayerType.SUNRISE }
            val sunrisePrayer = prayers.find { it.type == PrayerType.SUNRISE }
            val sunriseEpoch = sunrisePrayer?.let { epochForTimeToday(it.time24) } ?: Long.MAX_VALUE

            var nextPrayer: SinglePrayer? = null
            var targetEpochMillis = 0L

            for (p in obligatoryPrayers) {
                val prayerEpoch = epochForTimeToday(p.time24, dayOffset = 0)
                if (prayerEpoch > nowMillis) {
                    nextPrayer = p
                    targetEpochMillis = prayerEpoch
                    break
                }
            }

            // If all 5 obligatory prayers for today have passed, next prayer is tomorrow's Fajr
            if (nextPrayer == null) {
                val fajr = obligatoryPrayers.firstOrNull() ?: prayers.first()
                nextPrayer = fajr
                targetEpochMillis = epochForTimeToday(fajr.time24, dayOffset = 1)
            }

            val remainingMillis = (targetEpochMillis - nowMillis).coerceAtLeast(0L)

            // Determine current active obligatory prayer
            val nextIdx = obligatoryPrayers.indexOfFirst { it.type == nextPrayer.type }
            val previousObligatory = if (nextIdx > 0) {
                obligatoryPrayers[nextIdx - 1]
            } else {
                // Before today's Fajr or after today's Isha -> Isha is the current prayer window
                obligatoryPrayers.lastOrNull()
            }

            // Note: Fajr ends at Sunrise; between Sunrise and Dhuhr there is no active obligatory fard prayer
            val currentPrayer = if (previousObligatory?.type == PrayerType.FAJR && nowMillis >= sunriseEpoch) {
                null
            } else {
                previousObligatory
            }

            return PrayerTimingWindow(
                nextPrayer = nextPrayer,
                currentPrayer = currentPrayer,
                remainingMillis = remainingMillis,
                targetEpochMillis = targetEpochMillis
            )
        }

        /**
         * Recalculates `nextPrayer`, `currentPrayer`, `remainingMillisToNextPrayer`, and `nextPrayerTargetEpochMillis`
         * against the real wall-clock time `nowMillis`.
         */
        fun recalculateScheduleForNow(
            schedule: PrayerDaySchedule,
            nowMillis: Long = System.currentTimeMillis()
        ): PrayerDaySchedule {
            val window = calculateNextAndCurrentPrayer(schedule.allPrayers, nowMillis)
            val updated = schedule.allPrayers.map { it.copy(isNext = it.type == window.nextPrayer.type) }
            return schedule.copy(
                fajr = updated[0],
                sunrise = updated[1],
                dhuhr = updated[2],
                asr = updated[3],
                maghrib = updated[4],
                isha = updated[5],
                nextPrayer = window.nextPrayer.copy(isNext = true),
                currentPrayer = window.currentPrayer,
                remainingMillisToNextPrayer = window.remainingMillis,
                nextPrayerTargetEpochMillis = window.targetEpochMillis
            )
        }
    }
}
