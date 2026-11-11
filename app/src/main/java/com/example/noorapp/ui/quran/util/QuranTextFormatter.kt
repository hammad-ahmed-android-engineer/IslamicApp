package com.example.noorapp.ui.quran.util

import androidx.annotation.StringRes
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import com.example.noorapp.R
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.TextSecondary

object QuranTextFormatter {

    const val INLINE_AYAH_END_ID = "inline_ayah_end"
    const val INLINE_WAQF_PREFIX = "inline_waqf_"

    /**
     * Canonical inline Waqf / Sajdah ID -> crisp non-combining Arabic pause label pairs
     * rendered cleanly in inline slots so marks never overlap Tanween/diacritics or split cursive stems.
     */
    val WAQF_INLINE_ENTRIES: List<Pair<String, String>> = listOf(
        "${INLINE_WAQF_PREFIX}wasl" to "صلى",
        "${INLINE_WAQF_PREFIX}waqf" to "قلى",
        "${INLINE_WAQF_PREFIX}lazim" to "مـ",
        "${INLINE_WAQF_PREFIX}mamnu" to "لا",
        "${INLINE_WAQF_PREFIX}jaiz" to "ج",
        "${INLINE_WAQF_PREFIX}muanaqah" to "∴",
        "${INLINE_WAQF_PREFIX}saktah" to "س",
        "${INLINE_WAQF_PREFIX}mutlaq" to "ط",
        "${INLINE_WAQF_PREFIX}sajdah" to "۩"
    )

    fun waqfInlineIdFor(ch: Char): String = when (ch) {
        '\u06D6', '\uE01B', '\uE020' -> "${INLINE_WAQF_PREFIX}wasl"
        '\u06D7', '\uE01C' -> "${INLINE_WAQF_PREFIX}waqf"
        '\u06D8', '\uE01E' -> "${INLINE_WAQF_PREFIX}lazim"
        '\u06D9', '\uE01A' -> "${INLINE_WAQF_PREFIX}mamnu"
        '\u06DA', '\uE01D' -> "${INLINE_WAQF_PREFIX}jaiz"
        '\u06DB', '\uE01F' -> "${INLINE_WAQF_PREFIX}muanaqah"
        '\u06DC' -> "${INLINE_WAQF_PREFIX}saktah"
        '\u0615' -> "${INLINE_WAQF_PREFIX}mutlaq"
        '\u06E9' -> "${INLINE_WAQF_PREFIX}sajdah"
        else -> "${INLINE_WAQF_PREFIX}jaiz"
    }

    fun waqfDisplayLabel(ch: Char): String = when (ch) {
        '\u06D6', '\uE01B', '\uE020' -> "صلى"
        '\u06D7', '\uE01C' -> "قلى"
        '\u06D8', '\uE01E' -> "مـ"
        '\u06D9', '\uE01A' -> "لا"
        '\u06DA', '\uE01D' -> "ج"
        '\u06DB', '\uE01F' -> "∴"
        '\u06DC' -> "س"
        '\u0615' -> "ط"
        '\u06E9' -> "۩"
        else -> ch.toString()
    }

