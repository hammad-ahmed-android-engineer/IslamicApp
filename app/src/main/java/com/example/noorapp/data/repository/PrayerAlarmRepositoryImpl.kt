package com.example.noorapp.data.repository

import com.example.noorapp.core.common.PrayerSettingsManager
import com.example.noorapp.data.local.dao.PrayerAlarmDao
import com.example.noorapp.data.local.entity.PrayerAlarmEntity
import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.model.PrayerDaySchedule
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.repository.PrayerAlarmRepository
import com.example.noorapp.service.AlarmScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [PrayerAlarmRepository] managing local prayer alarm database entries
 * and keeping them synchronized with [PrayerSettingsManager] and [AlarmScheduler].
 */
@Singleton
class PrayerAlarmRepositoryImpl @Inject constructor(
    private val prayerAlarmDao: PrayerAlarmDao,
    private val prayerSettingsManager: PrayerSettingsManager,
    private val alarmScheduler: AlarmScheduler
) : PrayerAlarmRepository {

    override fun getPrayerAlarms(): Flow<List<PrayerAlarmItem>> {
        return prayerAlarmDao.getAllAlarms()
            .onStart {
                initDefaultAlarmsIfEmpty()
            }
            .map { list ->
                val effectiveList = if (list.isEmpty()) {
                    PrayerAlarmEntity.DEFAULT_ALARMS.map { entity ->
                        val domain = entity.toDomain()
                        entity.copy(isActive = prayerSettingsManager.isPrayerAlarmEnabled(domain.prayerType))
                    }
                } else {
                    list
                }
                effectiveList.map { it.toDomain() }
            }
    }

    override suspend fun updatePrayerAlarm(alarmItem: PrayerAlarmItem) {
        prayerSettingsManager.setPrayerAlarmEnabled(alarmItem.prayerType, alarmItem.isAlarmActive)
        prayerAlarmDao.insertAlarms(listOf(PrayerAlarmEntity.fromDomain(alarmItem)))
    }

    override suspend fun initDefaultAlarmsIfEmpty() {
        if (prayerAlarmDao.getAlarmCount() == 0) {
            val defaultsWithPrefs = PrayerAlarmEntity.DEFAULT_ALARMS.map { entity ->
                val domain = entity.toDomain()
                entity.copy(isActive = prayerSettingsManager.isPrayerAlarmEnabled(domain.prayerType))
            }
            prayerAlarmDao.insertAlarms(defaultsWithPrefs)
        }
    }

    override suspend fun syncPrayerAlarmsWithSchedule(schedule: PrayerDaySchedule) {
        initDefaultAlarmsIfEmpty()
        val existingAlarms = prayerAlarmDao.getAllAlarms().firstOrNull().orEmpty()
        val updatedEntities = mutableListOf<PrayerAlarmEntity>()
        val notificationsEnabled = prayerSettingsManager.getCurrentSettings().notificationsEnabled

        val mainPrayers = listOf(
            0 to schedule.fajr,
            1 to schedule.dhuhr,
            2 to schedule.asr,
            3 to schedule.maghrib,
            4 to schedule.isha
        )

        for ((alarmId, prayer) in mainPrayers) {
            val existing = existingAlarms.find {
                it.id == alarmId ||
                        it.prayerName.equals(prayer.type.name, ignoreCase = true) ||
                        it.prayerName.equals(prayer.type.displayName, ignoreCase = true)
            }
            val isEnabled = existing?.isActive ?: prayerSettingsManager.isPrayerAlarmEnabled(prayer.type)
            prayerSettingsManager.setPrayerAlarmEnabled(prayer.type, isEnabled)

            val entity = PrayerAlarmEntity(
                id = alarmId,
                prayerName = prayer.type.name,
                time = prayer.time24,
                isActive = isEnabled
            )
            updatedEntities.add(entity)

            val domainItem = entity.toDomain()
            if (notificationsEnabled && isEnabled) {
                alarmScheduler.scheduleAlarm(domainItem)
            } else {
                alarmScheduler.cancelAlarm(alarmId)
            }
        }

        if (updatedEntities.isNotEmpty()) {
            prayerAlarmDao.insertAlarms(updatedEntities)
        }
    }
}
