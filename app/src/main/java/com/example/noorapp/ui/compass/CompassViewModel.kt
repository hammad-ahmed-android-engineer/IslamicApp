package com.example.noorapp.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.core.common.Resource
import com.example.noorapp.core.utils.LocationUtils
import com.example.noorapp.domain.model.QiblaState
import com.example.noorapp.domain.usecase.location.GetCurrentLocationUseCase
import com.example.noorapp.domain.usecase.qibla.GetQiblaDirectionUseCase
import com.example.noorapp.domain.usecase.qibla.ObserveCompassReadingUseCase
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
    private val observeCompassReadingUseCase: ObserveCompassReadingUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getQiblaDirectionUseCase: GetQiblaDirectionUseCase
) : ViewModel() {

    private val _qiblaState = MutableStateFlow(
        QiblaState(isSensorAvailable = observeCompassReadingUseCase.hasCompassSensor())
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
            val cachedCoords = getCurrentLocationUseCase.getLastSavedCoordinates()
            if (cachedCoords != null) {
                applyCoordinates(cachedCoords.first, cachedCoords.second, isFallback = false)
                startSensorCollection(cachedCoords.first, cachedCoords.second)
            } else {
                startSensorCollection(0.0, 0.0)
            }

            val resolvedLocation = getCurrentLocationUseCase(includeCityName = true)
            val userLat = resolvedLocation.latitude
            val userLng = resolvedLocation.longitude

            applyCoordinates(userLat, userLng, isFallback = resolvedLocation.isFallback)
            startSensorCollection(userLat, userLng)

            _qiblaState.update {
                it.copy(
                    cityName = resolvedLocation.cityName,
                    isLocationAvailable = true,
                    hasLocationPermission = resolvedLocation.hasPermission
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
            observeCompassReadingUseCase(latitude, longitude).collect { reading ->
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