    /**
     * Sacred Quranic Waqf (Stopping Signs) & Tajweed Pause marks in Unicode (Uthmani, Indo-Pak & PDMS PUA).
     * Note: U+06DF (۟) and U+06E0 (۠) are silent-letter circles (e.g. أُو۟لَـٰٓئِكَ / قَالُوا۟), NOT Waqf pauses.
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
        '\u06E9', // ۩ Sajdah
        '\uE01A', // PDMS PUA Laa
        '\uE01B', // PDMS PUA Sad-Lam-Ya
        '\uE01C', // PDMS PUA Qaf-Lam-Ya
        '\uE01D', // PDMS PUA Jeem
        '\uE01E', // PDMS PUA Meem
        '\uE01F', // PDMS PUA Mu'anaqah
        '\uE020'  // PDMS PUA Waqf
    )

    private val TANWEEN_MARKS = setOf('\u064B', '\u064C', '\u064D')
    private val HARAKAT_VOWELS = setOf('\u064E', '\u064F', '\u0650', '\u0651', '\u0656', '\u0657')
    private val SUKUN_MARKS = setOf('\u0652', '\u06E1')
    private val MADD_MARKS = setOf('\u0653', '\u06E4')
    private val IQLAB_MARKS = setOf('\u06E2', '\u06ED')

    // Authentic Tajweed Color Palette for Coloured Quran
    val TAJWEED_WAQF_COLOR = Color(0xFFD84315)      // Deep Warm Terracotta for Waqf Pause characters & signs
    val TAJWEED_MADD_COLOR = Color(0xFFC62828)      // Crimson Red for Madd (4-6 count elongation)
    val TAJWEED_GHUNNAH_COLOR = Color(0xFF2E7D32)   // Forest Green for Ghunnah & Idgham w/ Ghunnah
    val TAJWEED_IKHFA_COLOR = Color(0xFF00897B)     // Teal Green for Ikhfa & Ikhfa Shafawi
    val TAJWEED_QALQALAH_COLOR = Color(0xFF1565C0)  // Sapphire Blue for Qalqalah (ق ط ب ج د)
    val TAJWEED_IQLAB_COLOR = Color(0xFF7B1FA2)     // Royal Purple for Iqlab (م / ب)

    val QALQALAH_LETTERS = setOf('ق', 'ط', 'ب', 'ج', 'د')
    private val IKHFA_LETTERS = setOf(
        'ت', 'ث', 'ج', 'د', 'ذ', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ف', 'ق', 'ك', '\u06A9'
    )
    private val IDGHAM_WITH_GHUNNAH_LETTERS = setOf('ي', '\u06CC', 'ن', 'م', 'و')
    private val SILENT_CARRIER_LETTERS = setOf('ا', 'ى', '\u06D2', '\u06DF', '\u06E0')

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

    private fun isBareOrSakin(cluster: GraphemeCluster): Boolean {
        if (cluster.hasAny(SUKUN_MARKS)) return true
        if (cluster.hasAny(HARAKAT_VOWELS) || cluster.hasAny(TANWEEN_MARKS)) return false
        return true
    }

    private data class NextPhoneticTarget(
        val cluster: GraphemeCluster,
        val crossesWordBoundary: Boolean,
        val crossesWaqfPause: Boolean
    )

    private fun findNextPhoneticTarget(
        clusters: List<GraphemeCluster>,
        fromIndex: Int,
        skipSilentAlefCarrier: Boolean = false
    ): NextPhoneticTarget? {
        var i = fromIndex + 1
        var sawSpace = false
        var sawWaqf = false
        var skippedCarrier = false

        while (i < clusters.size) {
            val c = clusters[i]
            if (c.baseChar == ' ') {
                sawSpace = true
                i++
                continue
            }
            if (c.baseChar in WAQF_MARKS) {
                sawWaqf = true
                i++
                continue
            }
            if (skipSilentAlefCarrier && !skippedCarrier &&
                c.baseChar in SILENT_CARRIER_LETTERS &&
                !c.hasAny(HARAKAT_VOWELS) && !c.hasAny(TANWEEN_MARKS)
            ) {
                skippedCarrier = true
                if (c.hasAny(WAQF_MARKS)) sawWaqf = true
                i++
                continue
            }
            return NextPhoneticTarget(
                cluster = c,
                crossesWordBoundary = sawSpace || skippedCarrier,
                crossesWaqfPause = sawWaqf
            )
        }
        return null
    }

    /**
     * Applies shaping-safe, font-aware Tajweed & Waqf pause colors across Arabic grapheme clusters.
     *
     * Key per-font shaping protections:
     * - Only `SpanStyle(color = ...)` is emitted (never `fontWeight` or `fontFamily`), keeping HarfBuzz cursive joining intact.
     * - Adjacent ligated pairs (such as Tatweel-Madd `ـٰٓ` and Lam-Alef Madd `لَآ`) share the same `SpanStyle`
     *   so no font (Muhammadi, Hafs, Al-Majeed, Al-Mushaf, Al-Qalam, Nabi, PDMS Saleem) suffers mid-ligature clipping.
     * - Cross-word Tajweed rules skip silent Alef carriers (`ـًا`, `ـًى`) so Tanween Ikhfa/Idgham/Iqlab match accurately.
     * - Mid-verse Waqf marks on fonts without standalone `cls=1` PUA glyphs are extracted out of the Arabic cluster
     *   into atomic [appendInlineContent] pause slots so they never stack on Tanween or tear cursive final letters.
     */
    private fun AnnotatedString.Builder.appendFormattedBodyText(
        trimmed: String,
        isColored: Boolean,
        waqfHighlightColor: Color,
        useInlineWaqfMarks: Boolean
    ) {
        val clusters = parseClusters(trimmed)
        if (clusters.isEmpty()) return

        val clusterColors = arrayOfNulls<Color>(clusters.size)

        if (isColored) {
            for (idx in clusters.indices) {
                val cluster = clusters[idx]
                val base = cluster.baseChar
                if (base == ' ') continue

                val nextDirect = findNextPhoneticTarget(clusters, idx, skipSilentAlefCarrier = false)
                val hasWaqaMark = cluster.hasAny(WAQF_MARKS) || base in WAQF_MARKS

                // 1. Standalone Waqf pause glyph (spaced cls=1 PUA or isolated Waqf symbol)
                if (base in WAQF_MARKS) {
                    clusterColors[idx] = TAJWEED_WAQF_COLOR
                    continue
                }

                // 2. Iqlab (Small High/Low Meem ۢ / ۭ OR Noon Sakin / Tanween before ب)
                val hasTanween = cluster.hasAny(TANWEEN_MARKS)
                val isNoonSakin = base == 'ن' && isBareOrSakin(cluster)
                val nextAfterTanweenOrNoon = if (hasTanween || isNoonSakin) {
                    findNextPhoneticTarget(clusters, idx, skipSilentAlefCarrier = hasTanween)
                } else {
                    nextDirect
                }
                if (cluster.hasAny(IQLAB_MARKS) ||
                    ((isNoonSakin || hasTanween) && nextAfterTanweenOrNoon?.cluster?.baseChar == 'ب')
                ) {
                    clusterColors[idx] = TAJWEED_IQLAB_COLOR
                    continue
                }

                // 3. Qalqalah (ق ط ب ج د with Sukun, OR at verse end / right before a Waqf pause)
                val isAtEndOrPause = hasWaqaMark ||
                        nextDirect == null ||
                        nextDirect.crossesWaqfPause ||
                        nextDirect.cluster.baseChar in WAQF_MARKS
                if (base in QALQALAH_LETTERS && (cluster.hasAny(SUKUN_MARKS) || isAtEndOrPause)) {
                    clusterColors[idx] = TAJWEED_QALQALAH_COLOR
                    continue
                }

                // 4. Ghunnah (نّ / مّ) + Idgham with Ghunnah (Noon Sakin / Tanween + ي ن م و across word boundary)
                //    + Idgham Shafawi (Meem Sakin + م across word boundary)
                if ((base == 'ن' || base == 'م') && cluster.hasChar('\u0651')) {
                    clusterColors[idx] = TAJWEED_GHUNNAH_COLOR
                    continue
                }
                if ((isNoonSakin || hasTanween) &&
                    nextAfterTanweenOrNoon != null &&
                    nextAfterTanweenOrNoon.crossesWordBoundary &&
                    !nextAfterTanweenOrNoon.crossesWaqfPause &&
                    nextAfterTanweenOrNoon.cluster.baseChar in IDGHAM_WITH_GHUNNAH_LETTERS
                ) {
                    clusterColors[idx] = TAJWEED_GHUNNAH_COLOR
                    continue
                }
                val isMeemSakin = base == 'م' && isBareOrSakin(cluster)
                if (isMeemSakin &&
                    nextDirect != null &&
                    nextDirect.crossesWordBoundary &&
                    !nextDirect.crossesWaqfPause &&
                    nextDirect.cluster.baseChar == 'م'
                ) {
                    clusterColors[idx] = TAJWEED_GHUNNAH_COLOR
                    continue
                }

                // 5. Ikhfa (Noon Sakin / Tanween + 15 Ikhfa letters) & Ikhfa Shafawi (Meem Sakin + ب)
                if ((isNoonSakin || hasTanween) &&
                    nextAfterTanweenOrNoon != null &&
                    !nextAfterTanweenOrNoon.crossesWaqfPause &&
                    nextAfterTanweenOrNoon.cluster.baseChar in IKHFA_LETTERS
                ) {
                    clusterColors[idx] = TAJWEED_IKHFA_COLOR
                    continue
                }
                if (isMeemSakin &&
                    nextDirect != null &&
                    nextDirect.crossesWordBoundary &&
                    !nextDirect.crossesWaqfPause &&
                    nextDirect.cluster.baseChar == 'ب'
                ) {
                    clusterColors[idx] = TAJWEED_IKHFA_COLOR
                    continue
                }

                // 6. Madd (Obligatory / Permissible 4-6 count Elongation: Maddah ٓ, Small High Madda ۤ, Alif Madda آ)
                if (cluster.hasAny(MADD_MARKS) || base == '\u0622') {
                    clusterColors[idx] = TAJWEED_MADD_COLOR
                    // Keep Tatweel-Madd (e.g. يَـٰٓ) and Lam-Alef Madd ligature (لَآ) unified
                    // so HarfBuzz never splits the cursive stroke or Lam-Alef ligature in any font
                    if (idx > 0 && clusters[idx - 1].baseChar != ' ') {
                        val prev = clusters[idx - 1]
                        if (base == '\u0640' || (base == 'ا' && prev.baseChar == 'ل')) {
                            if (clusterColors[idx - 1] == null) {
                                clusterColors[idx - 1] = TAJWEED_MADD_COLOR
                            }
                        }
                    }
                    continue
                }
            }
        }

        // Emit contiguous color runs; when useInlineWaqfMarks is enabled, extract Waqf marks
        // out of clusters into atomic inline slots so combining Waqf marks never collide with
        // Tanween/Shaddah or cause cursive span splits.
        fun emitSegment(slice: String, activeColor: Color?) {
            if (!useInlineWaqfMarks) {
                val renderedColor = if (activeColor == TAJWEED_WAQF_COLOR) waqfHighlightColor else activeColor
                if (renderedColor != null) {
                    withStyle(SpanStyle(color = renderedColor)) {
                        append(slice)
                    }
                } else {
                    append(slice)
                }
                return
            }

            var textBuf = StringBuilder()
            fun flushTextBuf() {
                if (textBuf.isNotEmpty()) {
                    val str = textBuf.toString()
                    if (activeColor != null && activeColor != TAJWEED_WAQF_COLOR) {
                        withStyle(SpanStyle(color = activeColor)) {
                            append(str)
                        }
                    } else {
                        append(str)
                    }
                    textBuf = StringBuilder()
                }
            }

            for (ch in slice) {
                if (ch in WAQF_MARKS) {
                    flushTextBuf()
                    append(" ")
                    appendInlineContent(waqfInlineIdFor(ch), waqfDisplayLabel(ch))
                } else {
                    textBuf.append(ch)
                }
            }
            flushTextBuf()
        }

        var runStart = 0
        while (runStart < clusters.size) {
            val activeColor = clusterColors[runStart]
            var runEnd = runStart + 1
            while (runEnd < clusters.size && clusterColors[runEnd] == activeColor) {
                runEnd++
            }
            val textSlice = trimmed.substring(clusters[runStart].start, clusters[runEnd - 1].end)
            emitSegment(textSlice, activeColor)
            runStart = runEnd
        }
    }

