package com.example.noorapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.noorapp.data.local.dao.PrayerAlarmDao
import com.example.noorapp.data.local.dao.SalahRecordDao
import com.example.noorapp.data.local.entity.PrayerAlarmEntity
import com.example.noorapp.data.local.entity.SalahRecordEntity

@Database(
    entities = [
        PrayerAlarmEntity::class,
        SalahRecordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class IslamicAppDatabase : RoomDatabase() {
    abstract val prayerAlarmDao: PrayerAlarmDao
    abstract val salahRecordDao: SalahRecordDao
}
