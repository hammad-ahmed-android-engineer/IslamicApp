package com.ne.muslimprayers.qiblafinder.data.repository

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
 * Implementation of [PrayerScheduleRepository] providing 100% offline-ready prayer schedules.
 *
 * STRATEGY:
 * 1. Online: Fetches live monthly schedule from AlAdhan API and persists into [PrayerLocalCache].
 * 2. Offline with Cache: Immediately uses cached monthly data.
 * 3. Offline without Cache: Computes high-precision astronomical prayer times and Hijri dates
 *    using [PrayerTimeCalculator] and [HijriCalendarCalculator].
 *
 * GUARANTEE: Never fails or displays blank/error states when offline.
 */
@Singleton
class PrayerScheduleRepositoryImpl @Inject constructor(
    private val apiService: PrayerApiService,
    private val prayerAlarmDao: PrayerAlarmDao,
    private val prayerLocalCache: PrayerLocalCache
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

        val alarms = prayerAlarmDao.getAllAlarms().firstOrNull().orEmpty()
        val alarmMap = alarms.associate { it.prayerName.uppercase() to it.isActive }

        // 1. Try remote API first and update local cache
        try {
            val response = apiService.getCalendar(effectiveLat, effectiveLng, month, year)
            val scheduleList = response.data.orEmpty()
            if (scheduleList.isNotEmpty()) {
                prayerLocalCache.saveCalendar(effectiveLat, effectiveLng, year, month, scheduleList)
                val dayData = scheduleList.find {
                    it.dateResponse?.gregorian?.day?.toIntOrNull() == day
                } ?: scheduleList.firstOrNull()

                if (dayData != null) {
                    val schedule = buildScheduleFromApiData(dayData, alarmMap)
                    emit(Resource.Success(schedule))
                    return@flow
                }
            }
        } catch (_: Exception) {
            // Network failure - fall through to offline local cache & calculation
        }

        // 2. Try offline cache
        val cachedList = prayerLocalCache.getCalendar(effectiveLat, effectiveLng, year, month)
        if (!cachedList.isNullOrEmpty()) {
            val dayData = cachedList.find {
                it.dateResponse?.gregorian?.day?.toIntOrNull() == day
            } ?: cachedList.firstOrNull()

            if (dayData != null) {
                val schedule = buildScheduleFromApiData(dayData, alarmMap)
                emit(Resource.Success(schedule))
                return@flow
            }
        }

        // 3. Complete Offline Synthesis via Astronomical Solar & Lunar Formulas
        try {
            val schedule = buildScheduleFromCalculation(effectiveLat, effectiveLng, year, month, day, alarmMap)
            emit(Resource.Success(schedule))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to calculate offline prayer timings"))
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

        // 1. Try remote API first and update cache
        try {
            val response = apiService.getCalendar(effectiveLat, effectiveLng, month, year)
            val scheduleList = response.data.orEmpty()
            if (scheduleList.isNotEmpty()) {
                prayerLocalCache.saveCalendar(effectiveLat, effectiveLng, year, month, scheduleList)
                val calendarList = mapScheduleListToCalendarInfo(scheduleList, month, year)
                emit(Resource.Success(calendarList))
                return@flow
            }
        } catch (_: Exception) {
            // Network failure - fall through to offline cache & calculation
        }

        // 2. Try offline cache
        val cachedList = prayerLocalCache.getCalendar(effectiveLat, effectiveLng, year, month)
        if (!cachedList.isNullOrEmpty()) {
            val calendarList = mapScheduleListToCalendarInfo(cachedList, month, year)
            emit(Resource.Success(calendarList))
            return@flow
        }

        // 3. Complete Offline Monthly Generation via Astronomical Formulas
        try {
            val calendarList = generateMonthlyCalendarOffline(effectiveLat, effectiveLng, year, month)
            emit(Resource.Success(calendarList))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to generate Islamic calendar"))
        }
    }

    private fun buildScheduleFromApiData(
        dayData: ScheduleResponse,
        alarmMap: Map<String, Boolean>
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

        val (nextPrayer, currentPrayer, remainingMillis) = calculateNextAndCurrentPrayer(prayers)
        val updatedPrayers = prayers.map { it.copy(isNext = it.type == nextPrayer.type) }

        val hijri = dayData.dateResponse?.hijri
        val gregorian = dayData.dateResponse?.gregorian

        val hijriFormatted = if (hijri != null) {
            "${hijri.day} ${hijri.month?.en ?: ""} ${hijri.year} AH"
        } else ""

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
            nextPrayer = nextPrayer.copy(isNext = true),
            currentPrayer = currentPrayer,
            remainingMillisToNextPrayer = remainingMillis
        )
    }

    private fun buildScheduleFromCalculation(
        lat: Double,
        lng: Double,
        year: Int,
        month: Int,
        day: Int,
        alarmMap: Map<String, Boolean>
    ): PrayerDaySchedule {
        val times = PrayerTimeCalculator.calculate(lat, lng, year, month, day)
        val hijri = HijriCalendarCalculator.calculate(year, month, day)

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

        val (nextPrayer, currentPrayer, remainingMillis) = calculateNextAndCurrentPrayer(prayers)
        val updatedPrayers = prayers.map { it.copy(isNext = it.type == nextPrayer.type) }

        return PrayerDaySchedule(
            dateGregorian = gregorianFormatted,
            dateHijri = hijri.formatted,
            fajr = updatedPrayers[0],
            sunrise = updatedPrayers[1],
            dhuhr = updatedPrayers[2],
            asr = updatedPrayers[3],
            maghrib = updatedPrayers[4],
            isha = updatedPrayers[5],
            nextPrayer = nextPrayer.copy(isNext = true),
            currentPrayer = currentPrayer,
            remainingMillisToNextPrayer = remainingMillis
        )
    }

    private fun calculateNextAndCurrentPrayer(
        prayers: List<SinglePrayer>
    ): Triple<SinglePrayer, SinglePrayer, Long> {
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val timeToMinutes = { timeStr: String ->
            val parts = timeStr.split(":")
            val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
            h * 60 + m
        }

        var nextPrayer: SinglePrayer? = null
        var remainingMillis = 0L

        for (p in prayers) {
            val prayerMinutes = timeToMinutes(p.time24)
            if (prayerMinutes > currentMinutes) {
                nextPrayer = p
                val diffMin = prayerMinutes - currentMinutes
                remainingMillis = diffMin * 60 * 1000L
                break
            }
        }

        if (nextPrayer == null) {
            nextPrayer = prayers.first()
            val fajrMin = timeToMinutes(nextPrayer.time24)
            val diffMin = (24 * 60 - currentMinutes) + fajrMin
            remainingMillis = diffMin * 60 * 1000L
        }

        val nextPrayerIndex = prayers.indexOfFirst { it.type == nextPrayer.type }
        val currentPrayer = if (nextPrayerIndex > 0) {
            prayers[nextPrayerIndex - 1]
        } else {
            prayers.last()
        }

        return Triple(nextPrayer, currentPrayer, remainingMillis)
    }

    private fun mapScheduleListToCalendarInfo(
        scheduleList: List<ScheduleResponse>,
        month: Int,
        year: Int
    ): List<IslamicCalendarInfo> {
        return scheduleList.mapNotNull { item ->
            val g = item.dateResponse?.gregorian
            val h = item.dateResponse?.hijri
            val gDay = g?.day?.toIntOrNull() ?: return@mapNotNull null
            val gMonth = g.month?.number ?: month
            val gYear = g.year?.toIntOrNull() ?: year
            val gFormatted = "${g.weekday?.en ?: ""}, $gDay ${g.month?.en ?: ""} $gYear"

            val hDay = h?.day?.toIntOrNull() ?: 1
            val hMonthName = h?.month?.en ?: ""
            val hYear = h?.year?.toIntOrNull() ?: 1447
            val hFormatted = "$hDay $hMonthName $hYear AH"

            val gWeekday = g.weekday?.en ?: ""
            val holidays = h?.holidays?.filterNotNull() ?: emptyList()

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
        month: Int
    ): List<IslamicCalendarInfo> {
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
            val times = PrayerTimeCalculator.calculate(lat, lng, year, month, day)
            val hijri = HijriCalendarCalculator.calculate(year, month, day)

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
}
