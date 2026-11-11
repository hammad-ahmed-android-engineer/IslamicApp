package com.example.noorapp.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Centralized typography tokens and dynamic style builders dedicated exclusively to Quranic verses,
 * Arabic calligraphic scripts (across all 7 bundled Mushaf fonts), Rosette & Waqf badges, and Urdu Nastaleeq.
 */
object QuranTypography {

    // SACRED ARABIC SCRIPT TOKENS (RESPONSIVE SSP)
    val ArabicLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = QuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.ssp,
            lineHeight = 52.ssp
        )

    val ArabicMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = QuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.ssp,
            lineHeight = 42.ssp
        )

    val ArabicSmall: TextStyle
        @Composable get() = TextStyle(
            fontFamily = QuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.ssp,
            lineHeight = 34.ssp
        )

    val IndoPakArabicLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = IndoPakQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.ssp,
            lineHeight = 54.ssp
        )

    val IndoPakArabicMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = IndoPakQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.ssp,
            lineHeight = 44.ssp
        )

    val UthmaniArabicLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = UthmaniQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.ssp,
            lineHeight = 52.ssp
        )

    val UthmaniArabicMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = UthmaniQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.ssp,
            lineHeight = 42.ssp
        )

    val PdmsSaleemArabic: TextStyle
        @Composable get() = TextStyle(
            fontFamily = PdmsSaleemFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 26.ssp,
            lineHeight = 48.ssp
        )

    // URDU NASTALEEQ TRANSLATION & TAFSEER STYLES
    val UrduTranslationLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = UrduTranslationFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 20.ssp,
            lineHeight = 36.ssp
        )

    val UrduTranslationMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = UrduTranslationFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 17.ssp,
            lineHeight = 30.ssp
        )

    val TafseerUrdu: TextStyle
        @Composable get() = TextStyle(
            fontFamily = UrduTranslationFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.ssp,
            lineHeight = 28.ssp
        )

    /**
     * Builds a metric-scaled RTL [TextStyle] tailored to the specified [fontFamily] and [FontTypographyConfig].
     */
    fun arabicVerseStyle(
        fontFamily: FontFamily,
        config: FontTypographyConfig,
        fontSize: TextUnit,
        lineHeight: TextUnit = TextUnit.Unspecified,
        fontWeight: FontWeight = FontWeight.Normal,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start
    ): TextStyle {
        val effectiveFontSize = (fontSize.value * config.fontSizeScale).sp
        val effectiveLineHeight = if (lineHeight != TextUnit.Unspecified) {
            maxOf(lineHeight.value, effectiveFontSize.value * config.lineHeightMultiplier).sp
        } else {
            (effectiveFontSize.value * config.lineHeightMultiplier).sp
        }
        return TextStyle(
            fontFamily = fontFamily,
            fontSize = effectiveFontSize,
            lineHeight = effectiveLineHeight,
            fontWeight = fontWeight,
            color = color,
            textAlign = textAlign,
            textDirection = TextDirection.Rtl,
            lineBreak = LineBreak.Simple,
            platformStyle = PlatformTextStyle(includeFontPadding = true),
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None
            )
        )
    }

    /**
     * Builds a Nastaleeq RTL [TextStyle] with vertical headroom preventing ascender/descender clipping.
     */
    fun urduTranslationStyle(
        fontSize: TextUnit,
        lineHeight: TextUnit = (fontSize.value * 1.95f).sp,
        color: Color = Color.Unspecified,
        textAlign: TextAlign = TextAlign.Start
    ): TextStyle = TextStyle(
        fontFamily = UrduTranslationFontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = color,
        textAlign = textAlign,
        textDirection = TextDirection.Rtl,
        platformStyle = PlatformTextStyle(includeFontPadding = true),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None
        )
    )

    /**
     * Centered numeral style for circular Ayat ending rosettes.
     */
    fun rosetteNumeralStyle(
        fontSize: TextUnit,
        color: Color
    ): TextStyle = TextStyle(
        fontSize = fontSize,
        lineHeight = fontSize,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = FontFamily.Default,
        textAlign = TextAlign.Center,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    )

    /**
     * Compact zero-padding RTL style for overhead Waqf pause and Sajdah badges.
     */
    fun specialMarkStyle(
        fontSize: TextUnit,
        color: Color,
        fontFamily: FontFamily = FontFamily.Default
    ): TextStyle = TextStyle(
        fontSize = fontSize,
        lineHeight = (fontSize.value * 1.15f).sp,
        fontWeight = FontWeight.Bold,
        color = color,
        fontFamily = fontFamily,
        textAlign = TextAlign.Center,
        textDirection = TextDirection.Rtl,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    )
}