    /**
     * Data result for a formatted Ayah containing its AnnotatedString and an optional extracted
     * end-of-verse special Waqf / Sajdah mark to be positioned directly ABOVE the rosette icon.
     */
    data class FormattedAyahResult(
        val annotatedString: AnnotatedString,
        val endingSpecialMark: String? = null
    )

    private val ENDING_SPECIAL_MARKS = setOf(
        '\u06D6', // ۖ Al-Wasl Awla
        '\u06D7', // ۗ Al-Waqf Awla
        '\u06D8', // ۘ Waqf Lazim
        '\u06D9', // ۙ Waqf Mamnu'
        '\u06DA', // ۚ Waqf Ja'iz
        '\u06DB', // ۛ Mu'anaqah
        '\u06DC', // ۜ Saktah
        '\u0615', // ؕ Small High Tah
        '\u06E9', // ۩ Sajdah
        '\uE01A', '\uE01B', '\uE01C', '\uE01D', '\uE01E', '\uE01F', '\uE020'
    )

    /**
     * Extracts ALL trailing end-of-verse Waqf pause signs or Sajdah marks so they render cleanly
     * floating centered ABOVE the Ayah rosette icon instead of sitting beside it.
     */
    fun extractEndingSpecialMark(text: String): Pair<String, String?> {
        var trimmed = text.trim()
        if (trimmed.isEmpty()) return Pair(trimmed, null)

        val collectedMarks = mutableListOf<String>()
        while (trimmed.isNotEmpty() && trimmed.last() in ENDING_SPECIAL_MARKS) {
            val label = waqfDisplayLabel(trimmed.last())
            if (collectedMarks.firstOrNull() != label) {
                collectedMarks.add(0, label)
            }
            trimmed = trimmed.dropLast(1).trimEnd()
        }

        val combinedMark = if (collectedMarks.isNotEmpty()) {
            collectedMarks.joinToString(" ")
        } else {
            null
        }
        return Pair(trimmed, combinedMark)
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
        val cleanedText = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText, fontKey)
        val (bodyText, _) = extractEndingSpecialMark(cleanedText)
        val arabicNumber = ayahNumberInSurah.toArabicDigits()

