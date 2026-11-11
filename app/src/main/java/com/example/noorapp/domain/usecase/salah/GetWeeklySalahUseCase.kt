package com.example.noorapp.domain.usecase.salah

import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.repository.SalahTrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklySalahUseCase @Inject constructor(
    private val repository: SalahTrackerRepository
) {
    operator fun invoke(startDate: String, endDate: String): Flow<List<DailySalahRecord>> {
        return repository.getRecordsBetween(startDate, endDate)
    }
}
