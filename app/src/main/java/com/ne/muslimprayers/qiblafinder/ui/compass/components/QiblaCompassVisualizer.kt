package com.ne.muslimprayers.qiblafinder.ui.compass.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
        modifier = Modifier
            .size(size.sdp)
            .shadow(12.sdp, CircleShape)
            .clip(CircleShape)
            .background(PureAlabaster)
            .border(3.5.sdp, statusBorderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.compass_base),
            contentDescription = "Compass Base Dial",
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .rotate(animatedDialRotation)
        )

        Image(
            painter = painterResource(id = R.drawable.compass_needle),
            contentDescription = "Qibla Direction Pointer",
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .rotate(animatedDialRotation + state.qiblaBearing)
        )

        Box(
            modifier = Modifier
                .size(40.sdp)
                .clip(CircleShape)
                .background(MedinaSageLight)
                .border(1.8.sdp, DesertOchre, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = null,
                tint = if (isFacingQibla) SuccessGreen else MedinaSage,
                modifier = Modifier.size(22.sdp)
            )
        }
    }
}
