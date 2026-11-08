package com.ne.muslimprayers.qiblafinder.ui.compass.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.SuccessGreen
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.QiblaState

@Composable
fun QiblaCompassVisualizer(
    state: QiblaState,
    isFacingQibla: Boolean,
    animatedDialRotation: Float,
    statusBorderColor: Color,
    size: Int
) {
    Box(
        modifier = Modifier.size((size + 28).sdp),
        contentAlignment = Alignment.Center
    ) {
        // Main Circular Compass Dial
        Box(
            modifier = Modifier
                .size(size.sdp)
                .shadow(AppDimensions.elevationHigh, CircleShape)
                .clip(CircleShape)
                .background(PureAlabaster)
                .border(4.sdp, statusBorderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Compass Rose (North on dial rotates by -azimuth)
            Image(
                painter = painterResource(id = R.drawable.compass_base),
                contentDescription = "Compass Base Dial",
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .rotate(animatedDialRotation)
            )

            // Rotating Qibla Pointer (points toward Kaaba at qiblaBearing relative to True North on dial)
            Image(
                painter = painterResource(id = R.drawable.compass_needle),
                contentDescription = "Qibla Direction Pointer",
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .rotate(animatedDialRotation + state.qiblaBearing)
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

        // Fixed Kaaba Image at Top (12-o'clock) showing Kaaba/Qibla alignment target
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(44.sdp)
                .shadow(AppDimensions.elevationMedium, CircleShape)
                .clip(CircleShape)
                .background(PureAlabaster)
                .border(
                    width = AppDimensions.borderWidthThick,
                    color = if (isFacingQibla) SuccessGreen else DesertOchre,
                    shape = CircleShape
                )
                .padding(3.sdp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_kaaba),
                contentDescription = "Kaaba Qibla Direction",
                modifier = Modifier.size(36.sdp)
            )
        }
    }
}
