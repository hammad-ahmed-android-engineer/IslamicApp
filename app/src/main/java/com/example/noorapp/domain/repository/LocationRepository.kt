package com.example.noorapp.domain.repository

import com.example.noorapp.domain.model.UserLocation

interface LocationRepository {
    suspend fun getCurrentOrEffectiveLocation(includeCityName: Boolean = true): UserLocation
    fun getLastSavedCoordinates(): Pair<Double, Double>?
    suspend fun getCityAndCountry(latitude: Double, longitude: Double): String
    fun hasLocationPermission(): Boolean
}

