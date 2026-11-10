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
