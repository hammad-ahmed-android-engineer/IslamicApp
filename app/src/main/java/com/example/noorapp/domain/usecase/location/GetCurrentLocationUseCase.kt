package com.example.noorapp.domain.usecase.location

import com.example.noorapp.domain.model.UserLocation
import com.example.noorapp.domain.repository.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(includeCityName: Boolean = true): UserLocation {
        return locationRepository.getCurrentOrEffectiveLocation(includeCityName = includeCityName)
    }

    fun getLastSavedCoordinates(): Pair<Double, Double>? {
        return locationRepository.getLastSavedCoordinates()
    }

    suspend fun getCityAndCountry(latitude: Double, longitude: Double): String {
        return locationRepository.getCityAndCountry(latitude, longitude)
    }

    fun hasLocationPermission(): Boolean {
        return locationRepository.hasLocationPermission()
    }
}

