package com.ne.muslimprayers.qiblafinder.domain.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Qibla direction calculation.
 */
interface QiblaRepository {
    /**
     * Fetches Qibla direction bearing in degrees for given latitude and longitude.
     */
    fun getQiblaDirection(
        latitude: Double,
        longitude: Double
    ): Flow<Resource<Float>>
}
