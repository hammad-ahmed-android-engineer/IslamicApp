package com.ne.muslimprayers.qiblafinder.ui.quran.util

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.QuranFontFamily

object QuranTextFormatter {

    const val INLINE_AYAH_END_ID = "inline_ayah_end"

    /**
     * Set of sacred Quranic Waqf (Stopping Signs) & Tajweed Pause marks in Unicode (Uthmani & Indo-Pak)
     */
    val WAQF_MARKS = setOf(
        '\u06D6', // ۖ Al-Wasl Awla (صلى)
        '\u06D7', // ۗ Al-Waqf Awla (قلى)
        '\u06D8', // ۘ Waqf Lazim (مـ)
        '\u06D9', // ۙ Waqf Mamnu' (لا)
        '\u06DA', // ۚ Waqf Ja'iz (ج)
        '\u06DB', // ۛ Mu'anaqah (∴)
        '\u06DC', // ۜ Saktah (س)
        '\u0615', // ؕ Small Tah (Waqf Mutlaq - standard South Asian / Indo-Pak / Islam 360)
        '\u06DF', // ۟ Small High Rounded Zero
        '\u06E0', // ۠ Small High Upright Rectangular Zero
        '\u06E1', // ۡ Small High Dotless Head of Khah / Sukun
        '\u06E2', // ۢ Small High Meem (Iqlab)
        '\u06E4', // ۤ Small High Mad
        '\u06E9'  // ۩ Sajdah
    )

    /**
     * Converts an integer to Eastern Arabic-Indic digits (٠, ١, ٢, ٣, ٤, ٥, ٦, ٧, ٨, ٩).
     */
    fun Int.toArabicDigits(): String {
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return this.toString().map { char ->
            if (char.isDigit()) arabicDigits[char - '0'] else char
        }.joinToString("")
    }

    private val BISMILLAH_PREFIXES = listOf(
        "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
        "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        "بِسْمِ اللهِ الرَّحْمٰنِ الرَّحِيْمِ",
        "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
        "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ"
    )

    /**
     * Cleans initial BOM and redundant prefixed Bismillah from Ayah 1 of surahs > 1
     * when the Surah header banner already prominently features Bismillah.
     */
    fun cleanAyahTextForDisplay(surahNumber: Int, ayahNumberInSurah: Int, rawArabicText: String): String {
        var text = rawArabicText.removePrefix("\uFEFF").trim()
        if (surahNumber > 1 && ayahNumberInSurah == 1) {
            for (p in BISMILLAH_PREFIXES) {
                if (text.startsWith(p)) {
                    text = text.removePrefix(p).trim()
                    break
                }
            }
        }
        return text
    }

    // Authentic Tajweed Color Palette for Coloured Quran
    val TAJWEED_WAQF_COLOR = DesertOchre // Golden Ochre for Stopping & Pause Signs
    val TAJWEED_MADD_COLOR = Color(0xFFC62828) // Deep Crimson Red for Madd (elongation)
    val TAJWEED_GHUNNAH_COLOR = Color(0xFF2E7D32) // Forest Green for Ghunnah / Ikhfa
    val TAJWEED_QALQALAH_COLOR = Color(0xFF1565C0) // Vibrant Blue for Qalqalah (echo)
    val TAJWEED_IQLAB_COLOR = Color(0xFF7B1FA2) // Royal Purple for Iqlab (mim)

    val QALQALAH_LETTERS = setOf('ق', 'ط', 'ب', 'ج', 'د')

    /**
     * Formats an Ayah's raw text with:
     * 1. Preserved Arabic ligatures and natural Quranic Waqf / Tajweed pause marks.
     * 2. Supports Plain (monochrome) vs Coloured (Tajweed colored) Mushaf style.
     * 3. Appends the authentic Quranic Ayah End Rosette (۝) with the verse number in Eastern Arabic numerals.
     */
    fun formatAyahWithEndSign(
        rawArabicText: String,
        ayahNumberInSurah: Int,
        surahNumber: Int = 1,
        isColored: Boolean = true,
        waqfHighlightColor: Color = DesertOchre,
        ayahEndColor: Color = DesertOchre
    ): AnnotatedString {
        val trimmed = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText)
        val arabicNumber = ayahNumberInSurah.toArabicDigits()

