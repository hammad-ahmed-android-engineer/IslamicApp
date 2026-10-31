package com.ne.muslimprayers.qiblafinder.domain.usecase

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.IslamicCalendarInfo
import com.ne.muslimprayers.qiblafinder.domain.repository.PrayerScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve monthly Islamic calendar information.
 */
class GetIslamicCalendarUseCase @Inject constructor(
    private val repository: PrayerScheduleRepository
) {
    operator fun invoke(
        latitude: Double,
        longitude: Double,
        year: Int,
        month: Int
    ): Flow<Resource<List<IslamicCalendarInfo>>> {
        return repository.getIslamicCalendar(latitude, longitude, month, year)
    }
}
