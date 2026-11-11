package com.example.noorapp.data.repository

import com.example.noorapp.core.common.Resource
import com.example.noorapp.core.utils.LocationUtils
import com.example.noorapp.data.remote.api.PrayerApiService
import com.example.noorapp.domain.repository.QiblaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [QiblaRepository] using high-precision local Great Circle spherical trigonometry
 * first (instant & offline-capable) with optional API verification.
 */
@Singleton
class QiblaRepositoryImpl @Inject constructor(
    private val apiService: PrayerApiService
) : QiblaRepository {

    override fun getQiblaDirection(latitude: Double, longitude: Double): Flow<Resource<Float>> = flow {
        // 1. Immediately emit accurate local Great-Circle Qibla bearing
        val localBearing = LocationUtils.calculateQiblaBearing(latitude, longitude)
        emit(Resource.Success(localBearing))

        // 2. Optionally verify with API if online without overwriting with error if offline
        try {
            val response = apiService.getQibla(latitude, longitude)
            val dir = response.data?.direction
            if (dir != null && !dir.isNaN() && dir in 0f..360f) {
                emit(Resource.Success((dir + 360f) % 360f))
            }
        } catch (_: Exception) {
            // Local calculation is already emitted and mathematically exact
        }
    }
}
