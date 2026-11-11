package com.example.noorapp.core.designsystem

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.AppTypography
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.QuranTypography

/**
 * Standardized typography component wrappers enforcing centralized [AppTypography]
 * for general UI text and [QuranTypography] for Quranic/Arabic/Urdu content.
 */
object AppText {

    @Composable
    private fun BaseText(
        text: String,
        baseStyle: TextStyle,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        fontStyle: FontStyle? = null,
        fontSizeOverride: TextUnit = TextUnit.Unspecified,
        lineHeightOverride: TextUnit = TextUnit.Unspecified,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) {
        val styled = baseStyle.copy(
            fontWeight = fontWeight ?: baseStyle.fontWeight,
            fontStyle = fontStyle ?: baseStyle.fontStyle,
            fontSize = if (fontSizeOverride != TextUnit.Unspecified) fontSizeOverride else baseStyle.fontSize,
            lineHeight = if (lineHeightOverride != TextUnit.Unspecified) lineHeightOverride else baseStyle.lineHeight
        )
        Text(
            text = text,
            style = styled,
            color = color,
            textAlign = textAlign,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }

    @Composable
    fun DisplayLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        fontSizeOverride: TextUnit = TextUnit.Unspecified,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(
        text = text,
        baseStyle = AppTypography.DisplayLarge,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        fontSizeOverride = fontSizeOverride,
        textAlign = textAlign,
        maxLines = maxLines
    )

    @Composable
    fun DisplayMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.DisplayMedium, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun DisplaySmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.DisplaySmall, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun HeadingLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.HeadingLarge, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun HeadingMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.HeadingMedium, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun HeadingSmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.HeadingSmall, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun TitleLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.TitleLarge, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun TitleMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.TitleMedium, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun TitleSmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.TitleSmall, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun SectionHeader(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.SectionHeader, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun BodyLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.BodyLarge, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun BodyMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        fontSizeOverride: TextUnit = TextUnit.Unspecified,
        lineHeightOverride: TextUnit = TextUnit.Unspecified,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(
        text = text,
        baseStyle = AppTypography.BodyMedium,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        fontSizeOverride = fontSizeOverride,
        lineHeightOverride = lineHeightOverride,
        textAlign = textAlign,
        maxLines = maxLines
    )

    @Composable
    fun BodySmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        fontStyle: FontStyle? = null,
        fontSizeOverride: TextUnit = TextUnit.Unspecified,
        lineHeightOverride: TextUnit = TextUnit.Unspecified,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(
        text = text,
        baseStyle = AppTypography.BodySmall,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        fontSizeOverride = fontSizeOverride,
        lineHeightOverride = lineHeightOverride,
        textAlign = textAlign,
        maxLines = maxLines
    )

    @Composable
    fun LabelLarge(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.LabelLarge, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun LabelMedium(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.LabelMedium, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun LabelSmall(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(text, AppTypography.LabelSmall, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    @Composable
    fun Caption(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        fontSizeOverride: TextUnit = TextUnit.Unspecified,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE
    ) = BaseText(
        text = text,
        baseStyle = AppTypography.Caption,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        fontSizeOverride = fontSizeOverride,
        textAlign = textAlign,
        maxLines = maxLines
    )

    @Composable
    fun ButtonText(
        text: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontWeight: FontWeight? = null,
        textAlign: TextAlign? = null,
        maxLines: Int = 1
    ) = BaseText(text, AppTypography.ButtonText, modifier, color, fontWeight, textAlign = textAlign, maxLines = maxLines)

    /**
     * Centralized Arabic calligraphy component powered by [QuranTypography.arabicVerseStyle]
     * and [ArabicFontManager] metrics.
     */
    @Composable
    fun ArabicText(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = AppDimensions.textArabicListTitle,
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

        val resolvedAlign = if (textAlign == TextAlign.End || textAlign == TextAlign.Right) TextAlign.Start else textAlign
        val style = remember(fontFamily, config, fontSize, lineHeight, fontWeight, color, resolvedAlign) {
            QuranTypography.arabicVerseStyle(
                fontFamily = fontFamily,
                config = config,
                fontSize = fontSize,
                lineHeight = lineHeight,
                fontWeight = fontWeight ?: FontWeight.Normal,
                color = color,
                textAlign = resolvedAlign
            )
        }
        val paddedModifier = if (applySafePadding) {
            modifier.padding(
                horizontal = AppDimensions.scaledDp(config.horizontalSafePaddingDp),
                vertical = AppDimensions.scaledDp(config.verticalSafePaddingDp)
            )
        } else {
            modifier
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = normalizedText,
                style = style,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = paddedModifier
            )
        }
    }

    /**
     * Centralized Coloured / Annotated Quranic verse renderer powered by [QuranTypography.arabicVerseStyle].
     */
    @Composable
    fun AnnotatedArabicText(
        text: AnnotatedString,
        modifier: Modifier = Modifier,
        inlineContent: Map<String, InlineTextContent> = emptyMap(),
        fontSize: TextUnit = AppDimensions.textArabicListTitle,
        lineHeight: TextUnit = TextUnit.Unspecified,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE,
        fontKeyOverride: String? = null
    ) {
        val currentGlobalKey by ArabicFontManager.currentFontKey.collectAsState()
        val activeFontKey = fontKeyOverride ?: currentGlobalKey
        val config = remember(activeFontKey) { ArabicFontManager.getTypographyConfig(activeFontKey) }
        val fontFamily = remember(activeFontKey) { ArabicFontManager.getFontFamily(activeFontKey) }
        val resolvedAlign = if (textAlign == TextAlign.End || textAlign == TextAlign.Right) TextAlign.Start else textAlign
        val style = remember(fontFamily, config, fontSize, lineHeight, color, resolvedAlign) {
            QuranTypography.arabicVerseStyle(
                fontFamily = fontFamily,
                config = config,
                fontSize = fontSize,
                lineHeight = lineHeight,
                color = color,
                textAlign = resolvedAlign
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = text,
                inlineContent = inlineContent,
                style = style,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
            )
        }
    }

    /**
     * Centralized Urdu Nastaleeq translation component powered by [QuranTypography.urduTranslationStyle].
     */
    @Composable
    fun UrduText(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = AppDimensions.textArabicPreview,
        lineHeight: TextUnit = AppDimensions.scaledSp(fontSize.value * 1.95f),
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start,
        maxLines: Int = Int.MAX_VALUE
    ) {
        val resolvedAlign = if (textAlign == TextAlign.End || textAlign == TextAlign.Right) TextAlign.Start else textAlign
        val style = remember(fontSize, lineHeight, color, resolvedAlign) {
            QuranTypography.urduTranslationStyle(
                fontSize = fontSize,
                lineHeight = lineHeight,
                color = color,
                textAlign = resolvedAlign
            )
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = text,
                style = style,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = modifier
            )
        }
    }

    /**
     * Centered numeral typography tailored for circular Ayat Rosette badges via [QuranTypography.rosetteNumeralStyle].
     */
    @Composable
    fun RosetteNumeral(
        text: String,
        fontSize: TextUnit,
        color: Color,
        modifier: Modifier = Modifier
    ) {
        Text(
            text = text,
            style = QuranTypography.rosetteNumeralStyle(fontSize = fontSize, color = color),
            modifier = modifier
        )
    }

    /**
     * Crisp zero-padding badge text for Waqf pause and Sajdah signs via [QuranTypography.specialMarkStyle].
     */
    @Composable
    fun SpecialMarkBadge(
        text: String,
        fontSize: TextUnit,
        color: Color,
        modifier: Modifier = Modifier,
        fontFamily: FontFamily = FontFamily.Default
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = text,
                style = QuranTypography.specialMarkStyle(
                    fontSize = fontSize,
                    color = color,
                    fontFamily = fontFamily
                ),
                maxLines = 1,
                softWrap = false,
                modifier = modifier
            )
        }
    }
}

