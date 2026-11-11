package com.example.noorapp.domain.usecase

import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.repository.PrayerAlarmRepository
import javax.inject.Inject

/**
 * Use case to toggle and update prayer alarm preferences.
 */
class TogglePrayerAlarmUseCase @Inject constructor(
    private val repository: PrayerAlarmRepository
) {
    suspend operator fun invoke(alarmItem: PrayerAlarmItem, isEnabled: Boolean) {
        val updated = alarmItem.copy(isAlarmActive = isEnabled)
        repository.updatePrayerAlarm(updated)
    }
}
