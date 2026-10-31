package com.ne.muslimprayers.qiblafinder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ne.muslimprayers.qiblafinder.data.local.dao.PrayerAlarmDao
import com.ne.muslimprayers.qiblafinder.data.local.dao.SalahRecordDao
import com.ne.muslimprayers.qiblafinder.data.local.entity.PrayerAlarmEntity
import com.ne.muslimprayers.qiblafinder.data.local.entity.SalahRecordEntity

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
