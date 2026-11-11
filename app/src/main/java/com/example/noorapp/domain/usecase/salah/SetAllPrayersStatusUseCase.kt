package com.example.noorapp.domain.usecase.salah

import com.example.noorapp.domain.repository.SalahTrackerRepository
import javax.inject.Inject

class SetAllPrayersStatusUseCase @Inject constructor(
    private val repository: SalahTrackerRepository
) {
    suspend operator fun invoke(date: String, offered: Boolean) {
        repository.setAllPrayersOffered(date, offered)
    }
}
