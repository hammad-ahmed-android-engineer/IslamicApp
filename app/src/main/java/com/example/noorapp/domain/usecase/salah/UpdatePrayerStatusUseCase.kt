package com.example.noorapp.domain.usecase.salah

import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.repository.SalahTrackerRepository
import javax.inject.Inject

class UpdatePrayerStatusUseCase @Inject constructor(
    private val repository: SalahTrackerRepository
) {
    suspend operator fun invoke(date: String, prayerType: PrayerType, offered: Boolean) {
        repository.setPrayerOffered(date, prayerType, offered)
    }
}
