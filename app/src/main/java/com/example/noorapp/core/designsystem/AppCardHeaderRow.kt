package com.example.noorapp.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.noorapp.R
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary

/**
 * Standardized card header row pairing a circular [AppIconBadge], title, optional subtitle,
 * and an optional trailing slot for pill buttons, toggles, or Copy/Share actions.
 */
@Composable
fun AppCardHeaderRow(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    subtitleColor: Color = TextSecondary,
    iconTint: Color = MedinaSage,
    iconBackgroundColor: Color = MedinaSageLight,
    iconBorderColor: Color? = null,
    badgeSize: Dp = AppDimensions.iconButtonCompact,
    iconSize: Dp = AppDimensions.iconRegular,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            AppIconBadge(
                icon = icon,
                tint = iconTint,
                backgroundColor = iconBackgroundColor,
                borderColor = iconBorderColor,
                size = badgeSize,
                iconSize = iconSize
            )
            Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))
            Column(modifier = Modifier.weight(1f)) {
                AppText.TitleMedium(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                if (!subtitle.isNullOrBlank()) {
                    AppText.Caption(
                        text = subtitle,
                        color = subtitleColor,
                        maxLines = 1
                    )
                }
            }
        }

        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall),
                content = trailingContent
            )
        }
    }
}

/**
 * Reusable circular Copy & Share action pair shared across Home, Dua, and Hadith cards.
 */
@Composable
fun AppCopyShareActions(
    onCopy: () -> Unit,
    onShare: () -> Unit,
    copyDescription: String = stringResource(R.string.copy),
    shareDescription: String = stringResource(R.string.share),
    copyTint: Color = MedinaSage,
    copyContainerColor: Color = MedinaSageLight,
    shareTint: Color = DesertOchre,
    shareContainerColor: Color = DesertOchreLight
) {
    AppCircleIconButton(
        icon = Icons.Default.ContentCopy,
        contentDescription = copyDescription,
        onClick = onCopy,
        tint = copyTint,
        containerColor = copyContainerColor,
        borderColor = copyTint.copy(alpha = 0.25f)
    )
    AppCircleIconButton(
        icon = Icons.Default.Share,
        contentDescription = shareDescription,
        onClick = onShare,
        tint = shareTint,
        containerColor = shareContainerColor,
        borderColor = shareTint.copy(alpha = 0.25f)
    )
}

