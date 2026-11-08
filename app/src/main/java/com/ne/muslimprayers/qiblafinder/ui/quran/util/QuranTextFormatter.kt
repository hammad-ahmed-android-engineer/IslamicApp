package com.ne.muslimprayers.qiblafinder.ui.quran.util

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre

object QuranTextFormatter {

    const val INLINE_AYAH_END_ID = "inline_ayah_end"

    /**
     * Sacred Quranic Waqf (Stopping Signs) & Tajweed Pause marks in Unicode (Uthmani, Indo-Pak & PDMS PUA)
     */
    val WAQF_MARKS = setOf(
        '\u06D6', // ۖ Al-Wasl Awla (صلى)
        '\u06D7', // ۗ Al-Waqf Awla (قلى)
        '\u06D8', // ۘ Waqf Lazim (مـ)
        '\u06D9', // ۙ Waqf Mamnu' (لا)
        '\u06DA', // ۚ Waqf Ja'iz (ج)
        '\u06DB', // ۛ Mu'anaqah (∴)
        '\u06DC', // ۜ Saktah (س)
        '\u0615', // ؕ Small High Tah (Waqf Mutlaq - Indo-Pak)
        '\u06DF', // ۟ Small High Rounded Zero
        '\u06E0', // ۠ Small High Upright Rectangular Zero
        '\u06E9', // ۩ Sajdah
        '\uE01A', // PDMS PUA Laa
        '\uE01B', // PDMS PUA Sad-Lam-Ya
        '\uE01C', // PDMS PUA Qaf-Lam-Ya
        '\uE01D', // PDMS PUA Jeem
        '\uE01E', // PDMS PUA Meem
        '\uE01F', // PDMS PUA Mu'anaqah
        '\uE020', // PDMS PUA Waqf
        '\uE021', // PDMS PUA Seen
        '\uE022'  // PDMS PUA Sajdah
    )

    private val TANWEEN_MARKS = setOf('\u064B', '\u064C', '\u064D')
    private val SUKUN_MARKS = setOf('\u0652', '\u06E1')
    private val MADD_MARKS = setOf('\u0653', '\u06E4', '\u0670', '\u0657')
    private val IQLAB_MARKS = setOf('\u06E2', '\u06ED')

    // Authentic Tajweed Color Palette for Coloured Quran
    val TAJWEED_WAQF_COLOR = Color(0xFFD84315)      // Deep Warm Terracotta / Ochre for Waqf Pause characters & signs
    val TAJWEED_MADD_COLOR = Color(0xFFC62828)      // Crimson Red for Madd (elongation)
    val TAJWEED_GHUNNAH_COLOR = Color(0xFF2E7D32)   // Forest Green for Ghunnah (نّ / مّ)
    val TAJWEED_IKHFA_COLOR = Color(0xFF00897B)     // Teal Green for Ikhfa & Idgham with Ghunnah
    val TAJWEED_QALQALAH_COLOR = Color(0xFF1565C0)  // Sapphire Blue for Qalqalah (ق ط ب ج د)
    val TAJWEED_IQLAB_COLOR = Color(0xFF7B1FA2)     // Royal Purple for Iqlab (م / ب)

