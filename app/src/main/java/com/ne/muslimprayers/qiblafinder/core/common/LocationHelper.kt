package com.ne.muslimprayers.qiblafinder.core.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.ne.muslimprayers.qiblafinder.core.utils.LocationUtils
import com.ne.muslimprayers.qiblafinder.core.utils.ValidationUtils
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
    fun hasLocationPermission(): Boolean = LocationUtils.hasLocationPermission(context)

    fun isLocationEnabled(): Boolean = LocationUtils.isLocationEnabled(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null

        return withContext(Dispatchers.IO) {
            try {
                // 1. Check recent last known location from FusedLocationProvider
                val lastLocation = fusedLocationProviderClient.lastLocation.await()
                if (lastLocation != null && isLocationRecent(lastLocation)) {
                    return@withContext lastLocation
                }

                // 2. Request fresh high-accuracy location with timeout
                val cts = CancellationTokenSource()
                val freshLocation = withTimeoutOrNull(6000L) {
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cts.token
                    ).await()
                }
                if (freshLocation != null) {
                    return@withContext freshLocation
                }

                // 3. Fallback to older lastLocation if available
                if (lastLocation != null) {
                    return@withContext lastLocation
                }

                // 4. Fallback to native LocationManager providers
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                if (locationManager != null) {
                    val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (gpsLoc != null) return@withContext gpsLoc

                    val networkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (networkLoc != null) return@withContext networkLoc

                    val passiveLoc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    if (passiveLoc != null) return@withContext passiveLoc
                }

                null
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun isLocationRecent(location: Location): Boolean {
        val maxAgeMs = 15 * 60 * 1000L // 15 minutes
        val locationTime = location.time
        return (System.currentTimeMillis() - locationTime) < maxAgeMs
    }

    suspend fun getCityAndCountry(latitude: Double, longitude: Double): String {
        if (!ValidationUtils.isValidCoordinates(latitude, longitude)) {
            return "Unknown Location"
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
                            if (!city.isNullOrBlank() && !country.isNullOrBlank()) {
                                return@withContext "$city, $country"
                            } else if (!city.isNullOrBlank()) {
                                return@withContext city
                            } else if (!country.isNullOrBlank()) {
                                return@withContext country
                            }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                            val country = addr.countryName
                            if (!city.isNullOrBlank() && !country.isNullOrBlank()) {
                                return@withContext "$city, $country"
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Geocoder service may be unavailable on some devices
            }

            return@withContext "%.2f°, %.2f°".format(Locale.US, latitude, longitude)
        }
    }
}
