package com.example.noorapp.domain.repository

import com.example.noorapp.domain.model.PrayerAlarmItem

interface AlarmSchedulerRepository {
    fun scheduleAlarm(alarmItem: PrayerAlarmItem)
    fun cancelAlarm(alarmId: Int)
}

