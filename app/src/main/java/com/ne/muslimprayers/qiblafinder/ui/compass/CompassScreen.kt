package com.ne.muslimprayers.qiblafinder.ui.compass

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.GoldPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.SuccessGreen
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.compass.components.CalibrationNotice
import com.ne.muslimprayers.qiblafinder.ui.compass.components.LocationHeaderCard
import com.ne.muslimprayers.qiblafinder.ui.compass.components.QiblaCompassVisualizer
import com.ne.muslimprayers.qiblafinder.ui.compass.components.ReadingsCard
import com.ne.muslimprayers.qiblafinder.ui.compass.components.StatusBanner
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import kotlin.math.abs

@Composable
fun CompassScreen(
    viewModel: CompassViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.qiblaState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Calculate relative angle to Qibla: 0° = perfectly aligned with top
    val relativeAngle = ((state.qiblaBearing - state.compassAzimuth + 540f) % 360f) - 180f
    val isFacingQibla = abs(relativeAngle) < 4f && state.isLocationAvailable

    // Trigger haptic feedback when aligned
    LaunchedEffect(isFacingQibla) {
        if (isFacingQibla) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Dial rotates smoothly with the compass heading
    val animatedDialRotation by animateFloatAsState(
        targetValue = -state.compassAzimuth,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "dial_rotation"
    )

    val statusBorderColor by animateColorAsState(
        targetValue = if (isFacingQibla) SuccessGreen else GoldPrimary.copy(alpha = 0.5f),
        label = "status_color"
    )

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = "Qibla Finder (اتجاه القبلة)",
                onBackClick = onBackClick
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (isLandscape) {
            // Landscape Two-Column Dashboard Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.sdp, vertical = 8.sdp),
                horizontalArrangement = Arrangement.spacedBy(16.sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pane: Compass Visualizer
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    QiblaCompassVisualizer(
                        state = state,
                        isFacingQibla = isFacingQibla,
                        animatedDialRotation = animatedDialRotation,
                        statusBorderColor = statusBorderColor,
                        size = 230
                    )
                }

                // Right Pane: Info & Status
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.sdp)
                ) {
                    LocationHeaderCard(state = state, onRefresh = viewModel::initLocationAndCompass)
                    StatusBanner(
                        isFacingQibla = isFacingQibla,
                        relativeAngle = relativeAngle,
                        qiblaBearing = state.qiblaBearing,
                        statusBorderColor = statusBorderColor
                    )
                    ReadingsCard(state = state, isFacingQibla = isFacingQibla)
                    CalibrationNotice()
                }
            }
        } else {
            // Portrait View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.sdp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LocationHeaderCard(state = state, onRefresh = viewModel::initLocationAndCompass)

                Spacer(modifier = Modifier.height(12.sdp))

                StatusBanner(
                    isFacingQibla = isFacingQibla,
                    relativeAngle = relativeAngle,
                    qiblaBearing = state.qiblaBearing,
                    statusBorderColor = statusBorderColor
                )

                Spacer(modifier = Modifier.height(20.sdp))

                // Compass Visualizer with Fixed Qibla Point at 12 o'clock
                QiblaCompassVisualizer(
                    state = state,
                    isFacingQibla = isFacingQibla,
                    animatedDialRotation = animatedDialRotation,
                    statusBorderColor = statusBorderColor,
                    size = 290
                )

                Spacer(modifier = Modifier.height(20.sdp))

                ReadingsCard(state = state, isFacingQibla = isFacingQibla)

                Spacer(modifier = Modifier.height(14.sdp))

                CalibrationNotice()
            }
        }
    }
}
