package com.ne.muslimprayers.qiblafinder.ui.more.components

import androidx.compose.foundation.background
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun MoreSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    IslamicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickableDebounced { onClick() },
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        cornerRadius = AppDimensions.cornerLarge,
        elevation = AppDimensions.elevationSubtle,
        contentPadding = AppDimensions.paddingMediumSmall
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimensions.avatarSize)
                    .clip(CircleShape)
                    .background(MedinaSageLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }
            Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))
            Column(modifier = Modifier.weight(1f)) {
                AppText.TitleMedium(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                AppText.BodySmall(
                    text = subtitle,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
