package com.ne.muslimprayers.qiblafinder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ne.muslimprayers.qiblafinder.data.local.entity.PrayerAlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerAlarmDao {
    @Query("SELECT * FROM prayer_alarms ORDER BY id ASC")
    fun getAllAlarms(): Flow<List<PrayerAlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarms(alarms: List<PrayerAlarmEntity>)

    @Update
    suspend fun updateAlarm(alarm: PrayerAlarmEntity)

    @Query("SELECT COUNT(*) FROM prayer_alarms")
    suspend fun getAlarmCount(): Int
}
