package com.ne.muslimprayers.qiblafinder.domain.usecase.qibla

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.repository.QiblaRepository
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
