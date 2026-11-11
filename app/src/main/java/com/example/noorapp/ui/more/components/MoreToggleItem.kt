package com.example.noorapp.ui.more.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.core.designsystem.AppIconBadge
import com.example.noorapp.core.designsystem.AppSwitch
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary

@Composable
fun MoreToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconTint: Color = MedinaSage,
    iconBgColor: Color = MedinaSageLight
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppDimensions.paddingMedium,
                vertical = AppDimensions.paddingMediumSmall
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
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
                AppText.TitleMedium(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                AppText.BodySmall(
                    text = subtitle,
                    color = TextSecondary,
                    maxLines = 2
                )
            }
        }
        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
        AppSwitch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
