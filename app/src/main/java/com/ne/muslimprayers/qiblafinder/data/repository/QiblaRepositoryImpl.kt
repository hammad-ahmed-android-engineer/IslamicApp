package com.ne.muslimprayers.qiblafinder.data.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.data.remote.api.PrayerApiService
import com.ne.muslimprayers.qiblafinder.domain.repository.QiblaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [QiblaRepository] communicating with [PrayerApiService].
 */
@Singleton
class QiblaRepositoryImpl @Inject constructor(
    private val apiService: PrayerApiService
) : QiblaRepository {

    override fun getQiblaDirection(latitude: Double, longitude: Double): Flow<Resource<Float>> = flow {
        emit(Resource.Loading(true))
        try {
            val response = apiService.getQibla(latitude, longitude)
            val dir = response.data?.direction
            if (dir != null) {
                emit(Resource.Success(dir))
            } else {
                emit(Resource.Error("Could not calculate Qibla direction for location"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error calculating Qibla"))
        }
    }
}
