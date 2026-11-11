package com.example.noorapp.domain.usecase.qibla

import com.example.noorapp.core.common.CompassReading
import com.example.noorapp.domain.repository.CompassRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCompassReadingUseCase @Inject constructor(
    private val compassRepository: CompassRepository
) {
    fun hasCompassSensor(): Boolean = compassRepository.hasCompassSensor()

    operator fun invoke(latitude: Double, longitude: Double): Flow<CompassReading> {
        return compassRepository.observeCompassReadings(latitude, longitude)
    }
}