        return buildAnnotatedString {
            if (!isColored) {
                // Plain Quran: Standard monochrome Arabic text
                append(trimmed)
            } else {
                // Coloured Quran: Authentic Tajweed Color Coded Rules
                var i = 0
                while (i < trimmed.length) {
                    val c = trimmed[i]
                    when {
                        c in WAQF_MARKS -> {
                            withStyle(SpanStyle(color = waqfHighlightColor, fontWeight = FontWeight.Bold)) {
                                append(c)
                            }
                        }
                        c == '\u0653' || c == '\u06E4' -> { // Maddah ~ or Small High Mad
                            withStyle(SpanStyle(color = TAJWEED_MADD_COLOR, fontWeight = FontWeight.Bold)) {
                                append(c)
                            }
                        }
                        c == '\u06E2' -> { // Small High Meem (Iqlab)
                            withStyle(SpanStyle(color = TAJWEED_IQLAB_COLOR, fontWeight = FontWeight.Bold)) {
                                append(c)
                            }
                        }
                        c in QALQALAH_LETTERS && i + 1 < trimmed.length && (trimmed[i + 1] == '\u06E1' || trimmed[i + 1] == '\u0652') -> {
                            withStyle(SpanStyle(color = TAJWEED_QALQALAH_COLOR, fontWeight = FontWeight.SemiBold)) {
                                append(c)
                            }
                        }
                        (c == 'ن' || c == 'م') && i + 1 < trimmed.length && trimmed[i + 1] == '\u0651' -> {
                            withStyle(SpanStyle(color = TAJWEED_GHUNNAH_COLOR, fontWeight = FontWeight.SemiBold)) {
                                append(c)
                            }
                        }
                        else -> {
                            append(c)
                        }
                    }
                    i++
                }
            }

            // Append the authentic Quranic Ayah End Rosette Sign (۝)
            withStyle(
                SpanStyle(
                    color = if (isColored) ayahEndColor else Color.Unspecified,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuranFontFamily
                )
            ) {
                append(" \u06DD$arabicNumber")
            }
        }
    }

    /**
     * Formats an Ayah's raw text inserting an [InlineTextContent] placeholder for the Ayah End Rosette,
     * ensuring the verse number is rendered perfectly INSIDE the circular rosette without detachment.
     * Supports Plain (monochrome) vs Coloured (Tajweed colored) Quran styles.
     */
    fun formatAyahWithInlineEndSign(
        rawArabicText: String,
        ayahNumberInSurah: Int,
        surahNumber: Int = 1,
        isColored: Boolean = true,
        waqfHighlightColor: Color = DesertOchre
    ): AnnotatedString {
        val trimmed = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText)
        val arabicNumber = ayahNumberInSurah.toArabicDigits()

        return buildAnnotatedString {
            if (!isColored) {
                // Plain Quran: pure monochrome text, no colored spans
                append(trimmed)
            } else {
                // Coloured Quran: Tajweed pause signs, Madd, Ghunnah, Qalqalah highlighted
                var i = 0
                while (i < trimmed.length) {
                    val c = trimmed[i]
                    when {
                        c in WAQF_MARKS -> {
                            withStyle(SpanStyle(color = waqfHighlightColor, fontWeight = FontWeight.Bold)) {
                                append(c)
                            }
                        }
                        c == '\u0653' || c == '\u06E4' -> { // Maddah
                            withStyle(SpanStyle(color = TAJWEED_MADD_COLOR, fontWeight = FontWeight.Bold)) {
                                append(c)
                            }
                        }
                        c == '\u06E2' -> { // Iqlab
                            withStyle(SpanStyle(color = TAJWEED_IQLAB_COLOR, fontWeight = FontWeight.Bold)) {
                                append(c)
                            }
                        }
                        c in QALQALAH_LETTERS && i + 1 < trimmed.length && (trimmed[i + 1] == '\u06E1' || trimmed[i + 1] == '\u0652') -> {
                            withStyle(SpanStyle(color = TAJWEED_QALQALAH_COLOR, fontWeight = FontWeight.SemiBold)) {
                                append(c)
                            }
                        }
                        (c == 'ن' || c == 'م') && i + 1 < trimmed.length && trimmed[i + 1] == '\u0651' -> {
                            withStyle(SpanStyle(color = TAJWEED_GHUNNAH_COLOR, fontWeight = FontWeight.SemiBold)) {
                                append(c)
                            }
                        }
                        else -> {
                            append(c)
                        }
                    }
                    i++
                }
            }

            append("\u200F ")
            appendInlineContent(INLINE_AYAH_END_ID, "\u200F\u06DD$arabicNumber\u200F")
            append(" \u200F")
        }
    }

    /**
     * Formats plain text for clipboard copy or sharing with the authentic Ayah End sign included.
     */
    fun getPlainTextWithEndSign(rawArabicText: String, ayahNumberInSurah: Int, surahNumber: Int = 1): String {
        val trimmed = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText)
        val arabicNumber = ayahNumberInSurah.toArabicDigits()
        return "$trimmed \u200F\u06DD$arabicNumber\u200F"
    }

    /**
     * Data model describing a Quranic Waqf symbol and its recitation rule
     */
    data class WaqfSignInfo(
        val symbol: String,
        val arabicName: String,
        val englishName: String,
        val rule: String,
        val description: String,
        val importance: String
    )

    /**
     * Official Quranic Waqf & Tajweed stopping signs catalog
     */
    val WAQF_SIGNS_CATALOG: List<WaqfSignInfo> = listOf(
        WaqfSignInfo(
            symbol = "ط / ؕ",
            arabicName = "الوقف المطلق",
            englishName = "Waqf Mutlaq (Absolute Pause)",
            rule = "Better to Stop",
            description = "Marks the completion of a full thought or theme. Stopping here is highly recommended so that the next sentence can start cleanly.",
            importance = "Crucial"
        ),
        WaqfSignInfo(
            symbol = "مـ",
            arabicName = "الوقف اللازم",
            englishName = "Waqf Lazim (Compulsory Stop)",
            rule = "Must Stop",
            description = "Halting recitation here is compulsory. Continuing without stopping will alter or corrupt the intended divine meaning.",
            importance = "Crucial"
        ),
        WaqfSignInfo(
            symbol = "لا",
            arabicName = "الوقف الممنوع",
            englishName = "Waqf Mamnu' (Forbidden Stop)",
            rule = "Do Not Stop",
            description = "Stopping here is strictly forbidden because the sentence structure is not yet complete. If you run out of breath and must stop, repeat from the preceding word.",
            importance = "Crucial"
        ),
        WaqfSignInfo(
            symbol = "ج",
            arabicName = "الوقف الجائز",
            englishName = "Waqf Ja'iz (Permissible Stop)",
            rule = "Optional Stop",
            description = "You have equal choice to stop or continue recitation; neither choice is preferred over the other.",
            importance = "Permissible"
        ),
        WaqfSignInfo(
            symbol = "صلى",
            arabicName = "الوصل أولى",
            englishName = "Al-Wasl Awla (Better to Continue)",
            rule = "Continue Preferred",
            description = "Stopping is permissible, but connecting with the following word is preferred and superior in meaning.",
            importance = "Recommended"
        ),
        WaqfSignInfo(
            symbol = "قلى",
            arabicName = "الوقف أولى",
            englishName = "Al-Waqf Awla (Better to Stop)",
            rule = "Stop Preferred",
            description = "Continuing is permissible, but pausing at this word is preferred and superior in meaning.",
            importance = "Recommended"
        ),
        WaqfSignInfo(
            symbol = "∴ ∴",
            arabicName = "تعانق الوقف (المعانقة)",
            englishName = "Mu'anaqah (Embracing / Paired Stop)",
            rule = "Stop at One Only",
            description = "These three dots appear in pairs on nearby words. You may pause at ONE of the two places, but you must NEVER pause at both, nor skip both.",
            importance = "Paired Rule"
        ),
        WaqfSignInfo(
            symbol = "س",
            arabicName = "السكتة اللطيفة",
            englishName = "Saktah (Silence / Brief Pause)",
            rule = "Pause Without Breath",
            description = "Make a momentary, brief pause in sound for approximately one count without drawing a new breath, then immediately resume recitation.",
            importance = "Subtle Pause"
        ),
        WaqfSignInfo(
            symbol = "۝",
            arabicName = "رأس الآية (نهاية الآية)",
            englishName = "Ayat End Sign (End of Verse)",
            rule = "Sunnah Stop (Waqf Taam)",
            description = "Marks the conclusion of an Ayah. Pausing at the end of each verse is the authentic Sunnah of the Prophet Muhammad ﷺ.",
            importance = "Sunnah"
        ),
        WaqfSignInfo(
            symbol = "۩",
            arabicName = "علامة السجدة",
            englishName = "Sajdah (Prostration Verse)",
            rule = "Prostration Sunnah",
            description = "Indicates a verse of Sujood at-Tilawah (prostration of recitation). Performing one prostration upon reciting or hearing it is Sunnah Mu'akkadah.",
            importance = "Devotional"
        )
    )
}
