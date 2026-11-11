package com.example.noorapp.data.repository

import com.example.noorapp.core.common.CompassReading
import com.example.noorapp.core.common.CompassSensorManager
import com.example.noorapp.domain.repository.CompassRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CompassRepositoryImpl @Inject constructor(
    private val compassSensorManager: CompassSensorManager
) : CompassRepository {

    override fun hasCompassSensor(): Boolean = compassSensorManager.hasCompassSensor()

    override fun observeCompassReadings(latitude: Double, longitude: Double): Flow<CompassReading> {
        return compassSensorManager.getCompassReadingFlow(latitude, longitude)
    }
}

