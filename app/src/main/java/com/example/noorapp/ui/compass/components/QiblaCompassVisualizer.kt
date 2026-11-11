package com.example.noorapp.ui.compass.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.noorapp.R
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.SuccessGreen
import com.example.noorapp.domain.model.QiblaState

@Composable
fun QiblaCompassVisualizer(
    state: QiblaState,
    isFacingQibla: Boolean,
    animatedDialRotation: Float,
    animatedNeedleRotation: Float = animatedDialRotation + state.qiblaBearing,
    statusBorderColor: Color,
    size: androidx.compose.ui.unit.Dp = AppDimensions.compassDialSize
) {
    // Outer container reserves room for the fixed 12-o'clock reference notch and orbiting Kaaba badge
    Box(
        modifier = Modifier.size(size + AppDimensions.compassKaabaBadgeSize + AppDimensions.paddingMedium),
        contentAlignment = Alignment.Center
    ) {
        // Fixed top phone-heading alignment notch (12 o'clock reference showing phone front direction)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = AppDimensions.paddingExtraSmall, height = AppDimensions.paddingSmall)
                .clip(RoundedCornerShape(AppDimensions.cornerMicro))
                .background(if (isFacingQibla) SuccessGreen else DesertOchre)
        )

        // Main Circular Compass Dial
        Box(
            modifier = Modifier
                .size(size)
                .shadow(AppDimensions.elevationHigh, CircleShape)
                .clip(CircleShape)
                .background(PureAlabaster)
                .border(AppDimensions.borderWidthBold, statusBorderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Compass Rose (North on dial rotates by -azimuth to track True North)
            Image(
                painter = painterResource(id = R.drawable.compass_base),
                contentDescription = stringResource(R.string.compass_base_dial_desc),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .rotate(animatedDialRotation)
            )

            // Center Direction Hub
            Box(
                modifier = Modifier
                    .size(AppDimensions.avatarSize)
                    .clip(CircleShape)
                    .background(MedinaSageLight)
                    .border(
                        AppDimensions.borderWidthThick,
                        if (isFacingQibla) SuccessGreen else DesertOchre,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = if (isFacingQibla) SuccessGreen else MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }
        }

        // Rotating Qibla Pointer + Orbiting Kaaba Icon:
        // Both rotate smoothly around the dial center at (qiblaBearing - compassAzimuth),
        // so the Kaaba icon moves directly along the Qibla pointing direction and meets
        // the top 12-o'clock indicator when the phone faces Makkah.
        Box(
            modifier = Modifier
                .size(size + AppDimensions.compassKaabaBadgeSize)
                .rotate(animatedNeedleRotation),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.compass_needle),
                contentDescription = stringResource(R.string.compass_pointer_desc),
                modifier = Modifier.size(size * 0.86f)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(AppDimensions.compassKaabaBadgeSize)
                    .shadow(AppDimensions.elevationMedium, CircleShape)
                    .clip(CircleShape)
                    .background(PureAlabaster)
                    .border(
                        width = AppDimensions.borderWidthThick,
                        color = if (isFacingQibla) SuccessGreen else DesertOchre,
                        shape = CircleShape
                    )
                    .padding(AppDimensions.paddingTiny),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_kaaba),
                    contentDescription = stringResource(R.string.compass_kaaba_direction_desc),
                    modifier = Modifier.size(AppDimensions.compassKaabaIconSize)
                )
            }
        }
    }
}
