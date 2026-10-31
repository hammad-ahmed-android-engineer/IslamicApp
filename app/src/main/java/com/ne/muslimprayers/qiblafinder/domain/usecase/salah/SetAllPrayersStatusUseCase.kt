package com.ne.muslimprayers.qiblafinder.domain.usecase.salah

import com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
import javax.inject.Inject

class SetAllPrayersStatusUseCase @Inject constructor(
    private val repository: SalahTrackerRepository
) {
    suspend operator fun invoke(date: String, offered: Boolean) {
        repository.setAllPrayersOffered(date, offered)
    }
}
