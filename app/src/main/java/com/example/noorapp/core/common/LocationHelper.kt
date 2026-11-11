package com.example.noorapp.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.example.noorapp.R
import com.example.noorapp.core.utils.LocationUtils
import com.example.noorapp.core.utils.ValidationUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    private val prefs by lazy {
        context.getSharedPreferences("location_cache", Context.MODE_PRIVATE)
    }

    fun hasLocationPermission(): Boolean = LocationUtils.hasLocationPermission(context)

    fun isLocationEnabled(): Boolean = LocationUtils.isLocationEnabled(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return getLastSavedLocation()
        }

        return withContext(Dispatchers.IO) {
            try {
                // 1. Check recent last known location from FusedLocationProvider
                val lastLocation = fusedLocationProviderClient.lastLocation.await()
                if (lastLocation != null && isLocationRecent(lastLocation)) {
                    saveCoordinates(lastLocation.latitude, lastLocation.longitude)
                    return@withContext lastLocation
                }

                // 2. Request fresh high-accuracy location with timeout
                val cts = CancellationTokenSource()
                val freshLocation = withTimeoutOrNull(4000L) {
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cts.token
                    ).await()
                }
                if (freshLocation != null) {
                    saveCoordinates(freshLocation.latitude, freshLocation.longitude)
                    return@withContext freshLocation
                }

                // 3. Fallback to older lastLocation if available
                if (lastLocation != null) {
                    saveCoordinates(lastLocation.latitude, lastLocation.longitude)
                    return@withContext lastLocation
                }

                // 4. Fallback to native LocationManager providers (GPS, Network, Passive)
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                if (locationManager != null) {
                    val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (gpsLoc != null) {
                        saveCoordinates(gpsLoc.latitude, gpsLoc.longitude)
                        return@withContext gpsLoc
                    }

                    val networkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (networkLoc != null) {
                        saveCoordinates(networkLoc.latitude, networkLoc.longitude)
                        return@withContext networkLoc
                    }

                    val passiveLoc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    if (passiveLoc != null) {
                        saveCoordinates(passiveLoc.latitude, passiveLoc.longitude)
                        return@withContext passiveLoc
                    }
                }

                // 5. Fallback to previously saved persistent coordinates for offline continuity
                getLastSavedLocation()
            } catch (e: Exception) {
                getLastSavedLocation()
            }
        }
    }

    /**
     * Guarantees a valid Location is returned for prayer times calculation.
     * Falls back to saved location, or Makkah default if brand new install offline.
     */
    suspend fun getEffectiveLocation(): Location {
        return getCurrentLocation()
            ?: getLastSavedLocation()
            ?: Location("makkah_default").apply {
                latitude = 21.4225
                longitude = 39.8262
            }
    }

    private fun saveCoordinates(lat: Double, lng: Double) {
        if (ValidationUtils.isValidCoordinates(lat, lng)) {
            prefs.edit().apply {
                putFloat("last_lat", lat.toFloat())
                putFloat("last_lng", lng.toFloat())
                apply()
            }
        }
    }

    fun getLastSavedLocation(): Location? {
        if (prefs.contains("last_lat") && prefs.contains("last_lng")) {
            val lat = prefs.getFloat("last_lat", 0f).toDouble()
            val lng = prefs.getFloat("last_lng", 0f).toDouble()
            if (ValidationUtils.isValidCoordinates(lat, lng)) {
                return Location("cached_offline").apply {
                    latitude = lat
                    longitude = lng
                }
            }
        }
        return null
    }

    private fun isLocationRecent(location: Location): Boolean {
        val maxAgeMs = 15 * 60 * 1000L // 15 minutes
        val locationTime = location.time
        return (System.currentTimeMillis() - locationTime) < maxAgeMs
    }

    suspend fun getCityAndCountry(latitude: Double, longitude: Double): String {
        if (!ValidationUtils.isValidCoordinates(latitude, longitude)) {
            val fallback = context.getString(R.string.unknown_location)
            return prefs.getString("last_city", fallback) ?: fallback
        }
        return withContext(Dispatchers.IO) {
            try {
                if (Geocoder.isPresent()) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                            val country = addr.countryName
                            val result = if (!city.isNullOrBlank() && !country.isNullOrBlank()) {
                                "$city, $country"
                            } else if (!city.isNullOrBlank()) {
                                city
                            } else if (!country.isNullOrBlank()) {
                                country
                            } else null

                            if (result != null) {
                                prefs.edit().putString("last_city", result).apply()
                                return@withContext result
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                            val country = addr.countryName
                            val result = if (!city.isNullOrBlank() && !country.isNullOrBlank()) {
                                "$city, $country"
                            } else null

                            if (result != null) {
                                prefs.edit().putString("last_city", result).apply()
                                return@withContext result
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Geocoder service offline or unavailable
            }

            // Fallback to cached city name if coordinates match roughly
            val savedCity = prefs.getString("last_city", null)
            if (!savedCity.isNullOrBlank()) {
                return@withContext savedCity
            }

            return@withContext "%.2f°, %.2f°".format(Locale.US, latitude, longitude)
        }
    }
}
