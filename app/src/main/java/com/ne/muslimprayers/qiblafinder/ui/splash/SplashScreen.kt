package com.ne.muslimprayers.qiblafinder.ui.splash

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    var isStarted by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val permissionsList = remember {
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = permissionsList)

    LaunchedEffect(Unit) {
        isStarted = true
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
        delay(1800)
        onNavigateToHome()
    }

    val scale by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0.85f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "content_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmParchment),
        contentAlignment = Alignment.Center
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimensions.paddingExtraLarge, vertical = AppDimensions.paddingMedium)
                    .alpha(contentAlpha),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraLarge)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Outer soft halo
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.splashHaloSizeCompact)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        DesertOchre.copy(alpha = 0.25f),
                                        MedinaSageLight.copy(alpha = 0.3f),
                                        PureAlabaster
                                    )
                                )
                            )
                            .border(AppDimensions.borderWidthThick, DesertOchre, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.icon),
                            contentDescription = "App Emblem",
                            modifier = Modifier
                                .size(AppDimensions.splashEmblemSizeCompact)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                    AppText.ArabicText(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontSize = AppDimensions.textHeadingMedium,
                        fontWeight = FontWeight.Bold,
                        color = DesertOchre,
                        textAlign = TextAlign.Center,
                        applySafePadding = false
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppText.DisplayLarge(
                        text = "Muslim Prayers & Qibla",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                    AppText.BodyMedium(
                        text = "Your daily spiritual sanctuary for prayer timings, noble Quran, and accurate Qibla directions.",
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

                    CircularProgressIndicator(
                        color = MedinaSage,
                        strokeWidth = AppDimensions.borderWidthThick,
                        modifier = Modifier.size(AppDimensions.loaderSize)
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(AppDimensions.paddingLarge)
                    .alpha(contentAlpha)
            ) {
                // Luxury emblem container with halo
                Box(
                    modifier = Modifier
                        .size(AppDimensions.splashHaloSize)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    DesertOchre.copy(alpha = 0.28f),
                                    MedinaSageLight.copy(alpha = 0.35f),
                                    PureAlabaster
                                )
                            )
                        )
                        .border(AppDimensions.borderWidthThick, DesertOchre, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.icon),
                        contentDescription = "App Emblem",
                        modifier = Modifier
                            .size(AppDimensions.splashEmblemSize)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

                // Bismillah Calligraphy via AppText custom view
                AppText.ArabicText(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = AppDimensions.textBismillahBanner,
                    fontWeight = FontWeight.Bold,
                    color = DesertOchre,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    applySafePadding = false
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                AppText.DisplayLarge(
                    text = "Muslim Prayers & Qibla",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                        .background(MedinaSageLight)
                        .padding(horizontal = AppDimensions.paddingMediumSmall, vertical = AppDimensions.paddingTiny)
                ) {
                    AppText.Caption(
                        text = "Your Spiritual Daily Companion",
                        color = MedinaSage,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                AppText.BodyMedium(
                    text = "Accurate Qibla directions, prayer notifications, Holy Quran and spiritual remembrance.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppDimensions.paddingMedium)
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraLarge))

                CircularProgressIndicator(
                    color = MedinaSage,
                    strokeWidth = AppDimensions.borderWidthThick,
                    modifier = Modifier.size(AppDimensions.loaderSize)
                )
            }
        }
    }
}
