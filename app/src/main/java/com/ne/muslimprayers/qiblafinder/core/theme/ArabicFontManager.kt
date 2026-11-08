package com.ne.muslimprayers.qiblafinder.core.theme

import android.content.Context
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FontTypographyConfig(
    val fontSizeScale: Float,
    val lineHeightMultiplier: Float,
    val horizontalSafePaddingDp: Float,
    val verticalSafePaddingDp: Float,
    val prefersIndoPakScript: Boolean,
    val supportsPdmsPua: Boolean,
    val letterSpacingSp: Float = 0f
)

data class ArabicFontItem(
    val key: String,
    val title: String,
    val subtitle: String,
    val fontFamily: FontFamily,
    val config: FontTypographyConfig
)

object ArabicFontManager {

    private const val PREFS_NAME = "quran_prefs"
    private const val KEY_ARABIC_FONT = "selected_quran_font"

    private val MUHAMMADI_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.00f,
        lineHeightMultiplier = 1.95f,
        horizontalSafePaddingDp = 10f,
        verticalSafePaddingDp = 6f,
        prefersIndoPakScript = false,
        supportsPdmsPua = false
    )

    private val HAFS_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.02f,
        lineHeightMultiplier = 1.85f,
        horizontalSafePaddingDp = 8f,
        verticalSafePaddingDp = 4f,
        prefersIndoPakScript = false,
        supportsPdmsPua = false
    )

    private val AL_MAJEED_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.04f,
        lineHeightMultiplier = 1.95f,
        horizontalSafePaddingDp = 12f,
        verticalSafePaddingDp = 6f,
        prefersIndoPakScript = true,
        supportsPdmsPua = true
    )

    private val AL_MUSHAF_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.02f,
        lineHeightMultiplier = 1.92f,
        horizontalSafePaddingDp = 14f, // Protects extreme calligraphic overhangs (xmin = -13154)
        verticalSafePaddingDp = 6f,
        prefersIndoPakScript = false,
        supportsPdmsPua = false
    )

    private val AL_QALAM_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.04f,
        lineHeightMultiplier = 1.95f,
        horizontalSafePaddingDp = 12f,
        verticalSafePaddingDp = 6f,
        prefersIndoPakScript = true,
        supportsPdmsPua = true
    )

    private val NABI_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.08f, // Compensates for tall internal ascender/descender box (2700/-1700)
        lineHeightMultiplier = 2.00f,
        horizontalSafePaddingDp = 10f,
        verticalSafePaddingDp = 6f,
        prefersIndoPakScript = false,
        supportsPdmsPua = false
    )

    private val PDMS_SALEEM_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.14f, // Compensates for UPM = 2548 vs standard 2048
        lineHeightMultiplier = 1.92f,
        horizontalSafePaddingDp = 12f,
        verticalSafePaddingDp = 6f,
        prefersIndoPakScript = true,
        supportsPdmsPua = true
    )

    val AVAILABLE_FONTS: List<ArabicFontItem> = listOf(
        ArabicFontItem("muhammadi", "Muhammadi", "Qalam Calligraphic Script", MuhammadiFontFamily, MUHAMMADI_CONFIG),
        ArabicFontItem("hafs", "Hafs", "Medina Standard (Uthmani)", HafsFontFamily, HAFS_CONFIG),
        ArabicFontItem("al_majeed", "Al Majeed", "South Asian Quranic Script", AlMajeedFontFamily, AL_MAJEED_CONFIG),
        ArabicFontItem("al_mushaf", "Al Mushaf", "Classic Mushaf Calligraphy", AlMushafFontFamily, AL_MUSHAF_CONFIG),
        ArabicFontItem("al_qalam", "Al Qalam", "Qalam Indo-Pak Script", AlQalamFontFamily, AL_QALAM_CONFIG),
        ArabicFontItem("nabi", "Nabi", "Naskh Calligraphic Script", NabiFontFamily, NABI_CONFIG),
        ArabicFontItem("pdms_saleem", "PDMS Saleem", "Pakistani Classical Script", PdmsSaleemFontFamily, PDMS_SALEEM_CONFIG)
    )

    private val _currentFontKey = MutableStateFlow("muhammadi")
    val currentFontKey: StateFlow<String> = _currentFontKey.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_ARABIC_FONT, "muhammadi") ?: "muhammadi"
        _currentFontKey.value = normalizeKey(saved)
    }

    fun setFont(context: Context, key: String) {
        val normalized = normalizeKey(key)
        _currentFontKey.value = normalized
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ARABIC_FONT, normalized).apply()
    }

    fun getFontFamily(key: String = _currentFontKey.value): FontFamily {
        return when (normalizeKey(key)) {
            "hafs", "uthmani" -> HafsFontFamily
            "al_majeed" -> AlMajeedFontFamily
            "al_mushaf" -> AlMushafFontFamily
            "al_qalam" -> AlQalamFontFamily
            "nabi" -> NabiFontFamily
            "pdms", "pdms_saleem" -> PdmsSaleemFontFamily
            else -> MuhammadiFontFamily
        }
    }

    fun getTypographyConfig(key: String = _currentFontKey.value): FontTypographyConfig {
        return when (normalizeKey(key)) {
            "hafs", "uthmani" -> HAFS_CONFIG
            "al_majeed" -> AL_MAJEED_CONFIG
            "al_mushaf" -> AL_MUSHAF_CONFIG
            "al_qalam" -> AL_QALAM_CONFIG
            "nabi" -> NABI_CONFIG
            "pdms", "pdms_saleem" -> PDMS_SALEEM_CONFIG
            else -> MUHAMMADI_CONFIG
        }
    }

    fun normalizeKey(key: String): String {
        return when (key.lowercase().trim()) {
            "indopak" -> "pdms_saleem"
            "uthmani" -> "hafs"
            "pdms" -> "pdms_saleem"
            else -> key.lowercase().trim()
        }
    }

    /**
     * Intelligently sanitizes and normalizes Arabic/Quranic text for the target font's glyph table (`cmap`)
     * so no missing-glyph boxes, broken ligatures, or floating PUA characters ever appear.
     */
    fun prepareTextForFont(
        rawText: String,
        fontKey: String = _currentFontKey.value
    ): String {
        if (rawText.isEmpty()) return rawText
        val normalizedKey = normalizeKey(fontKey)
        val config = getTypographyConfig(normalizedKey)

        val sb = StringBuilder(rawText.length)
        var lastWasSpace = false

        for (i in rawText.indices) {
            val ch = rawText[i]
            when (ch) {
                // Strip invisible BOM / zero-width / stray PUA spacing artifacts that cause floating offsets
                '\uFEFF', '\u200B', '\u200C', '\u200E', '\u200F', '\uE003', '\uE004' -> continue

                // Normalize En-space, Em-space, NBSP to standard space
                '\u2002', '\u2003', '\u00A0' -> {
                    if (!lastWasSpace && sb.isNotEmpty()) {
                        sb.append(' ')
                        lastWasSpace = true
                    }
                }

                ' ' -> {
                    if (!lastWasSpace && sb.isNotEmpty()) {
                        sb.append(' ')
                        lastWasSpace = true
                    }
                }

                // PDMS Saleem Private Use Area Waqf marks (U+E01A .. U+E022)
                in '\uE01A'..'\uE022' -> {
                    if (config.supportsPdmsPua) {
                        sb.append(ch)
                        lastWasSpace = false
                    } else {
                        val mappedWaqf = when (ch) {
                            '\uE01A' -> '\u06D9' // ۙ Laa
                            '\uE01B' -> '\u06D6' // ۖ Sad-Lam-Ya
                            '\uE01C' -> '\u06D7' // ۗ Qaf-Lam-Ya
                            '\uE01D' -> '\u06DA' // ۚ Jeem
                            '\uE01E' -> '\u06D8' // ۘ Meem
                            '\uE01F' -> '\u06DB' // ۫ Mu'anaqah
                            '\uE020' -> '\u06D6' // ۖ Waqf
                            '\uE021' -> '\u06DC' // ۜ Seen
                            '\uE022' -> '\u06E9' // ۩ Sajdah
                            else -> '\u06DA'
                        }
                        sb.append(mappedWaqf)
                        lastWasSpace = false
                    }
                }

                // Indo-Pak Small High Tah (U+0615) - only supported in pdms_saleem, al_majeed, al_qalam
                '\u0615' -> {
                    if (config.supportsPdmsPua) {
                        sb.append(ch)
                    } else {
                        sb.append('\u06DA') // Map to standard Waqf Jeem ۚ
                    }
                    lastWasSpace = false
                }

                // U+0614 (Arabic Sign Takhallus) - skip on fonts that lack it
                '\u0614' -> {
                    if (config.supportsPdmsPua) {
                        sb.append(ch)
                    }
                }

                // U+066E (Dotless Beh) - only in pdms_saleem, al_majeed, al_qalam
                '\u066E' -> {
                    if (config.supportsPdmsPua) {
                        sb.append(ch)
                    } else {
                        sb.append('\u0628') // Standard Beh ب
                    }
                    lastWasSpace = false
                }

                // Subscript Alef (U+0656) & Inverted Damma (U+0657) - missing in hafs, al_mushaf, nabi
                '\u0656' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u0650') // Kasrah
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                }

                '\u0657' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u064F') // Damma
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                }

                // U+06E1 (Quranic Sukun / Small High Dotless Head of Khah) - missing in nabi.ttf
                '\u06E1' -> {
                    if (normalizedKey == "nabi") {
                        sb.append('\u0652') // Standard Sukun ْ
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                }

                // U+06CC (Farsi Yeh ی), U+06A9 (Keheh ک), U+06C1 (Heh Goal ہ)
                // Normalize for Uthmani-only fonts (hafs, al_mushaf, nabi) so cursive joining stays intact
                '\u06CC' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u064A')
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                }

                '\u06A9' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u0643')
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                }

                '\u06C1' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u0647')
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                }

                else -> {
                    sb.append(ch)
                    lastWasSpace = false
                }
            }
        }

        return sb.toString().trim()
    }
}
