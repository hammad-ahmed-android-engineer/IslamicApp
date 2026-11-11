package com.example.noorapp.domain.repository

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.IslamicCalendarInfo
import com.example.noorapp.domain.model.PrayerDaySchedule
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for fetching prayer timing schedules and calendar information.
 */
interface PrayerScheduleRepository {
    /**
     * Retrieves prayer schedule for a specific location and day.
     */
    fun getPrayerSchedule(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int,
        day: Int
    ): Flow<Resource<PrayerDaySchedule>>

    /**
     * Retrieves monthly Islamic calendar information including Gregorian and Hijri dates.
     */
    fun getIslamicCalendar(
        latitude: Double,
        longitude: Double,
        month: Int,
        year: Int
    ): Flow<Resource<List<IslamicCalendarInfo>>>

    /**
     * Recalculates current/next prayer fields of [schedule] against [nowMillis].
     */
    fun recalculateScheduleForNow(
        schedule: PrayerDaySchedule,
        nowMillis: Long = System.currentTimeMillis()
    ): PrayerDaySchedule
}
