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
import kotlin.math.atan2
import kotlin.math.sqrt

data class CompassReading(
    val azimuthTrueNorth: Float,
    val accuracy: Int,
    val isSensorAvailable: Boolean = true
)

@Singleton
class CompassSensorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

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
        val orientation = FloatArray(3)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        var smoothedAzimuth = Float.NaN
        var currentAccuracy = SensorManager.SENSOR_STATUS_ACCURACY_HIGH

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
                        val filterFactor = if (hasGravity) 0.85f else 0f
                        gravity[0] = filterFactor * gravity[0] + (1f - filterFactor) * event.values[0]
                        gravity[1] = filterFactor * gravity[1] + (1f - filterFactor) * event.values[1]
                        gravity[2] = filterFactor * gravity[2] + (1f - filterFactor) * event.values[2]
                        hasGravity = true
                        if (hasGeomagnetic && rotationVectorSensor == null) {
                            matrixReady = SensorManager.getRotationMatrix(
                                rotationMatrix,
                                inclinationMatrix,
                                gravity,
                                geomagnetic
                            )
                        }
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        if (event.accuracy > 0) {
                            currentAccuracy = event.accuracy
                        }
                        val filterFactor = if (hasGeomagnetic) 0.85f else 0f
                        geomagnetic[0] = filterFactor * geomagnetic[0] + (1f - filterFactor) * event.values[0]
                        geomagnetic[1] = filterFactor * geomagnetic[1] + (1f - filterFactor) * event.values[1]
                        geomagnetic[2] = filterFactor * geomagnetic[2] + (1f - filterFactor) * event.values[2]
                        hasGeomagnetic = true
                        if (hasGravity && rotationVectorSensor == null) {
                            matrixReady = SensorManager.getRotationMatrix(
                                rotationMatrix,
                                inclinationMatrix,
                                gravity,
                                geomagnetic
                            )
                        }
                    }
                }

                if (!matrixReady) return

                // Adjust coordinate axes for current screen orientation (Portrait / Landscape)
                val displayRotation = getDisplayRotation()
                val (axisX, axisY) = when (displayRotation) {
                    Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                    Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                    else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                }

                val remapSuccess = SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    axisX,
                    axisY,
                    remappedMatrix
                )
                val activeMatrix = if (remapSuccess) remappedMatrix else rotationMatrix

                // Compute azimuth reliably both when flat and when tilted upright
                // Standard getOrientation uses atan2(R[1], R[4]), which can gimbal-lock near 90° pitch.
                val r7 = activeMatrix[7] // sin(pitch)
                val rawMagneticAzimuthDeg: Float = if (abs(r7) > 0.75f) {
                    // Phone is held steeply tilted (> ~48 degrees): project device Z-axis onto horizontal plane
                    val east = activeMatrix[2].toDouble()
                    val north = activeMatrix[5].toDouble()
                    val norm = sqrt(east * east + north * north)
                    if (norm > 1e-4) {
                        Math.toDegrees(atan2(-east, -north)).toFloat()
                    } else {
                        SensorManager.getOrientation(activeMatrix, orientation)
                        Math.toDegrees(orientation[0].toDouble()).toFloat()
                    }
                } else {
                    SensorManager.getOrientation(activeMatrix, orientation)
                    Math.toDegrees(orientation[0].toDouble()).toFloat()
                }

                // Convert Magnetic North to True North using local geomagnetic declination
                val rawTrueAzimuth = (rawMagneticAzimuthDeg + declinationDeg + 360f) % 360f

                // Circular shortest-path low-pass smoothing (prevents 359° -> 1° jump through 180°)
                smoothedAzimuth = if (smoothedAzimuth.isNaN()) {
                    rawTrueAzimuth
                } else {
                    val delta = ((rawTrueAzimuth - smoothedAzimuth + 540f) % 360f) - 180f
                    // Adaptive smoothing: faster response for deliberate turns, smoother when steady
                    val smoothingGain = if (abs(delta) > 15f) 0.28f else 0.14f
                    (smoothedAzimuth + smoothingGain * delta + 360f) % 360f
                }

                trySend(
                    CompassReading(
                        azimuthTrueNorth = smoothedAzimuth,
                        accuracy = currentAccuracy,
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

        if (rotationVectorSensor != null) {
            sm.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME)
            // Also register magnetic field sensor solely to track calibration accuracy
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
