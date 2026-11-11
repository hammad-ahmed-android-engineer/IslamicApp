package com.example.noorapp.domain.usecase.salah

import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.repository.SalahTrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to observe daily prayer completion records for a specific date.
 */
class GetDailySalahUseCase @Inject constructor(
    private val repository: SalahTrackerRepository
) {
    operator fun invoke(date: String): Flow<DailySalahRecord> = repository.getRecordForDate(date)
}

