package com.ne.muslimprayers.qiblafinder.core.common

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompassSensorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    fun getAzimuthFlow(): Flow<Float> = callbackFlow {
        if (sensorManager == null) {
            close()
            return@callbackFlow
        }

        val gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val mGravity = FloatArray(3)
        val mGeomagnetic = FloatArray(3)
        val r = FloatArray(9)
        val i = FloatArray(9)
        val orientation = FloatArray(3)
        val alpha = 0.95f

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                        mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
                        mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
                        mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
                        mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
                    }
                }

                val success = SensorManager.getRotationMatrix(r, i, mGravity, mGeomagnetic)
                if (success) {
                    SensorManager.getOrientation(r, orientation)
                    val azimuthRad = orientation[0]
                    var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                    azimuthDeg = (azimuthDeg + 360f) % 360f
                    trySend(azimuthDeg)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, gsensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, msensor, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
