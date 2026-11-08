package com.ne.muslimprayers.qiblafinder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerAlarmItem
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType

@Entity(tableName = "prayer_alarms")
data class PrayerAlarmEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "prayer_name")
    val prayerName: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean
) {
    fun toDomain(): PrayerAlarmItem {
        val prayerType = try {
            PrayerType.valueOf(prayerName.uppercase())
        } catch (e: Exception) {
            when (id) {
                0 -> PrayerType.FAJR
                1 -> PrayerType.DHUHR
                2 -> PrayerType.ASR
                3 -> PrayerType.MAGHRIB
                else -> PrayerType.ISHA
            }
        }
        return PrayerAlarmItem(
            id = id,
            prayerType = prayerType,
            prayerTime = time,
            isAlarmActive = isActive
        )
    }

    companion object {
        fun fromDomain(domain: PrayerAlarmItem): PrayerAlarmEntity {
            return PrayerAlarmEntity(
                id = domain.id,
                prayerName = domain.prayerType.name,
                time = domain.prayerTime,
                isActive = domain.isAlarmActive
            )
        }

        val DEFAULT_ALARMS = listOf(
            PrayerAlarmEntity(0, "FAJR", "05:00", true),
            PrayerAlarmEntity(1, "DHUHR", "12:30", true),
            PrayerAlarmEntity(2, "ASR", "15:45", true),
            PrayerAlarmEntity(3, "MAGHRIB", "18:15", true),
            PrayerAlarmEntity(4, "ISHA", "19:45", true)
        )
    }
}
