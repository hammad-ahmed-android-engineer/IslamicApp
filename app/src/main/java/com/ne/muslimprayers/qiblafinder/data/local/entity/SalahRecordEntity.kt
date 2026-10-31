package com.ne.muslimprayers.qiblafinder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ne.muslimprayers.qiblafinder.domain.model.DailySalahRecord

@Entity(tableName = "salah_records")
data class SalahRecordEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String, // Format: "yyyy-MM-dd"

    @ColumnInfo(name = "fajr")
    val fajr: Boolean = false,

    @ColumnInfo(name = "dhuhr")
    val dhuhr: Boolean = false,

    @ColumnInfo(name = "asr")
    val asr: Boolean = false,

    @ColumnInfo(name = "maghrib")
    val maghrib: Boolean = false,

    @ColumnInfo(name = "isha")
    val isha: Boolean = false
) {
    fun toDomain(): DailySalahRecord {
        return DailySalahRecord(
            date = date,
            fajr = fajr,
            dhuhr = dhuhr,
            asr = asr,
            maghrib = maghrib,
            isha = isha
        )
    }

    companion object {
        fun fromDomain(domain: DailySalahRecord): SalahRecordEntity {
            return SalahRecordEntity(
                date = domain.date,
                fajr = domain.fajr,
                dhuhr = domain.dhuhr,
                asr = domain.asr,
                maghrib = domain.maghrib,
                isha = domain.isha
            )
        }
    }
}
