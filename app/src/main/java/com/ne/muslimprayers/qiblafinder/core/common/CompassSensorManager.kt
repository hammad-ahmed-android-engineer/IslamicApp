package com.ne.muslimprayers.qiblafinder.core.common

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.hypot

data class CompassReading(
    val azimuthTrueNorth: Float,
    val accuracy: Int,
    val tiltDegrees: Float = 0f,
    val isSensorAvailable: Boolean = true
)

/**
 * Sensor engine.
 * - Uses hardware-fused [Sensor.TYPE_ROTATION_VECTOR] (or [Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR])
 *   with [Sensor.TYPE_MAGNETIC_FIELD] for calibration accuracy tracking, and automatically falls back
 *   to low-pass filtered [Sensor.TYPE_ACCELEROMETER] + [Sensor.TYPE_MAGNETIC_FIELD] sensor fusion.
 * - Remaps axes for display rotation, applies [GeomagneticField] declination for True North,
 *   computes device tilt angle, and applies shortest-path circular smoothing.
 */
@Singleton
class CompassSensorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    companion object {
        private const val LOW_PASS_ALPHA = 0.15f
    }

    fun hasCompassSensor(): Boolean {
        val sm = sensorManager ?: return false
        return sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null ||
                sm.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) != null ||
                (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null &&
                        sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
    }

    fun getAzimuthFlow(
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): Flow<Float> = getCompassReadingFlow(latitude, longitude).map { it.azimuthTrueNorth }

    fun getCompassReadingFlow(
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): Flow<CompassReading> = callbackFlow {
        val sm = sensorManager
        if (sm == null || !hasCompassSensor()) {
            trySend(CompassReading(azimuthTrueNorth = 0f, accuracy = 0, isSensorAvailable = false))
            close()
            return@callbackFlow
        }

        val rotationVectorSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: sm.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
        val accelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val hasRotationVector = rotationVectorSensor != null

        // Calculate magnetic declination to convert Magnetic North -> True North
        val declinationDeg: Float = if (latitude != 0.0 || longitude != 0.0) {
            try {
                GeomagneticField(
                    latitude.toFloat(),
                    longitude.toFloat(),
                    0f,
                    System.currentTimeMillis()
                ).declination
            } catch (_: Exception) {
                0f
            }
        } else {
            0f
        }

        val rotationMatrix = FloatArray(9)
        val remappedMatrix = FloatArray(9)
        val inclinationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val accelerometerReading = FloatArray(3)
        val magnetometerReading = FloatArray(3)
        var hasAccelerometer = false
        var hasMagnetometer = false

        var smoothedAzimuth = Float.NaN
        var smoothedTilt = 0f
        var currentAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH

        fun lowPassFilter(input: FloatArray, output: FloatArray, initialized: Boolean) {
            if (!initialized) {
                output[0] = input[0]
                output[1] = input[1]
                output[2] = input[2]
            } else {
                output[0] = output[0] + LOW_PASS_ALPHA * (input[0] - output[0])
                output[1] = output[1] + LOW_PASS_ALPHA * (input[1] - output[1])
                output[2] = output[2] + LOW_PASS_ALPHA * (input[2] - output[2])
            }
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                var matrixReady = false

                when (event.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR,
                    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        matrixReady = true
                    }

                    Sensor.TYPE_ACCELEROMETER -> {
                        if (!hasRotationVector) {
                            lowPassFilter(event.values, accelerometerReading, hasAccelerometer)
                            hasAccelerometer = true
                            if (hasMagnetometer) {
                                matrixReady = SensorManager.getRotationMatrix(
                                    rotationMatrix,
                                    inclinationMatrix,
                                    accelerometerReading,
                                    magnetometerReading
                                )
                            }
                        }
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        if (event.accuracy >= 0) {
                            currentAccuracy = event.accuracy
                        }
                        if (!hasRotationVector) {
                            lowPassFilter(event.values, magnetometerReading, hasMagnetometer)
                            hasMagnetometer = true
                            if (hasAccelerometer) {
                                matrixReady = SensorManager.getRotationMatrix(
                                    rotationMatrix,
                                    inclinationMatrix,
                                    accelerometerReading,
                                    magnetometerReading
                                )
                            }
                        }
                    }
                }

                if (!matrixReady) return

                // Remap coordinate system for current screen rotation (Portrait / Landscape)
                val displayRotation = getDisplayRotation()
                val activeMatrix = if (displayRotation != Surface.ROTATION_0) {
                    val (axisX, axisY) = when (displayRotation) {
                        Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                        Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                        Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                        else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                    }
                    if (SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, remappedMatrix)) {
                        remappedMatrix
                    } else {
                        rotationMatrix
                    }
                } else {
                    rotationMatrix
                }

                // Extract [azimuth, pitch, roll] from orientation matrix
                SensorManager.getOrientation(activeMatrix, orientationAngles)
                val rawMagneticAzimuthDeg = ((Math.toDegrees(orientationAngles[0].toDouble()).toFloat() + 360f) % 360f)
                val pitchDeg = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                val rollDeg = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
                val rawTilt = hypot(pitchDeg, rollDeg)
                smoothedTilt = smoothedTilt + 0.2f * (rawTilt - smoothedTilt)

                // Convert Magnetic North to True North using local geomagnetic declination
                val rawTrueAzimuth = (rawMagneticAzimuthDeg + declinationDeg + 360f) % 360f

                // Shortest-path circular smoothing (prevents 359° -> 1° jump through 180°)
                smoothedAzimuth = if (smoothedAzimuth.isNaN()) {
                    rawTrueAzimuth
                } else {
                    val delta = ((rawTrueAzimuth - smoothedAzimuth + 540f) % 360f) - 180f
                    val smoothingGain = if (abs(delta) > 12f) 0.28f else 0.16f
                    (smoothedAzimuth + smoothingGain * delta + 360f) % 360f
                }

                trySend(
                    CompassReading(
                        azimuthTrueNorth = smoothedAzimuth,
                        accuracy = currentAccuracy,
                        tiltDegrees = smoothedTilt,
                        isSensorAvailable = true
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD ||
                    sensor?.type == Sensor.TYPE_ROTATION_VECTOR
                ) {
                    currentAccuracy = accuracy
                }
            }
        }

        if (hasRotationVector && rotationVectorSensor != null) {
            sm.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME)
            if (magSensor != null) {
                sm.registerListener(listener, magSensor, SensorManager.SENSOR_DELAY_UI)
            }
        } else {
            if (accelSensor != null) {
                sm.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_GAME)
            }
            if (magSensor != null) {
                sm.registerListener(listener, magSensor, SensorManager.SENSOR_DELAY_GAME)
            }
        }

        awaitClose {
            sm.unregisterListener(listener)
        }
    }

    @Suppress("DEPRECATION")
    private fun getDisplayRotation(): Int {
        return try {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            wm?.defaultDisplay?.rotation ?: Surface.ROTATION_0
        } catch (_: Exception) {
            Surface.ROTATION_0
        }
    }
}
