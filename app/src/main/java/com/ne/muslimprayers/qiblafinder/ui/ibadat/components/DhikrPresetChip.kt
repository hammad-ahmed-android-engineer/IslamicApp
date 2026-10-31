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
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
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
            .clip(RoundedCornerShape(16.sdp))
            .clickableDebounced { onClick() }
            .border(
                width = 1.sdp,
                color = if (isSelected) MedinaSage else DividerColor,
                shape = RoundedCornerShape(16.sdp)
            ),
        shape = RoundedCornerShape(16.sdp),
        color = if (isSelected) MedinaSage else PureAlabaster
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.sdp, vertical = 8.sdp),
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
