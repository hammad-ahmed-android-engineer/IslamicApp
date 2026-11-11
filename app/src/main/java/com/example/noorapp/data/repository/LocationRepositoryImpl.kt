package com.example.noorapp.data.repository

import com.example.noorapp.core.common.LocationHelper
import com.example.noorapp.domain.model.UserLocation
import com.example.noorapp.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationHelper: LocationHelper
) : LocationRepository {

    override suspend fun getCurrentOrEffectiveLocation(includeCityName: Boolean): UserLocation {
        val currentLocation = locationHelper.getCurrentLocation()
        val lastSaved = locationHelper.getLastSavedLocation()
        val effectiveLocation = currentLocation ?: locationHelper.getEffectiveLocation()
        val isRealUserLocation = currentLocation != null || lastSaved != null
        val hasPermission = locationHelper.hasLocationPermission()

        val cityName = if (includeCityName && (isRealUserLocation || hasPermission)) {
            locationHelper.getCityAndCountry(effectiveLocation.latitude, effectiveLocation.longitude)
        } else {
            ""
        }

        return UserLocation(
            latitude = effectiveLocation.latitude,
            longitude = effectiveLocation.longitude,
            cityName = cityName,
            isFallback = !isRealUserLocation,
            hasPermission = hasPermission
        )
    }

    override fun getLastSavedCoordinates(): Pair<Double, Double>? {
        return locationHelper.getLastSavedLocation()?.let { it.latitude to it.longitude }
    }

    override suspend fun getCityAndCountry(latitude: Double, longitude: Double): String {
        return locationHelper.getCityAndCountry(latitude, longitude)
    }

    override fun hasLocationPermission(): Boolean {
        return locationHelper.hasLocationPermission()
    }
}

