package com.ne.muslimprayers.qiblafinder.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.AppTypography
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager

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
    fun DisplayMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.DisplayMedium.copy(fontWeight = fontWeight) else AppTypography.DisplayMedium,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun DisplaySmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.DisplaySmall.copy(fontWeight = fontWeight) else AppTypography.DisplaySmall,
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
    fun HeadingSmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.HeadingSmall.copy(fontWeight = fontWeight) else AppTypography.HeadingSmall,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun TitleLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.TitleLarge.copy(fontWeight = fontWeight) else AppTypography.TitleLarge,
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
    fun TitleSmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.TitleSmall.copy(fontWeight = fontWeight) else AppTypography.TitleSmall,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun SectionHeader(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.SectionHeader.copy(fontWeight = fontWeight) else AppTypography.SectionHeader,
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
    fun LabelLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.LabelLarge.copy(fontWeight = fontWeight) else AppTypography.LabelLarge,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun LabelMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.LabelMedium.copy(fontWeight = fontWeight) else AppTypography.LabelMedium,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun LabelSmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.LabelSmall.copy(fontWeight = fontWeight) else AppTypography.LabelSmall,
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

    @Composable
    fun ButtonText(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = 1
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.ButtonText.copy(fontWeight = fontWeight) else AppTypography.ButtonText,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun DigitalClock(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = 1
    ) {
        Text(
            text = text,
            style = if (fontWeight != null) AppTypography.DigitalClock.copy(fontWeight = fontWeight) else AppTypography.DigitalClock,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    /**
     * Centralized Arabic text typography using the user-selected Arabic font (from the 7 root fonts)
     * with per-font metric scaling, glyph normalization, safe horizontal/vertical overhang padding,
     * and guaranteed Right-to-Left (RTL) layout direction.
     */
    @Composable
    fun ArabicText(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = 24.sp,
        lineHeight: TextUnit = TextUnit.Unspecified,
        fontWeight: FontWeight? = null,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        fontKeyOverride: String? = null,
        applySafePadding: Boolean = true
    ) {
        val currentGlobalKey by ArabicFontManager.currentFontKey.collectAsState()
        val activeFontKey = fontKeyOverride ?: currentGlobalKey
        val config = remember(activeFontKey) { ArabicFontManager.getTypographyConfig(activeFontKey) }
        val fontFamily = remember(activeFontKey) { ArabicFontManager.getFontFamily(activeFontKey) }
        val normalizedText = remember(text, activeFontKey) {
            ArabicFontManager.prepareTextForFont(text, activeFontKey)
        }

        val effectiveFontSize = (fontSize.value * config.fontSizeScale).sp
        val effectiveLineHeight = if (lineHeight != TextUnit.Unspecified) {
            val minLineHeight = effectiveFontSize.value * config.lineHeightMultiplier
            maxOf(lineHeight.value, minLineHeight).sp
        } else {
            (effectiveFontSize.value * config.lineHeightMultiplier).sp
        }

        // Inside LayoutDirection.Rtl, TextAlign.Start is the RIGHT edge (correct for Arabic).
        // If a caller passes TextAlign.End or TextAlign.Right thinking of LTR, normalize to Start.
        val resolvedAlign = when (textAlign) {
            TextAlign.End, TextAlign.Right -> TextAlign.Start
            else -> textAlign
        }

        val paddedModifier = if (applySafePadding) {
            modifier.padding(
                horizontal = config.horizontalSafePaddingDp.dp,
                vertical = config.verticalSafePaddingDp.dp
            )
        } else {
            modifier
        }

        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            Text(
                text = normalizedText,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = effectiveFontSize,
                    lineHeight = effectiveLineHeight,
                    fontWeight = fontWeight ?: FontWeight.Normal,
                    color = color,
                    textAlign = resolvedAlign,
                    textDirection = TextDirection.Rtl,
                    lineBreak = LineBreak.Simple,
                    platformStyle = PlatformTextStyle(includeFontPadding = true),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                ),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = paddedModifier
            )
        }
    }
}
