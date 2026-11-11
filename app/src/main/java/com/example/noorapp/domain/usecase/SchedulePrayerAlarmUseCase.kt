package com.example.noorapp.domain.usecase

import com.example.noorapp.domain.model.PrayerAlarmItem
import com.example.noorapp.domain.repository.AlarmSchedulerRepository
import javax.inject.Inject

class SchedulePrayerAlarmUseCase @Inject constructor(
    private val alarmSchedulerRepository: AlarmSchedulerRepository
) {
    fun schedule(alarmItem: PrayerAlarmItem) {
        alarmSchedulerRepository.scheduleAlarm(alarmItem)
    }

    fun cancel(alarmId: Int) {
        alarmSchedulerRepository.cancelAlarm(alarmId)
    }
}

