package com.ne.muslimprayers.qiblafinder.ui.compass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.CompassSensorManager
import com.ne.muslimprayers.qiblafinder.core.common.LocationHelper
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.QiblaState
import com.ne.muslimprayers.qiblafinder.domain.usecase.qibla.GetQiblaDirectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _qiblaState = MutableStateFlow(QiblaState())
    val qiblaState: StateFlow<QiblaState> = _qiblaState.asStateFlow()

    init {
        initLocationAndCompass()
    }

    fun initLocationAndCompass() {
        viewModelScope.launch {
            val location = locationHelper.getCurrentLocation()
            if (location == null) {
                _qiblaState.update {
                    it.copy(
                        isLocationAvailable = false,
                        cityName = "Location permission required"
                    )
                }
                return@launch
            }

            val userLat = location.latitude
            val userLng = location.longitude
            val cityName = locationHelper.getCityAndCountry(userLat, userLng)

            _qiblaState.update {
                it.copy(
                    userLatitude = userLat,
                    userLongitude = userLng,
                    cityName = cityName,
                    isLocationAvailable = true
                )
            }

            // Fetch live Qibla direction from API
            launch {
                getQiblaDirectionUseCase(userLat, userLng).collect { result ->
                    if (result is Resource.Success && result.data != null) {
                        _qiblaState.update { it.copy(qiblaBearing = result.data) }
                    }
                }
            }

            // Listen to device compass azimuth flow
            launch {
                compassSensorManager.getAzimuthFlow().collect { azimuth ->
                    val qBearing = _qiblaState.value.qiblaBearing
                    val needleAngle = (qBearing - azimuth + 360f) % 360f
                    val diff = abs(azimuth - qBearing)
                    val isFacing = diff < 4f || diff > 356f

                    _qiblaState.update {
                        it.copy(
                            compassAzimuth = azimuth,
                            needleAngle = needleAngle,
                            isFacingQibla = isFacing
                        )
                    }
                }
            }
        }
    }
}
