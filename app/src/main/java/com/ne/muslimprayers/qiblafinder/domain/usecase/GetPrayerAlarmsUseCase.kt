package com.ne.muslimprayers.qiblafinder.domain.usecase

import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
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
