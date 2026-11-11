package com.example.noorapp.domain.repository

import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.model.PrayerType
import kotlinx.coroutines.flow.Flow

interface SalahTrackerRepository {
    fun getRecordForDate(date: String): Flow<DailySalahRecord>
    fun getRecordsBetween(startDate: String, endDate: String): Flow<List<DailySalahRecord>>
    suspend fun setPrayerOffered(date: String, prayerType: PrayerType, offered: Boolean)
    suspend fun setAllPrayersOffered(date: String, offered: Boolean)
}