        return buildAnnotatedString {
            appendFormattedBodyText(
                trimmed = bodyText,
                isColored = isColored,
                waqfHighlightColor = waqfHighlightColor,
                useInlineWaqfMarks = false
            )

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
     * Extracts trailing Waqf/Sajdah marks so they float centered ABOVE the rosette icon, and converts
     * mid-verse colliding Waqf marks into non-overlapping inline pause slots.
     */
    fun formatAyahWithInlineEndSign(
        rawArabicText: String,
        ayahNumberInSurah: Int,
        surahNumber: Int = 1,
        isColored: Boolean = true,
        waqfHighlightColor: Color = TAJWEED_WAQF_COLOR,
        fontKey: String = ArabicFontManager.currentFontKey.value
    ): FormattedAyahResult {
        val cleanedText = cleanAyahTextForDisplay(surahNumber, ayahNumberInSurah, rawArabicText, fontKey)
        val (bodyText, extractedEndingMark) = extractEndingSpecialMark(cleanedText)
        val arabicNumber = ayahNumberInSurah.toArabicDigits()
        val fontConfig = ArabicFontManager.getTypographyConfig(fontKey)

        val annotated = buildAnnotatedString {
            appendFormattedBodyText(
                trimmed = bodyText,
                isColored = isColored,
                waqfHighlightColor = waqfHighlightColor,
                useInlineWaqfMarks = !fontConfig.supportsPdmsPua
            )

            append(" ")
            appendInlineContent(INLINE_AYAH_END_ID, "\u06DD$arabicNumber")
        }

        return FormattedAyahResult(
            annotatedString = annotated,
            endingSpecialMark = extractedEndingMark
        )
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
     * Formats general vocalized Arabic text (Dhikr presets, short phrases) in the active Quranic font
     * without multi-color Tajweed overlays by default.
     */
    fun formatArabicPhraseMultiColor(
        rawArabicText: String,
        isColored: Boolean = false,
        waqfHighlightColor: Color = TAJWEED_WAQF_COLOR,
        fontKey: String = ArabicFontManager.currentFontKey.value
    ): AnnotatedString {
        val normalized = ArabicFontManager.prepareTextForFont(
            rawArabicText.removePrefix("\uFEFF").trim(),
            fontKey
        )
        return buildAnnotatedString {
            appendFormattedBodyText(
                trimmed = normalized,
                isColored = isColored,
                waqfHighlightColor = waqfHighlightColor,
                useInlineWaqfMarks = false
            )
        }
    }

    /**
     * Formats Dua Arabic text in non-colored monochrome Quranic typography while cleanly
     * handling enclosing round brackets `((...))`, `(...)`, square brackets `[...]`, and
     * ornate Quranic parentheses `﴿...﴾`:
     * - Strips redundant double/single outer enclosing brackets around the whole Dua.
     * - Renders remaining inline `(notes)` and `[notes]` safely with fallback system brackets
     *   in [TextSecondary] so bundled Quranic TTF fonts without ASCII bracket glyphs never show tofu boxes.
     * - Styles ornate Quranic `﴿...﴾` markers cleanly without changing the monochrome Arabic body ink.
     */
    fun formatDuaArabicText(
        rawArabicText: String,
        fontKey: String = ArabicFontManager.currentFontKey.value
    ): AnnotatedString {
        var text = ArabicFontManager.prepareTextForFont(
            rawArabicText.removePrefix("\uFEFF").trim(),
            fontKey
        )

        // Clean stray punctuation inside/outside enclosing brackets from source data
        text = text
            .replace("(.", "(")
            .replace("..)", ")")
            .replace("..", ".")
            .trim()

        // Strip double-parenthesis wrappers (( ... )) while keeping inner content intact
        text = text.replace("((", "").replace("))", "").trim()

        // Trim redundant outer single wrapper (...) or [...] when it encloses the full Dua
        text = unwrapOuterBracketPair(text, '(', ')')
        text = unwrapOuterBracketPair(text, '[', ']')
        text = text.trim()

        // Put square-bracket variant instructions onto a separated visual line when embedded after punctuation
        text = text.replace(".[", ".\n[").replace(". [", ".\n[")

        val noteColor = TextSecondary
        val fallbackBracketStyle = SpanStyle(fontFamily = FontFamily.Default, color = noteColor)
        val mainBracketStyle = SpanStyle(fontFamily = FontFamily.Default, color = DesertOchre)

        return buildAnnotatedString {
            var i = 0
            while (i < text.length) {
                val ch = text[i]
                when (ch) {
                    '[' -> {
                        val close = text.indexOf(']', i + 1)
                        if (close > i) {
                            val noteBody = text.substring(i + 1, close).trim()
                            append("\u2067")
                            withStyle(fallbackBracketStyle) { append("[") }
                            withStyle(SpanStyle(color = noteColor)) { append(" $noteBody ") }
                            withStyle(fallbackBracketStyle) { append("]") }
                            append("\u2069")
                            i = close + 1
                        } else {
                            withStyle(fallbackBracketStyle) { append(ch) }
                            i++
                        }
                    }
                    '(' -> {
                        val close = text.indexOf(')', i + 1)
                        if (close > i) {
                            val inner = text.substring(i + 1, close).trim()
                            // Treat short parentheticals (e.g. repetition counts) as muted note, longer blocks cleanly isolated
                            val isShortNote = inner.length <= 40 && !inner.contains('\uFD3E')
                            append("\u2067")
                            if (isShortNote) {
                                withStyle(fallbackBracketStyle) { append("(") }
                                withStyle(SpanStyle(color = noteColor)) { append(inner) }
                                withStyle(fallbackBracketStyle) { append(")") }
                            } else {
                                withStyle(mainBracketStyle) { append("(") }
                                append(inner)
                                withStyle(mainBracketStyle) { append(")") }
                            }
                            append("\u2069")
                            i = close + 1
                        } else {
                            withStyle(fallbackBracketStyle) { append(ch) }
                            i++
                        }
                    }
                    '\uFD3E', '\uFD3F' -> {
                        withStyle(mainBracketStyle) { append(ch) }
                        i++
                    }
                    else -> {
                        append(ch)
                        i++
                    }
                }
            }
        }
    }

    /**
     * Normalizes enclosing round/square brackets in Dua English translations/transliterations
     * so notes like `[In the evening:]` format cleanly on a separate paragraph instead of `)[Note:](`.
     */
    fun formatDuaTranslationText(rawText: String): String {
        var text = rawText.trim()
        text = text.replace("((", "(").replace("))", ")")
        text = unwrapOuterBracketPair(text, '(', ')')
        text = text.replace(Regex("\\)\\s*\\["), ")\n[")
        text = text.replace(Regex("\\]\\s*\\("), "]\n")
        return text.trim()
    }

    private fun unwrapOuterBracketPair(input: String, open: Char, close: Char): String {
        var trimmed = input.trim()
        val hadTrailingDot = trimmed.endsWith("$close.")
        val core = if (hadTrailingDot) trimmed.dropLast(1).trimEnd() else trimmed
        if (core.length < 2 || core.first() != open || core.last() != close) {
            return input
        }
        var depth = 0
        for (idx in core.indices) {
            val c = core[idx]
            if (c == open) depth++
            else if (c == close) depth--
            if (depth == 0) {
                return if (idx == core.lastIndex) {
                    core.substring(1, core.lastIndex).trim()
                } else {
                    input
                }
            }
        }
        return input
    }

    /**
     * Data model describing a Quranic Waqf symbol and its recitation rule
     */
    data class WaqfSignInfo(
        val symbol: String,
        @StringRes val arabicNameRes: Int,
        @StringRes val englishNameRes: Int,
        @StringRes val ruleRes: Int,
        @StringRes val descriptionRes: Int
    )

    /**
     * Official Quranic Waqf & Tajweed stopping signs catalog
     */
    val WAQF_SIGNS_CATALOG: List<WaqfSignInfo> = listOf(
        WaqfSignInfo(
            symbol = "ط / ؕ",
            arabicNameRes = R.string.waqf_mutlaq_arabic,
            englishNameRes = R.string.waqf_mutlaq_english,
            ruleRes = R.string.waqf_mutlaq_rule,
            descriptionRes = R.string.waqf_mutlaq_desc
        ),
        WaqfSignInfo(
            symbol = "مـ",
            arabicNameRes = R.string.waqf_lazim_arabic,
            englishNameRes = R.string.waqf_lazim_english,
            ruleRes = R.string.waqf_lazim_rule,
            descriptionRes = R.string.waqf_lazim_desc
        ),
        WaqfSignInfo(
            symbol = "لا",
            arabicNameRes = R.string.waqf_mamnu_arabic,
            englishNameRes = R.string.waqf_mamnu_english,
            ruleRes = R.string.waqf_mamnu_rule,
            descriptionRes = R.string.waqf_mamnu_desc
        ),
        WaqfSignInfo(
            symbol = "ج",
            arabicNameRes = R.string.waqf_jaiz_arabic,
            englishNameRes = R.string.waqf_jaiz_english,
            ruleRes = R.string.waqf_jaiz_rule,
            descriptionRes = R.string.waqf_jaiz_desc
        ),
        WaqfSignInfo(
            symbol = "صلى",
            arabicNameRes = R.string.waqf_wasl_arabic,
            englishNameRes = R.string.waqf_wasl_english,
            ruleRes = R.string.waqf_wasl_rule,
            descriptionRes = R.string.waqf_wasl_desc
        ),
        WaqfSignInfo(
            symbol = "قلى",
            arabicNameRes = R.string.waqf_awla_arabic,
            englishNameRes = R.string.waqf_awla_english,
            ruleRes = R.string.waqf_awla_rule,
            descriptionRes = R.string.waqf_awla_desc
        ),
        WaqfSignInfo(
            symbol = "∴ ∴",
            arabicNameRes = R.string.waqf_muanaqah_arabic,
            englishNameRes = R.string.waqf_muanaqah_english,
            ruleRes = R.string.waqf_muanaqah_rule,
            descriptionRes = R.string.waqf_muanaqah_desc
        ),
        WaqfSignInfo(
            symbol = "س",
            arabicNameRes = R.string.waqf_saktah_arabic,
            englishNameRes = R.string.waqf_saktah_english,
            ruleRes = R.string.waqf_saktah_rule,
            descriptionRes = R.string.waqf_saktah_desc
        ),
        WaqfSignInfo(
            symbol = "۝",
            arabicNameRes = R.string.waqf_ayat_arabic,
            englishNameRes = R.string.waqf_ayat_english,
            ruleRes = R.string.waqf_ayat_rule,
            descriptionRes = R.string.waqf_ayat_desc
        ),
        WaqfSignInfo(
            symbol = "۩",
            arabicNameRes = R.string.waqf_sajdah_arabic,
            englishNameRes = R.string.waqf_sajdah_english,
            ruleRes = R.string.waqf_sajdah_rule,
            descriptionRes = R.string.waqf_sajdah_desc
        )
    )
}
