package com.example.noorapp.domain.usecase

import com.example.noorapp.domain.model.PrayerDaySchedule
import com.example.noorapp.domain.repository.PrayerAlarmRepository
import javax.inject.Inject

/**
 * Use case to initialize default prayer alarms and synchronize alarm timings with a daily prayer schedule.
 */
class SyncPrayerAlarmsUseCase @Inject constructor(
    private val repository: PrayerAlarmRepository
) {
    suspend fun initDefaultIfEmpty() {
        repository.initDefaultAlarmsIfEmpty()
    }

    suspend fun syncWithSchedule(schedule: PrayerDaySchedule) {
        repository.syncPrayerAlarmsWithSchedule(schedule)
    }
}

