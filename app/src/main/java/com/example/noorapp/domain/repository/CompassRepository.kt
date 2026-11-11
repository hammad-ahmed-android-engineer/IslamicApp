package com.example.noorapp.domain.repository

import com.example.noorapp.core.common.CompassReading
import kotlinx.coroutines.flow.Flow

interface CompassRepository {
    fun hasCompassSensor(): Boolean
    fun observeCompassReadings(latitude: Double, longitude: Double): Flow<CompassReading>
}

