package com.ne.muslimprayers.qiblafinder.ui.compass

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.GoldPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.SuccessGreen
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.utils.LocationUtils
import com.ne.muslimprayers.qiblafinder.ui.compass.components.CalibrationNotice
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
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.initLocationAndCompass()
    }

    val handleRefresh: () -> Unit = {
        if (!LocationUtils.hasLocationPermission(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.initLocationAndCompass()
        }
    }

    // Calculate relative angle to Qibla in [-180, 180]: 0° = perfectly aligned with top of phone
    val relativeAngle = ((state.qiblaBearing - state.compassAzimuth + 540f) % 360f) - 180f
    val isFacingQibla = abs(relativeAngle) < 5f && state.isLocationAvailable

    // Trigger haptic feedback when aligned with the Kaaba
    LaunchedEffect(isFacingQibla) {
        if (isFacingQibla) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Unwrap dial target rotation so crossing 0°/360° smoothly rotates by shortest path
    var continuousDialTarget by remember { mutableFloatStateOf(-state.compassAzimuth) }
    LaunchedEffect(state.compassAzimuth) {
        val desired = -state.compassAzimuth
        val delta = ((desired - continuousDialTarget + 540f) % 360f) - 180f
        continuousDialTarget += delta
    }

    val animatedDialRotation by animateFloatAsState(
        targetValue = continuousDialTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dial_rotation"
    )

    val statusBorderColor by animateColorAsState(
        targetValue = if (isFacingQibla) SuccessGreen else GoldPrimary.copy(alpha = 0.5f),
        label = "status_color"
    )

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = stringResource(R.string.compass_screen_title),
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = handleRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh_location),
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        horizontal = AppDimensions.paddingMedium,
                        vertical = AppDimensions.paddingSmall
                    ),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        size = 220
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    StatusBanner(
                        isFacingQibla = isFacingQibla,
                        relativeAngle = relativeAngle,
                        qiblaBearing = state.qiblaBearing,
                        statusBorderColor = statusBorderColor
                    )
                    ReadingsCard(state = state, isFacingQibla = isFacingQibla)
                    CalibrationNotice(
                        sensorAccuracy = state.sensorAccuracy,
                        isSensorAvailable = state.isSensorAvailable
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(AppDimensions.paddingMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StatusBanner(
                    isFacingQibla = isFacingQibla,
                    relativeAngle = relativeAngle,
                    qiblaBearing = state.qiblaBearing,
                    statusBorderColor = statusBorderColor
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

                QiblaCompassVisualizer(
                    state = state,
                    isFacingQibla = isFacingQibla,
                    animatedDialRotation = animatedDialRotation,
                    statusBorderColor = statusBorderColor,
                    size = 280
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

                ReadingsCard(state = state, isFacingQibla = isFacingQibla)

                Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))

                CalibrationNotice(
                    sensorAccuracy = state.sensorAccuracy,
                    isSensorAvailable = state.isSensorAvailable
                )
            }
        }
    }
}
