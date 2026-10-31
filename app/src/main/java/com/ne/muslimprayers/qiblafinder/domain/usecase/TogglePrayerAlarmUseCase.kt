package com.ne.muslimprayers.qiblafinder.domain.usecase

import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
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
