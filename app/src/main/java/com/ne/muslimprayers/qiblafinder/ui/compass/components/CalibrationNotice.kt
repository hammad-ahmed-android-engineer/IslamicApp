package com.ne.muslimprayers.qiblafinder.ui.compass.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun CalibrationNotice() {
    AppText.BodySmall(
        text = "Tip: Wave your device in a Figure-8 pattern if compass sensor needs calibration.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
