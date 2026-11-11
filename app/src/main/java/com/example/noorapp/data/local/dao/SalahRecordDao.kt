package com.example.noorapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noorapp.data.local.entity.SalahRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalahRecordDao {

    @Query("SELECT * FROM salah_records WHERE date = :date")
    fun getRecordForDate(date: String): Flow<SalahRecordEntity?>

    @Query("SELECT * FROM salah_records WHERE date = :date")
    suspend fun getRecordForDateSync(date: String): SalahRecordEntity?

    @Query("SELECT * FROM salah_records WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getRecordsBetween(startDate: String, endDate: String): Flow<List<SalahRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: SalahRecordEntity)
}
