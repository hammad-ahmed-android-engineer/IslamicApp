package com.ne.muslimprayers.qiblafinder.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.R

// 1. Muhammadi Quranic Font (Default Indo-Pak Bold Script)
val MuhammadiFontFamily = FontFamily(
    Font(R.font.muhammadi, FontWeight.Normal)
)

// 2. Hafs Font (Standard Uthmani / Madinah Script)
val HafsFontFamily = FontFamily(
    Font(R.font.hafs, FontWeight.Normal)
)

// 3. Al Majeed Quranic Font
val AlMajeedFontFamily = FontFamily(
    Font(R.font.al_majeed, FontWeight.Normal)
)

// 4. Al Mushaf Quran Font
val AlMushafFontFamily = FontFamily(
    Font(R.font.al_mushaf, FontWeight.Normal)
)

// 5. Al Qalam Quran Font
val AlQalamFontFamily = FontFamily(
    Font(R.font.al_qalam, FontWeight.Normal)
)

// 6. Nabi Calligraphic Font
val NabiFontFamily = FontFamily(
    Font(R.font.nabi, FontWeight.Normal)
)

// 7. PDMS Saleem Quran Font (Pakistani Calligraphic)
val PdmsSaleemFontFamily = FontFamily(
    Font(R.font.pdms_saleem, FontWeight.Normal)
)

// Urdu Translation Font (Nastaleeq)
val UrduTranslationFontFamily = FontFamily(
    Font(R.font.jameel_noori_nastaleeq, FontWeight.Normal),
    Font(R.font.jameel_noori_nastaleeq, FontWeight.Bold)
)

// Backward compatibility aliases
val IndoPakQuranFontFamily = MuhammadiFontFamily
val UthmaniQuranFontFamily = HafsFontFamily
val QuranFontFamily = MuhammadiFontFamily
val AmiriQuranFontFamily = AlMajeedFontFamily

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
