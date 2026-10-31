package com.ne.muslimprayers.qiblafinder.domain.usecase

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerDaySchedule
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerScheduleRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

/**
 * Use case to calculate and retrieve prayer timings schedule for given location and date.
 */
class GetPrayerScheduleUseCase @Inject constructor(
    private val repository: PrayerScheduleRepository
) {
    operator fun invoke(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance()
    ): Flow<Resource<PrayerDaySchedule>> {
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return repository.getPrayerSchedule(latitude, longitude, month, year, day)
    }
}
