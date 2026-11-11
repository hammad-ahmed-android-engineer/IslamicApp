package com.example.noorapp.ui.more.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.core.designsystem.AppIconBadge
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.MutedText
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.utils.clickableDebounced

@Composable
fun MoreSettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    badgeText: String? = null,
    iconTint: Color = MedinaSage,
    iconBgColor: Color = MedinaSageLight,
    showChevron: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableDebounced { onClick() }
            .padding(
                horizontal = AppDimensions.paddingMedium,
                vertical = AppDimensions.paddingMediumSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconBadge(
            icon = icon,
            tint = iconTint,
            backgroundColor = iconBgColor,
            borderColor = iconTint.copy(alpha = 0.25f),
            size = AppDimensions.iconButtonCompact,
            iconSize = AppDimensions.iconMedium,
            shape = RoundedCornerShape(AppDimensions.cornerMedium)
        )

        Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                AppText.TitleMedium(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (!badgeText.isNullOrBlank()) {
                    AppStatusPill(
                        text = badgeText,
                        textColor = iconTint,
                        backgroundColor = iconBgColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
            AppText.BodySmall(
                text = subtitle,
                color = TextSecondary,
                maxLines = 2
            )
        }

        if (showChevron) {
            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MutedText,
                modifier = Modifier.size(AppDimensions.iconMedium)
            )
        }
    }
}
