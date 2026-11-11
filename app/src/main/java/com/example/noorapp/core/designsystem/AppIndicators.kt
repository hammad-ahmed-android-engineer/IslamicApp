package com.example.noorapp.core.designsystem

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.WarmParchment

/**
 * Standardized horizontal hairline divider.
 */
@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = AppDimensions.dividerHairline,
    color: Color = DividerColor
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

/**
 * Standardized vertical hairline divider.
 */
@Composable
fun AppVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = AppDimensions.borderWidthThin,
    color: Color = DividerColor
) {
    VerticalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

/**
 * Standardized horizontal progress indicator with rounded stroke caps.
 */
@Composable
fun AppLinearProgress(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = MedinaSage,
    trackColor: Color = WarmParchment
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier,
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round
    )
}

/**
 * Standardized indeterminate circular loading spinner.
 */
@Composable
fun AppCircularProgress(
    modifier: Modifier = Modifier,
    color: Color = MedinaSage,
    strokeWidth: Dp = AppDimensions.borderWidthThick
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth
    )
}

/**
 * Standardized slider styled with theme thumb and track colors.
 */
@Composable
fun AppSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    thumbColor: Color = MedinaSage,
    activeTrackColor: Color = MedinaSage,
    inactiveTrackColor: Color = WarmParchment
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            inactiveTrackColor = inactiveTrackColor
        )
    )
}

