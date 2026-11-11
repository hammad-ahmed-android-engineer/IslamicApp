package com.example.noorapp.core.theme

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
        fontSizeScale = 1.02f,
        lineHeightMultiplier = 2.25f,
        horizontalSafePaddingDp = 12f,
        verticalSafePaddingDp = 8f,
        prefersIndoPakScript = true,
        supportsPdmsPua = false
    )

    private val HAFS_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.02f,
        lineHeightMultiplier = 2.15f,
        horizontalSafePaddingDp = 10f,
        verticalSafePaddingDp = 6f,
        prefersIndoPakScript = false,
        supportsPdmsPua = false
    )

    private val AL_MAJEED_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.04f,
        lineHeightMultiplier = 2.25f,
        horizontalSafePaddingDp = 14f,
        verticalSafePaddingDp = 8f,
        prefersIndoPakScript = true,
        supportsPdmsPua = true
    )

    private val AL_MUSHAF_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.02f,
        lineHeightMultiplier = 2.20f,
        horizontalSafePaddingDp = 16f, // Protects extreme calligraphic overhangs (xmin = -13154)
        verticalSafePaddingDp = 8f,
        prefersIndoPakScript = false,
        supportsPdmsPua = false
    )

    private val AL_QALAM_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.04f,
        lineHeightMultiplier = 2.25f,
        horizontalSafePaddingDp = 14f,
        verticalSafePaddingDp = 8f,
        prefersIndoPakScript = true,
        supportsPdmsPua = true
    )

    private val NABI_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.08f, // Compensates for tall internal ascender/descender box (2700/-1700)
        lineHeightMultiplier = 2.30f,
        horizontalSafePaddingDp = 12f,
        verticalSafePaddingDp = 8f,
        prefersIndoPakScript = false,
        supportsPdmsPua = false
    )

    private val PDMS_SALEEM_CONFIG = FontTypographyConfig(
        fontSizeScale = 1.14f, // Compensates for UPM = 2548 vs standard 2048
        lineHeightMultiplier = 2.25f,
        horizontalSafePaddingDp = 14f,
        verticalSafePaddingDp = 8f,
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

    private fun isStandardWaqfMark(c: Char): Boolean {
        return c == '\u0615' || c in '\u06D6'..'\u06DC'
    }

    /**
     * Intelligently sanitizes and normalizes Arabic/Quranic text for the target font's glyph table (`cmap`)
     * and OpenType `GPOS`/`hmtx` metrics so no missing-glyph boxes, broken ligatures, or overlapping/floating
     * Waqf pause marks ever appear.
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
        var lastWasWaqf = false

        fun appendMappedWaqf(waqfChar: Char, isNativeSpacedPua: Boolean = false) {
            if (lastWasWaqf && waqfChar != '\u06E9') {
                return
            }

            // Determine the exact target glyph and spacing behavior for the active font:
            // - pdms_saleem / al_majeed / al_qalam: use native cls=1 PUA Waqf glyphs (U+E01A..U+E020)
            //   with wide spaces (al_majeed/al_qalam overhang -617; pdms_saleem space is 100/2548em)
            //   so glyphs never collide with Tanween / Shaddah / neighboring letters.
            // - muhammadi / hafs / al_mushaf / nabi: normalize to clean Unicode symbols; mid-verse
            //   colliding marks are upgraded by QuranTextFormatter into non-overlapping inline badges,
            //   while end-of-verse marks are extracted above the Ayah rosette icon.
            val useSpacedPua = config.supportsPdmsPua
            val resolvedChar: Char = if (useSpacedPua) {
                when (waqfChar) {
                    '\u06D9', '\uE01A' -> '\uE01A'
                    '\u06D6', '\uE01B', '\uE020' -> '\uE01B'
                    '\u06D7', '\uE01C' -> '\uE01C'
                    '\u06DA', '\uE01D', '\u0615' -> '\uE01D'
                    '\u06D8', '\uE01E' -> if (normalizedKey == "pdms_saleem") '\uE01E' else '\uE01D'
                    '\u06DB', '\uE01F' -> '\uE01F'
                    else -> waqfChar
                }
            } else {
                val baseUnicode = when (waqfChar) {
                    '\uE01A' -> '\u06D9'
                    '\uE01B', '\uE020' -> '\u06D6'
                    '\uE01C' -> '\u06D7'
                    '\uE01D' -> '\u06DA'
                    '\uE01E' -> '\u06D8'
                    '\uE01F' -> '\u06DB'
                    else -> waqfChar
                }
                when {
                    baseUnicode == '\u06D7' && normalizedKey == "muhammadi" -> '\u06D6'
                    baseUnicode == '\u06DB' && normalizedKey == "nabi" -> '\u06DA'
                    else -> baseUnicode
                }
            }

            if (sb.isEmpty()) return

            val isCls1Standalone = useSpacedPua && resolvedChar in '\uE01A'..'\uE020'

            if (isCls1Standalone) {
                val padSpaces = if (normalizedKey == "pdms_saleem") "  " else "   "
                if (!lastWasSpace && sb[sb.length - 1] != ' ') {
                    sb.append(padSpaces)
                } else if (sb[sb.length - 1] == ' ') {
                    sb.append(" ")
                }
                sb.append(resolvedChar)
                sb.append(padSpaces)
                lastWasSpace = true
            } else {
                while (sb.isNotEmpty() && sb[sb.length - 1] == ' ') {
                    sb.deleteCharAt(sb.length - 1)
                }
                if (sb.isNotEmpty()) {
                    sb.append(resolvedChar)
                    lastWasSpace = false
                }
            }
            lastWasWaqf = (resolvedChar != '\u06E9')
        }

        for (i in rawText.indices) {
            val ch = rawText[i]
            when (ch) {
                // Strip invisible BOM / zero-width / stray PUA spacing & Ruku artifacts that cause floating offsets
                '\uFEFF', '\u200B', '\u200C', '\u200E', '\u200F',
                '\uE003', '\uE004', '\uE021', '\uE022' -> continue

                // Normalize En-space, Em-space, NBSP, standard space
                '\u2002', '\u2003', '\u00A0', ' ' -> {
                    if (!lastWasSpace && sb.isNotEmpty()) {
                        sb.append(' ')
                        lastWasSpace = true
                    }
                }

                // PDMS Saleem Private Use Area Waqf marks (U+E01A .. U+E020)
                in '\uE01A'..'\uE020' -> {
                    appendMappedWaqf(ch, isNativeSpacedPua = true)
                }

                // Standard Quranic Waqf stopping marks (U+0615, U+06D6..U+06DC)
                '\u0615', in '\u06D6'..'\u06DC' -> {
                    appendMappedWaqf(ch, isNativeSpacedPua = false)
                }

                // U+0614 (Arabic Sign Takhallus) - skip on fonts that lack it
                '\u0614' -> {
                    if (config.supportsPdmsPua) {
                        sb.append(ch)
                        lastWasSpace = false
                        lastWasWaqf = false
                    }
                }

                // U+06E9 (Place of Sajdah ۩) - extracted above Ayah rosette or rendered as inline badge
                '\u06E9' -> {
                    sb.append(ch)
                    lastWasSpace = false
                    lastWasWaqf = false
                }

                // U+06E5 (Small Waw ۥ) - missing in al_mushaf.ttf
                '\u06E5' -> {
                    if (normalizedKey != "al_mushaf") {
                        sb.append(ch)
                        lastWasSpace = false
                        lastWasWaqf = false
                    }
                }

                // U+06EC (Rounded High Stop with Filled Centre ۬) - missing in nabi.ttf
                '\u06EC' -> {
                    if (normalizedKey != "nabi") {
                        sb.append(ch)
                        lastWasSpace = false
                        lastWasWaqf = false
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
                    lastWasWaqf = false
                }

                // Subscript Alef (U+0656) & Inverted Damma (U+0657) - missing in hafs, al_mushaf, nabi
                '\u0656' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u0650') // Kasrah
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                    lastWasWaqf = false
                }

                '\u0657' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u064F') // Damma
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                    lastWasWaqf = false
                }

                // U+06E1 (Quranic Sukun / Small High Dotless Head of Khah):
                // - missing in nabi.ttf
                // - in muhammadi.ttf sits at y=900..1416 (collides directly with Waqf marks at y=897..1521!),
                //   whereas \u0652 (standard Sukun) sits cleanly at y=0..353 right with vowels.
                '\u06E1' -> {
                    if (normalizedKey == "nabi" || normalizedKey == "muhammadi") {
                        sb.append('\u0652') // Standard Sukun ْ
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                    lastWasWaqf = false
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
                    lastWasWaqf = false
                }

                '\u06A9' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u0643')
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                    lastWasWaqf = false
                }

                '\u06C1' -> {
                    if (normalizedKey in setOf("hafs", "al_mushaf", "nabi")) {
                        sb.append('\u0647')
                    } else {
                        sb.append(ch)
                    }
                    lastWasSpace = false
                    lastWasWaqf = false
                }

                else -> {
                    // If a new Arabic letter starts immediately after a mid-verse Waqf mark without a space,
                    // insert a space so the Waqf mark has breathing room before the next word.
                    if (lastWasWaqf && !lastWasSpace && ch.code in 0x0621..0x064A) {
                        sb.append(' ')
                    }
                    sb.append(ch)
                    lastWasSpace = false
                    if (!isStandardWaqfMark(ch)) {
                        lastWasWaqf = false
                    }
                }
            }
        }

        return sb.toString().trim()
    }
}