    val QALQALAH_LETTERS = setOf('ق', 'ط', 'ب', 'ج', 'د')
    private val IKHFA_IDGHAM_LETTERS = setOf(
        'ت', 'ث', 'ج', 'د', 'ذ', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ف', 'ق', 'ك', 'ي', 'ی', 'ن', 'م', 'و'
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
    fun cleanAyahTextForDisplay(
        surahNumber: Int,
        ayahNumberInSurah: Int,
        rawArabicText: String,
        fontKey: String = ArabicFontManager.currentFontKey.value
    ): String {
        var text = rawArabicText.removePrefix("\uFEFF").trim()
        if (surahNumber > 1 && ayahNumberInSurah == 1) {
            for (p in BISMILLAH_PREFIXES) {
                if (text.startsWith(p)) {
                    text = text.removePrefix(p).trim()
                    break
                }
            }
        }
        return ArabicFontManager.prepareTextForFont(text, fontKey)
    }

    private fun isCombiningOrWaqfMark(c: Char): Boolean {
        val code = c.code
        return code in 0x0610..0x061A ||
                code in 0x064B..0x065F ||
                code == 0x0670 ||
                code in 0x06D6..0x06ED ||
                code in 0xE01A..0xE022
    }

    private data class GraphemeCluster(
        val start: Int,
        val end: Int,
        val baseChar: Char,
        val text: String
    ) {
        fun hasAny(chars: Set<Char>): Boolean = text.any { it in chars }
        fun hasChar(c: Char): Boolean = text.indexOf(c) >= 0
    }

    private fun parseClusters(text: String): List<GraphemeCluster> {
        if (text.isEmpty()) return emptyList()
        val clusters = ArrayList<GraphemeCluster>()
        var i = 0
        while (i < text.length) {
            val start = i
            val base = text[i]
            i++
            if (base != ' ') {
                while (i < text.length && isCombiningOrWaqfMark(text[i])) {
                    i++
                }
            }
            clusters.add(
                GraphemeCluster(
                    start = start,
                    end = i,
                    baseChar = base,
                    text = text.substring(start, i)
                )
            )
        }
        return clusters
    }

    /**
     * Applies shaping-safe Tajweed colors across Arabic grapheme clusters.
     *
     * IMPORTANT: Only `SpanStyle(color = ...)` is applied (never changing `fontWeight` or `fontFamily`
     * mid-word), which guarantees HarfBuzz keeps Arabic cursive joining and diacritic placement 100% intact.
     * Characters on which Tajweed pauses (Waqf marks) are added are colored together with their pause sign.
     */
    private fun AnnotatedString.Builder.appendTajweedColoredText(
        trimmed: String,
        waqfHighlightColor: Color
    ) {
        val clusters = parseClusters(trimmed)
        if (clusters.isEmpty()) return

        val clusterColors = arrayOfNulls<Color>(clusters.size)

        // Pass 1: Identify Tajweed rules and Waqf pauses per grapheme cluster
        for (idx in clusters.indices) {
            val cluster = clusters[idx]
            val base = cluster.baseChar
            if (base == ' ') continue

            // Next non-space cluster for context-sensitive rules (Ikhfa, Idgham, Iqlab, Waqf)
            var nextCluster: GraphemeCluster? = null
            var nextIdx = idx + 1
            while (nextIdx < clusters.size) {
                if (clusters[nextIdx].baseChar != ' ') {
                    nextCluster = clusters[nextIdx]
                    break
                }
                nextIdx++
            }

            // 1. Waqf / Tajweed Pause Marks:
            // If this cluster IS a Waqf mark or CARRIES a Waqf mark (e.g., ؕ, ۚ, ۖ, ۗ, ۘ, ۙ, etc.),
            // color this cluster AND if it's a standalone Waqf sign (or followed immediately by a Waqf sign),
            // also color the preceding character cluster on which the pause occurs!
            if (cluster.hasAny(WAQF_MARKS) || base in WAQF_MARKS) {
                clusterColors[idx] = TAJWEED_WAQF_COLOR
                // If this cluster is a standalone Waqf sign (possibly preceded by a space),
                // also highlight the final character cluster of the paused word right before it
                if (base in WAQF_MARKS) {
                    var prevIdx = idx - 1
                    while (prevIdx >= 0 && clusters[prevIdx].baseChar == ' ') {
                        prevIdx--
                    }
                    if (prevIdx >= 0 && clusterColors[prevIdx] == null) {
                        clusterColors[prevIdx] = TAJWEED_WAQF_COLOR
                    }
                }
                continue
            }

            // 2. Iqlab (Small High Meem ۢ or Noon/Tanween before ب)
            if (cluster.hasAny(IQLAB_MARKS) ||
                ((base == 'ن' && (cluster.hasAny(SUKUN_MARKS) || cluster.text.length == 1)) || cluster.hasAny(TANWEEN_MARKS)) &&
                nextCluster?.baseChar == 'ب'
            ) {
                clusterColors[idx] = TAJWEED_IQLAB_COLOR
                continue
            }

            // 3. Ghunnah (Noon or Meem with Shaddah ّ)
            if ((base == 'ن' || base == 'م') && cluster.hasChar('\u0651')) {
                clusterColors[idx] = TAJWEED_GHUNNAH_COLOR
                continue
            }

            // 4. Qalqalah (ق ط ب ج د with Sukun OR at the very end of the verse / before a Waqf pause)
            val isBeforePauseOrEnd = nextCluster == null ||
                    nextCluster.baseChar in WAQF_MARKS ||
                    nextCluster.hasAny(WAQF_MARKS)
            if (base in QALQALAH_LETTERS && (cluster.hasAny(SUKUN_MARKS) || isBeforePauseOrEnd)) {
                clusterColors[idx] = TAJWEED_QALQALAH_COLOR
                continue
            }

            // 5. Ikhfa & Idgham with Ghunnah (Noon Sakinah or Tanween followed by Ikhfa/Idgham letter)
            val isNoonSakin = base == 'ن' && (cluster.hasAny(SUKUN_MARKS) || cluster.text.length == 1)
            val hasTanween = cluster.hasAny(TANWEEN_MARKS)
            if ((isNoonSakin || hasTanween) && nextCluster != null && nextCluster.baseChar in IKHFA_IDGHAM_LETTERS) {
                clusterColors[idx] = TAJWEED_IKHFA_COLOR
                continue
            }

            // 6. Madd (Elongation: Maddah ~, Small High Mad ۤ, Dagger Alif ٰ, Inverted Damma ٗ)
            if (cluster.hasAny(MADD_MARKS)) {
                clusterColors[idx] = TAJWEED_MADD_COLOR
                continue
            }
        }

        // Pass 2: Emit contiguous runs of identical color to minimize Span count and preserve ligatures
        var runStart = 0
        while (runStart < clusters.size) {
            val activeColor = clusterColors[runStart]
            var runEnd = runStart + 1
            while (runEnd < clusters.size && clusterColors[runEnd] == activeColor) {
                runEnd++
            }
            val textSlice = trimmed.substring(clusters[runStart].start, clusters[runEnd - 1].end)
            if (activeColor != null) {
                withStyle(SpanStyle(color = activeColor)) {
                    append(textSlice)
                }
            } else {
                append(textSlice)
            }
            runStart = runEnd
        }
    }

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
        ayahEndColor: Color = DesertOchre,
        fontKey: String = ArabicFontManager.currentFontKey.value
    ): AnnotatedString {
        val trimmed = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText, fontKey)
        val arabicNumber = ayahNumberInSurah.toArabicDigits()

        return buildAnnotatedString {
            if (!isColored) {
                append(trimmed)
            } else {
                appendTajweedColoredText(trimmed, waqfHighlightColor)
            }

            withStyle(
                SpanStyle(
                    color = if (isColored) ayahEndColor else Color.Unspecified
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
        waqfHighlightColor: Color = DesertOchre,
        fontKey: String = ArabicFontManager.currentFontKey.value
    ): AnnotatedString {
        val trimmed = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText, fontKey)
        val arabicNumber = ayahNumberInSurah.toArabicDigits()

        return buildAnnotatedString {
            if (!isColored) {
                append(trimmed)
            } else {
                appendTajweedColoredText(trimmed, waqfHighlightColor)
            }

            append(" ")
            appendInlineContent(INLINE_AYAH_END_ID, "\u06DD$arabicNumber")
        }
    }

    /**
     * Formats plain text for clipboard copy or sharing with the authentic Ayah End sign included.
     */
    fun getPlainTextWithEndSign(
        rawArabicText: String,
        ayahNumberInSurah: Int,
        surahNumber: Int = 1,
        fontKey: String = ArabicFontManager.currentFontKey.value
    ): String {
        val trimmed = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText, fontKey)
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
