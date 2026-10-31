package com.ne.muslimprayers.qiblafinder.data.repository

import com.ne.muslimprayers.qiblafinder.data.local.dao.PrayerAlarmDao
import com.ne.muslimprayers.qiblafinder.data.local.entity.PrayerAlarmEntity
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerAlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [PrayerAlarmRepository] managing local prayer alarm database entries.
 */
@Singleton
class PrayerAlarmRepositoryImpl @Inject constructor(
    private val prayerAlarmDao: PrayerAlarmDao
) : PrayerAlarmRepository {

    override fun getPrayerAlarms(): Flow<List<PrayerAlarmItem>> {
        return prayerAlarmDao.getAllAlarms().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun updatePrayerAlarm(alarmItem: PrayerAlarmItem) {
        prayerAlarmDao.updateAlarm(PrayerAlarmEntity.fromDomain(alarmItem))
    }

    override suspend fun initDefaultAlarmsIfEmpty() {
        if (prayerAlarmDao.getAlarmCount() == 0) {
            prayerAlarmDao.insertAlarms(PrayerAlarmEntity.DEFAULT_ALARMS)
        }
    }

    override suspend fun syncPrayerAlarmsWithSchedule(schedule: PrayerDaySchedule) {
        val existingAlarms = prayerAlarmDao.getAllAlarms().firstOrNull().orEmpty()
        for (prayer in schedule.allPrayers) {
            val existing = existingAlarms.find {
                it.prayerName.equals(prayer.type.name, ignoreCase = true) ||
                        it.prayerName.equals(prayer.type.displayName, ignoreCase = true)
            }
            if (existing != null) {
                prayerAlarmDao.updateAlarm(existing.copy(time = prayer.time24))
            }
        }
    }
}
