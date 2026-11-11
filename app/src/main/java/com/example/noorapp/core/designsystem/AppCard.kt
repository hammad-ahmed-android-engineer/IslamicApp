package com.example.noorapp.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.utils.clickableDebounced

/**
 * Primary reusable card container enforcing centralized [AppDimensions], border strokes,
 * elevation, and debounced click handling across the app.
 */
@Composable
fun AppCard(
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

/**
 * Domain-aliased card wrapper delegating directly to [AppCard].
 */
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
    AppCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        borderWidth = borderWidth,
        cornerRadius = cornerRadius,
        contentPadding = contentPadding,
        elevation = elevation,
        onClick = onClick,
        content = content
    )
}

/**
 * Selectable card wrapper for chips, option cards, and grid tiles.
 */
@Composable
fun AppSelectableCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedBackgroundColor: Color = MedinaSage,
    unselectedBackgroundColor: Color = PureAlabaster,
    selectedBorderColor: Color = MedinaSage,
    unselectedBorderColor: Color = DividerColor,
    borderWidth: Dp = if (isSelected) AppDimensions.borderWidthMedium else AppDimensions.borderWidthThin,
    cornerRadius: Dp = AppDimensions.cornerLarge,
    contentPadding: Dp = AppDimensions.paddingMediumSmall,
    elevation: Dp = AppDimensions.elevationSubtle,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier,
        backgroundColor = if (isSelected) selectedBackgroundColor else unselectedBackgroundColor,
        borderColor = if (isSelected) selectedBorderColor else unselectedBorderColor,
        borderWidth = borderWidth,
        cornerRadius = cornerRadius,
        contentPadding = contentPadding,
        elevation = elevation,
        onClick = onClick,
        content = content
    )
}

/**
 * Navigation shortcut tile built on [AppCard] and [AppText].
 */
@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color = MedinaSageLight,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AppCard(
        modifier = modifier,
        backgroundColor = PureAlabaster,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimensions.avatarSize)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }
            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
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
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.6f),
                modifier = Modifier.size(AppDimensions.iconMedium)
            )
        }
    }
}

