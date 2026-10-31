package com.ne.muslimprayers.qiblafinder.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.sdp

import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced

@Composable
fun IslamicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PureAlabaster,
    borderColor: Color = DividerColor,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickableDebounced { onClick() }
    } else {
        modifier
    }

    Surface(
        modifier = clickableModifier
            .clip(RoundedCornerShape(18.sdp))
            .border(
                width = 1.sdp,
                color = borderColor,
                shape = RoundedCornerShape(18.sdp)
            ),
        shape = RoundedCornerShape(18.sdp),
        color = backgroundColor,
        tonalElevation = 1.sdp
    ) {
        Box(modifier = Modifier.padding(16.sdp)) {
            content()
        }
    }
}
