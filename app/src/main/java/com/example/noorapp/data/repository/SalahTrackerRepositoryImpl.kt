package com.example.noorapp.data.repository

import com.example.noorapp.data.local.dao.SalahRecordDao
import com.example.noorapp.data.local.entity.SalahRecordEntity
import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.repository.SalahTrackerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalahTrackerRepositoryImpl @Inject constructor(
    private val salahRecordDao: SalahRecordDao
) : SalahTrackerRepository {

    override fun getRecordForDate(date: String): Flow<DailySalahRecord> {
        return salahRecordDao.getRecordForDate(date).map { entity ->
            entity?.toDomain() ?: DailySalahRecord(date = date)
        }
    }

    override fun getRecordsBetween(startDate: String, endDate: String): Flow<List<DailySalahRecord>> {
        return salahRecordDao.getRecordsBetween(startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun setPrayerOffered(date: String, prayerType: PrayerType, offered: Boolean) {
        val existing = salahRecordDao.getRecordForDateSync(date)?.toDomain() ?: DailySalahRecord(date = date)
        val updated = existing.copyWithPrayer(prayerType, offered)
        salahRecordDao.insertOrUpdateRecord(SalahRecordEntity.fromDomain(updated))
    }

    override suspend fun setAllPrayersOffered(date: String, offered: Boolean) {
        val record = DailySalahRecord(
            date = date,
            fajr = offered,
            dhuhr = offered,
            asr = offered,
            maghrib = offered,
            isha = offered
        )
        salahRecordDao.insertOrUpdateRecord(SalahRecordEntity.fromDomain(record))
    }
}
