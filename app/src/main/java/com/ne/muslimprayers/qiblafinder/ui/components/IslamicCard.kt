package com.ne.muslimprayers.qiblafinder.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced

@Composable
fun IslamicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = PureAlabaster,
    borderColor: Color = DividerColor,
    borderWidth: Dp = AppDimensions.borderWidthThin,
    cornerRadius: Dp = AppDimensions.cornerExtraLarge,
    contentPadding: Dp = AppDimensions.paddingMedium,
    elevation: Dp = AppDimensions.elevationLow,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickableDebounced { onClick() }
    } else {
        modifier
    }

    val shape = RoundedCornerShape(cornerRadius)

    Surface(
        modifier = clickableModifier
            .clip(shape)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            ),
        shape = shape,
        color = backgroundColor,
        tonalElevation = elevation
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
