package com.example.noorapp.data.repository

import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.repository.AlarmSchedulerRepository
import com.example.noorapp.service.AlarmScheduler
import javax.inject.Inject

class AlarmSchedulerRepositoryImpl @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) : AlarmSchedulerRepository {

    override fun scheduleAlarm(alarmItem: PrayerAlarmItem) {
        alarmScheduler.scheduleAlarm(alarmItem)
    }

    override fun cancelAlarm(alarmId: Int) {
        alarmScheduler.cancelAlarm(alarmId)
    }
}

