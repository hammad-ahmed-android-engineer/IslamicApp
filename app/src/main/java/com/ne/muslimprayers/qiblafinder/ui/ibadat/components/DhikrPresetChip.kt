package com.ne.muslimprayers.qiblafinder.ui.ibadat.components

import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun DhikrPresetChip(
    title: String,
    arabic: String,
    target: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(AppDimensions.cornerLarge))
            .clickableDebounced { onClick() }
            .border(
                width = AppDimensions.borderWidthThin,
                color = if (isSelected) MedinaSage else DividerColor,
                shape = RoundedCornerShape(AppDimensions.cornerLarge)
            ),
        shape = RoundedCornerShape(AppDimensions.cornerLarge),
        color = if (isSelected) MedinaSage else PureAlabaster
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppDimensions.paddingMediumSmall,
                vertical = AppDimensions.paddingSmall
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppText.BodyMedium(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PureAlabaster else TextPrimary,
                maxLines = 1
            )
        }
    }
}
