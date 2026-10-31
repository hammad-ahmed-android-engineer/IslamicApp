package com.ne.muslimprayers.qiblafinder.data.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.data.local.dao.PrayerAlarmDao
import com.ne.muslimprayers.qiblafinder.data.remote.api.PrayerApiService
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
 * Implementation of [PrayerScheduleRepository] that communicates with the AlAdhan API and local PrayerAlarmDao.
 */
@Singleton
class PrayerScheduleRepositoryImpl @Inject constructor(
    private val apiService: PrayerApiService,
    private val prayerAlarmDao: PrayerAlarmDao
) : PrayerScheduleRepository {

    override fun getPrayerSchedule(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int,
        day: Int
    ): Flow<Resource<PrayerDaySchedule>> = flow {
        emit(Resource.Loading(true))
        try {
            val response = apiService.getCalendar(latitude, longitude, month, year)
            val scheduleList = response.data.orEmpty()
            val dayData = scheduleList.find {
                it.dateResponse?.gregorian?.day?.toIntOrNull() == day
            } ?: scheduleList.firstOrNull()

            if (dayData == null) {
                emit(Resource.Error("Could not find prayer timings for coordinates: $latitude, $longitude"))
                return@flow
            }

            val alarms = prayerAlarmDao.getAllAlarms().firstOrNull().orEmpty()
            val alarmMap = alarms.associate { it.prayerName.uppercase() to it.isActive }

            val timings = dayData.timingResponse
            val cleanTime = { raw: String? ->
                raw?.substringBefore(" ")?.trim() ?: "00:00"
            }

            val fajrTime = cleanTime(timings?.fajr)
            val sunriseTime = cleanTime(timings?.sunrise)
            val dhuhrTime = cleanTime(timings?.dhuhr)
            val asrTime = cleanTime(timings?.asr)
            val maghribTime = cleanTime(timings?.maghrib)
            val ishaTime = cleanTime(timings?.isha)

            val now = Calendar.getInstance()
            val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

            val timeToMinutes = { timeStr: String ->
                val parts = timeStr.split(":")
                val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                h * 60 + m
            }

            val prayers = listOf(
                SinglePrayer(PrayerType.FAJR, fajrTime, formatTo12Hour(fajrTime), isAlarmActive = alarmMap["FAJR"] ?: false),
                SinglePrayer(PrayerType.SUNRISE, sunriseTime, formatTo12Hour(sunriseTime)),
                SinglePrayer(PrayerType.DHUHR, dhuhrTime, formatTo12Hour(dhuhrTime), isAlarmActive = alarmMap["DHUHR"] ?: false),
                SinglePrayer(PrayerType.ASR, asrTime, formatTo12Hour(asrTime), isAlarmActive = alarmMap["ASR"] ?: false),
                SinglePrayer(PrayerType.MAGHRIB, maghribTime, formatTo12Hour(maghribTime), isAlarmActive = alarmMap["MAGHRIB"] ?: false),
                SinglePrayer(PrayerType.ISHA, ishaTime, formatTo12Hour(ishaTime), isAlarmActive = alarmMap["ISHA"] ?: false)
            )

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
                prayers.last() // Before Fajr, the active prayer time is Isha from the night
            }

            val updatedPrayers = prayers.map { it.copy(isNext = it.type == nextPrayer.type) }

            val hijri = dayData.dateResponse?.hijri
            val gregorian = dayData.dateResponse?.gregorian

            val hijriFormatted = if (hijri != null) {
                "${hijri.day} ${hijri.month?.en ?: ""} ${hijri.year} AH"
            } else ""

            val gregorianFormatted = if (gregorian != null) {
                "${gregorian.weekday?.en ?: ""}, ${gregorian.day} ${gregorian.month?.en ?: ""} ${gregorian.year}"
            } else ""

            val schedule = PrayerDaySchedule(
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

            emit(Resource.Success(schedule))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load prayer timings"))
        }
    }

    override fun getIslamicCalendar(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int
    ): Flow<Resource<List<IslamicCalendarInfo>>> = flow {
        emit(Resource.Loading(true))
        try {
            val response = apiService.getCalendar(latitude, longitude, month, year)
            val scheduleList = response.data.orEmpty()

            val calendarList = scheduleList.mapNotNull { item ->
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
            emit(Resource.Success(calendarList))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load Islamic calendar"))
        }
    }

    private fun formatTo12Hour(time24: String): String {
        return try {
            val sdf24 = SimpleDateFormat("HH:mm", Locale.US)
            val sdf12 = SimpleDateFormat("hh:mm a", Locale.US)
            val date = sdf24.parse(time24)
            if (date != null) sdf12.format(date) else time24
        } catch (e: Exception) {
            time24
        }
    }
}
