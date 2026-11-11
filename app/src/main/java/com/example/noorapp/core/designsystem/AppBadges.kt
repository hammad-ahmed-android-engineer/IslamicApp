package com.example.noorapp.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight

/**
 * Standardized tinted icon medallion (circular or rounded-square) used across
 * feature cards, settings items, prayer rows, and banners.
 */
@Composable
fun AppIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = MedinaSage,
    backgroundColor: Color = MedinaSageLight,
    borderColor: Color? = null,
    borderWidth: Dp = AppDimensions.borderWidthHairline,
    size: Dp = AppDimensions.avatarSize,
    iconSize: Dp = AppDimensions.iconMedium,
    shape: Shape = CircleShape
) {
    val borderedModifier = if (borderColor != null) {
        modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
    } else {
        modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
    }

    Box(
        modifier = borderedModifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Standardized pill/tag badge for category labels, status indicators, counts, and version tags.
 */
@Composable
fun AppStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MedinaSage,
    backgroundColor: Color = MedinaSageLight,
    borderColor: Color? = null,
    borderWidth: Dp = AppDimensions.borderWidthHairline,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color = textColor,
    cornerRadius: Dp = AppDimensions.cornerSmall,
    fontWeight: FontWeight = FontWeight.Bold,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AppDimensions.paddingSmall,
        vertical = AppDimensions.paddingMicro
    )
) {
    val shape = RoundedCornerShape(cornerRadius)
    val baseModifier = if (borderColor != null) {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .padding(contentPadding)
    } else {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPadding)
    }

    Row(
        modifier = baseModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = leadingIconTint,
                modifier = Modifier.size(AppDimensions.iconTiny)
            )
        }
        AppText.LabelSmall(
            text = text,
            color = textColor,
            fontWeight = fontWeight,
            maxLines = 1
        )
    }
}

