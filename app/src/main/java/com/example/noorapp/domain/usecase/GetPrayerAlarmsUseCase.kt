package com.example.noorapp.domain.usecase

import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.repository.PrayerAlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe prayer alarm configurations from the database.
 */
class GetPrayerAlarmsUseCase @Inject constructor(
    private val repository: PrayerAlarmRepository
) {
    operator fun invoke(): Flow<List<PrayerAlarmItem>> {
        return repository.getPrayerAlarms()
    }
}
