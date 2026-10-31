package com.ne.muslimprayers.qiblafinder.domain.usecase.salah

import com.ne.muslimprayers.qiblafinder.domain.model.DailySalahRecord
import com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklySalahUseCase @Inject constructor(
    private val repository: SalahTrackerRepository
) {
    operator fun invoke(startDate: String, endDate: String): Flow<List<DailySalahRecord>> {
        return repository.getRecordsBetween(startDate, endDate)
    }
}
