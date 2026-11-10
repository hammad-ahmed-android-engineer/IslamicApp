package com.ne.muslimprayers.qiblafinder.ui.compass.components

import android.hardware.SensorManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun CalibrationNotice(
    sensorAccuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    isSensorAvailable: Boolean = true,
    tiltDegrees: Float = 0f
) {
    val isTilted = tiltDegrees > 45f
    val message = when {
        !isSensorAvailable -> stringResource(R.string.compass_sensor_unavailable)
        sensorAccuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW -> stringResource(R.string.compass_calibration_needed)
        isTilted -> "Hold your device flat (tilt: ${tiltDegrees.toInt()}°) for accurate Qibla reading"
        else -> stringResource(R.string.compass_keep_flat_hint)
    }

    AppText.BodySmall(
        text = message,
        color = if (!isSensorAvailable || sensorAccuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW || isTilted)
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
