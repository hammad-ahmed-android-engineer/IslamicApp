package com.example.noorapp.domain.usecase.qibla

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.repository.QiblaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to compute Qibla bearing direction for specified geographic coordinates.
 */
class GetQiblaDirectionUseCase @Inject constructor(
    private val qiblaRepository: QiblaRepository
) {
    operator fun invoke(latitude: Double, longitude: Double): Flow<Resource<Float>> {
        return qiblaRepository.getQiblaDirection(latitude, longitude)
    }
}
