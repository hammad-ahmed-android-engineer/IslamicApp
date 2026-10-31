package com.ne.muslimprayers.qiblafinder.domain.usecase.salah

import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.repository.SalahTrackerRepository
import javax.inject.Inject

class UpdatePrayerStatusUseCase @Inject constructor(
    private val repository: SalahTrackerRepository
) {
    suspend operator fun invoke(date: String, prayerType: PrayerType, offered: Boolean) {
        repository.setPrayerOffered(date, prayerType, offered)
    }
}
