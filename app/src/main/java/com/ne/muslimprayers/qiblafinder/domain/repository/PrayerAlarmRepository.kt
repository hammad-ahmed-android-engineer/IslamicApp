package com.ne.muslimprayers.qiblafinder.domain.repository

import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing local Prayer Alarm preferences and database states.
 */
interface PrayerAlarmRepository {
    /**
     * Observes all prayer alarm items from the local database.
     */
    fun getPrayerAlarms(): Flow<List<PrayerAlarmItem>>

    /**
     * Updates an alarm preference in the database.
     */
    suspend fun updatePrayerAlarm(alarmItem: PrayerAlarmItem)

    /**
     * Initializes default prayer alarm entries if database is empty.
     */
    suspend fun initDefaultAlarmsIfEmpty()

    /**
     * Synchronizes local prayer alarm times with calculated schedule timings.
     */
    suspend fun syncPrayerAlarmsWithSchedule(schedule: PrayerDaySchedule)
}
