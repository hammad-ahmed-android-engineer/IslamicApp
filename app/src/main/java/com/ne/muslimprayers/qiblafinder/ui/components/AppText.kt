package com.ne.muslimprayers.qiblafinder.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.AppTypography

/**
 * Standardized typography composable wrappers enforcing centralized [AppTypography] styles.
 */
object AppText {

    @Composable
    fun DisplayLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.DisplayLarge.copy(fontWeight = fontWeight) else AppTypography.DisplayLarge,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun HeadingLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.HeadingLarge.copy(fontWeight = fontWeight) else AppTypography.HeadingLarge,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun HeadingMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.HeadingMedium.copy(fontWeight = fontWeight) else AppTypography.HeadingMedium,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun TitleMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.TitleMedium.copy(fontWeight = fontWeight) else AppTypography.TitleMedium,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun BodyLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.BodyLarge.copy(fontWeight = fontWeight) else AppTypography.BodyLarge,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun BodyMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.BodyMedium.copy(fontWeight = fontWeight) else AppTypography.BodyMedium,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun BodySmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.BodySmall.copy(fontWeight = fontWeight) else AppTypography.BodySmall,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun Caption(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.Caption.copy(fontWeight = fontWeight) else AppTypography.Caption,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    /**
     * Centralized Arabic text typography using the user-selected Arabic font (from the 7 root fonts)
     * with guaranteed Right-to-Left (RTL) layout direction.
     */
    @Composable
    fun ArabicText(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: androidx.compose.ui.unit.TextUnit = 22.sp,
        lineHeight: androidx.compose.ui.unit.TextUnit = 40.sp,
        fontWeight: FontWeight? = null,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE
    ) {
        val fontKey by com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager.currentFontKey.collectAsState()
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
        ) {
            Text(
                text = text,
                fontFamily = com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager.getFontFamily(fontKey),
                fontSize = fontSize,
                lineHeight = lineHeight,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
            )
        }
    }
}
