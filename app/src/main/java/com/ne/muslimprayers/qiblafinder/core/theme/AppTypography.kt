package com.ne.muslimprayers.qiblafinder.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * Centralized AppTypography system providing consistent, responsive typography tokens
 * across all screens, dialogs, inputs, and components in Islamic App.
 *
 * Utilizes scalable .ssp extension units for accessibility and proportional scaling.
 */
object AppTypography {

    // DISPLAY & HERO HEADINGS
    val DisplayLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 32.ssp,
            lineHeight = 38.ssp,
            letterSpacing = (-0.5).ssp
        )

    val DisplayMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 26.ssp,
            lineHeight = 32.ssp,
            letterSpacing = (-0.25).ssp
        )

    val DisplaySmall: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.ssp,
            lineHeight = 30.ssp,
            letterSpacing = 0.ssp
        )

    // MAIN HEADINGS & SUBHEADINGS
    val HeadingLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 22.ssp,
            lineHeight = 28.ssp,
            letterSpacing = (-0.2).ssp
        )

    val HeadingMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.ssp,
            lineHeight = 24.ssp,
            letterSpacing = (-0.15).ssp
        )

    val HeadingSmall: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.ssp,
            lineHeight = 22.ssp,
            letterSpacing = 0.ssp
        )

    // SECTION TITLES & HEADERS
    val TitleLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.ssp,
            lineHeight = 24.ssp,
            letterSpacing = 0.ssp
        )

    val TitleMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.ssp,
            lineHeight = 22.ssp,
            letterSpacing = (-0.1).ssp
        )

    val TitleSmall: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 14.ssp,
            lineHeight = 20.ssp,
            letterSpacing = 0.ssp
        )

    val SectionHeader: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 12.ssp,
            lineHeight = 16.ssp,
            letterSpacing = 0.8.ssp
        )

    // BODY TEXT
    val BodyLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 15.ssp,
            lineHeight = 22.ssp,
            letterSpacing = 0.15.ssp
        )

    val BodyMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 13.ssp,
            lineHeight = 18.ssp,
            letterSpacing = 0.1.ssp
        )

    val BodySmall: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 11.ssp,
            lineHeight = 16.ssp,
            letterSpacing = 0.15.ssp
        )

    // LABELS, CAPTIONS & HELPER TEXT
    val LabelLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.ssp,
            lineHeight = 20.ssp,
            letterSpacing = 0.1.ssp
        )

    val LabelMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 12.ssp,
            lineHeight = 16.ssp,
            letterSpacing = 0.3.ssp
        )

    val LabelSmall: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 11.ssp,
            lineHeight = 15.ssp,
            letterSpacing = 0.3.ssp
        )

    val Caption: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 10.ssp,
            lineHeight = 14.ssp,
            letterSpacing = 0.2.ssp
        )

    // SPECIALIZED ISLAMIC & COMPONENT TEXT STYLES
    val ToolbarTitle: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.ssp,
            lineHeight = 24.ssp,
            letterSpacing = (-0.15).ssp
        )

    val ButtonText: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.ssp,
            lineHeight = 20.ssp,
            letterSpacing = 0.1.ssp
        )

    // QURANIC ARABIC TYPOGRAPHY (INDO-PAK & UTHMANI SCRIPTS)
    val QuranArabicLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = QuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.ssp,
            lineHeight = 52.ssp
        )

    val QuranArabicMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = QuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.ssp,
            lineHeight = 42.ssp
        )

    val QuranArabicSmall: TextStyle
        @Composable get() = TextStyle(
            fontFamily = QuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.ssp,
            lineHeight = 34.ssp
        )

    // 1. Indo-Pak Script Font (Noorehuda / Muhammadi Bold)
    val IndoPakQuranArabicLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = IndoPakQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.ssp,
            lineHeight = 54.ssp
        )

    val IndoPakQuranArabicMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = IndoPakQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.ssp,
            lineHeight = 44.ssp
        )

    // 2. Uthmani Script Font (KFGQPC Uthman Taha Naskh / Hafs)
    val UthmaniQuranArabicLarge: TextStyle
        @Composable get() = TextStyle(
            fontFamily = UthmaniQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.ssp,
            lineHeight = 52.ssp
        )

    val UthmaniQuranArabicMedium: TextStyle
        @Composable get() = TextStyle(
            fontFamily = UthmaniQuranFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 22.ssp,
            lineHeight = 42.ssp
        )

    // PDMS Saleem Quran Font
    val PdmsSaleemQuranArabic: TextStyle
        @Composable get() = TextStyle(
            fontFamily = PdmsSaleemFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 26.ssp,
            lineHeight = 48.ssp
        )

    // 3. Translation & Tafseer Font (Urdu Nastaleeq / Jameel Noori Nastaleeq)
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

    val DigitalClock: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 24.ssp,
            lineHeight = 30.ssp
        )

    /**
     * Maps AppTypography to standard Material 3 Typography for Theme integration.
     */
    @Composable
    fun toMaterial3Typography(): Typography {
        return Typography(
            displayLarge = DisplayLarge,
            displayMedium = DisplayMedium,
            displaySmall = DisplaySmall,
            headlineLarge = HeadingLarge,
            headlineMedium = HeadingMedium,
            headlineSmall = HeadingSmall,
            titleLarge = TitleLarge,
            titleMedium = TitleMedium,
            titleSmall = TitleSmall,
            bodyLarge = BodyLarge,
            bodyMedium = BodyMedium,
            bodySmall = BodySmall,
            labelLarge = LabelLarge,
            labelMedium = LabelMedium,
            labelSmall = LabelSmall
        )
    }
}
