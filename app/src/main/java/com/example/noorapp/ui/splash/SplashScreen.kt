package com.example.noorapp.ui.splash

import android.content.res.Configuration
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.common.PermissionPreferencesManager
import com.example.noorapp.core.designsystem.AppCircularProgress
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.rememberAppPermissionHandler
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    var isStarted by rememberSaveable { mutableStateOf(false) }
    var hasRequestedPermissions by rememberSaveable { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val startupPermissions = remember { PermissionPreferencesManager.startupPermissions() }
    val permissionHandler = rememberAppPermissionHandler(
        permissionKey = PermissionPreferencesManager.KEY_STARTUP,
        permissions = startupPermissions,
        isGranted = { PermissionPreferencesManager.hasAllStartupPermissions(context) },
        dialogTitle = stringResource(R.string.perm_startup_title),
        dialogMessage = stringResource(R.string.perm_startup_message),
        onGranted = onNavigateToHome,
        onDismissWarning = onNavigateToHome,
        onReturnedFromSettingsDenied = onNavigateToHome
    )

    LaunchedEffect(Unit) {
        isStarted = true
        delay(1800)
        if (PermissionPreferencesManager.hasAllStartupPermissions(context)) {
            PermissionPreferencesManager.resetDenialCount(
                context,
                PermissionPreferencesManager.KEY_STARTUP
            )
            onNavigateToHome()
        } else if (!hasRequestedPermissions) {
            hasRequestedPermissions = true
            if (permissionHandler.hasReachedSettingsThreshold()) {
                onNavigateToHome()
            } else {
                permissionHandler.request()
            }
        }
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
                            painter = painterResource(id = R.drawable.noor_splash_icon),
                            contentDescription = stringResource(R.string.splash_app_emblem_desc),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                    AppText.ArabicText(
                        text = stringResource(R.string.splash_bismillah),
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
                        text = stringResource(R.string.splash_title),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                    AppText.BodyMedium(
                        text = stringResource(R.string.splash_subtitle_landscape),
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

                    AppCircularProgress(
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
                        painter = painterResource(id = R.drawable.noor_splash_icon),
                        contentDescription = stringResource(R.string.splash_app_emblem_desc),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingLarge))

                // Bismillah Calligraphy via AppText custom view
                AppText.ArabicText(
                    text = stringResource(R.string.splash_bismillah),
                    fontSize = AppDimensions.textBismillahBanner,
                    fontWeight = FontWeight.Bold,
                    color = DesertOchre,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    applySafePadding = false
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                AppText.DisplayLarge(
                    text = stringResource(R.string.splash_title),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                AppStatusPill(
                    text = stringResource(R.string.splash_tagline_pill),
                    textColor = MedinaSage,
                    backgroundColor = MedinaSageLight,
                    cornerRadius = AppDimensions.cornerMedium
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                AppText.BodyMedium(
                    text = stringResource(R.string.splash_subtitle_compact),
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppDimensions.paddingMedium)
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraLarge))

                AppCircularProgress(
                    color = MedinaSage,
                    strokeWidth = AppDimensions.borderWidthThick,
                    modifier = Modifier.size(AppDimensions.loaderSize)
                )
            }
        }
    }
}
