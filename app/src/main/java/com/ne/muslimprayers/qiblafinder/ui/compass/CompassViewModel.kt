package com.ne.muslimprayers.qiblafinder.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.CompassSensorManager
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.core.utils.LocationUtils
import com.ne.muslimprayers.qiblafinder.domain.model.QiblaState
import com.ne.muslimprayers.qiblafinder.domain.usecase.qibla.GetQiblaDirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class CompassViewModel @Inject constructor(
    private val compassSensorManager: CompassSensorManager,
    private val locationHelper: LocationHelper,
    private val getQiblaDirectionUseCase: GetQiblaDirectionUseCase
) : ViewModel() {

    private val _qiblaState = MutableStateFlow(
        QiblaState(isSensorAvailable = compassSensorManager.hasCompassSensor())
    )
    val qiblaState: StateFlow<QiblaState> = _qiblaState.asStateFlow()

    private var compassJob: Job? = null
    private var locationJob: Job? = null

    init {
        initLocationAndCompass()
    }

    fun initLocationAndCompass() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            // Start listening to sensor immediately even while location resolves
            val cachedLoc = locationHelper.getLastSavedLocation()
            if (cachedLoc != null) {
                applyCoordinates(cachedLoc.latitude, cachedLoc.longitude, isFallback = false)
                startSensorCollection(cachedLoc.latitude, cachedLoc.longitude)
            } else {
                startSensorCollection(0.0, 0.0)
            }

            val currentLocation = locationHelper.getCurrentLocation()
            val effectiveLocation = currentLocation ?: locationHelper.getEffectiveLocation()
            val isRealUserLocation = currentLocation != null || locationHelper.getLastSavedLocation() != null

            val userLat = effectiveLocation.latitude
            val userLng = effectiveLocation.longitude

            applyCoordinates(userLat, userLng, isFallback = !isRealUserLocation)
            startSensorCollection(userLat, userLng)

            val cityName = if (isRealUserLocation) {
                locationHelper.getCityAndCountry(userLat, userLng)
            } else if (!locationHelper.hasLocationPermission()) {
                "Tap refresh to grant location permission"
            } else {
                locationHelper.getCityAndCountry(userLat, userLng)
            }

            _qiblaState.update {
                it.copy(
                    cityName = cityName,
                    isLocationAvailable = true
                )
            }

            // Also run use-case (emits local calculation immediately + API verification)
            getQiblaDirectionUseCase(userLat, userLng).collect { result ->
                if (result is Resource.Success && result.data != null) {
                    _qiblaState.update { state ->
                        val qBearing = result.data
                        val needleAngle = (qBearing - state.compassAzimuth + 360f) % 360f
                        val rel = ((qBearing - state.compassAzimuth + 540f) % 360f) - 180f
                        state.copy(
                            qiblaBearing = qBearing,
                            needleAngle = needleAngle,
                            isFacingQibla = abs(rel) < 5f
                        )
                    }
                }
            }
        }
    }

    private fun applyCoordinates(lat: Double, lng: Double, isFallback: Boolean) {
        val localBearing = LocationUtils.calculateQiblaBearing(lat, lng)
        val distanceKm = LocationUtils.calculateDistanceToKaabaKm(lat, lng)
        _qiblaState.update { state ->
            val needleAngle = (localBearing - state.compassAzimuth + 360f) % 360f
            val rel = ((localBearing - state.compassAzimuth + 540f) % 360f) - 180f
            state.copy(
                userLatitude = lat,
                userLongitude = lng,
                qiblaBearing = localBearing,
                distanceToKaabaKm = distanceKm,
                needleAngle = needleAngle,
                isFacingQibla = abs(rel) < 5f,
                isLocationAvailable = !isFallback || state.isLocationAvailable
            )
        }
    }

    private fun startSensorCollection(latitude: Double, longitude: Double) {
        compassJob?.cancel()
        compassJob = viewModelScope.launch {
            compassSensorManager.getCompassReadingFlow(latitude, longitude).collect { reading ->
                val azimuth = reading.azimuthTrueNorth
                val qBearing = _qiblaState.value.qiblaBearing
                val needleAngle = (qBearing - azimuth + 360f) % 360f
                val rel = ((qBearing - azimuth + 540f) % 360f) - 180f
                val isFacing = abs(rel) < 5f

                _qiblaState.update {
                    it.copy(
                        compassAzimuth = azimuth,
                        needleAngle = needleAngle,
                        isFacingQibla = isFacing,
                        sensorAccuracy = reading.accuracy,
                        isSensorAvailable = reading.isSensorAvailable,
                        tiltDegrees = reading.tiltDegrees
                    )
                }
            }
        }
    }
}
