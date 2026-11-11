package com.example.noorapp.core.utils

/**
 * Centralized string and text processing utilities across Islamic App.
 * Includes word counting, truncation, search query sanitization, and Arabic text helpers.
 */
object StringUtils {

    private val ARABIC_DIACRITICS_REGEX = Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06DC\\u06DF-\\u06E8\\u06EA-\\u06ED]")
    private val ARABIC_DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    /**
     * Accurately calculates word count from text.
     */
    fun calculateWordCount(text: String): Int {
        if (text.isBlank()) return 0
        return text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
    }

    /**
     * Calculates character count, optionally including whitespace.
     */
    fun calculateCharCount(text: String, includeSpaces: Boolean = true): Int {
        return if (includeSpaces) text.length else text.replace(Regex("\\s+"), "").length
    }

    /**
     * Calculates estimated reading time in minutes based on average 200 WPM.
     */
    fun calculateReadingTimeMinutes(wordCount: Int, wordsPerMinute: Int = 200): Int {
        if (wordCount <= 0) return 0
        return maxOf(1, (wordCount + wordsPerMinute - 1) / wordsPerMinute)
    }

    /**
     * Truncates text cleanly with ellipsis if it exceeds maxLength.
     */
    fun truncateWithEllipsis(text: String, maxLength: Int): String {
        if (text.length <= maxLength) return text
        return text.take(maxLength).trimEnd() + "..."
    }

    /**
     * Extracts the first non-blank line of text, truncating it cleanly if necessary.
     */
    fun extractFirstLine(text: String, maxLength: Int = 40, fallback: String = ""): String {
        val firstLine = text.lines().firstOrNull { it.isNotBlank() }?.trim() ?: return fallback
        return truncateWithEllipsis(firstLine, maxLength)
    }

    /**
     * Normalizes and cleans a search query.
     */
    fun cleanSearchQuery(query: String?): String {
        return query?.trim().orEmpty()
    }

    /**
     * Converts a number to Eastern Arabic-Indic numerals (٠, ١, ٢, ...).
     */
    fun toArabicDigits(number: Int): String {
        return number.toString().map { char ->
            if (char.isDigit()) ARABIC_DIGITS[char - '0'] else char
        }.joinToString("")
    }

    /**
     * Strips Arabic Tashkeel (harakat and diacritics) for fuzzy Quran / Hadith searching.
     */
    fun removeArabicDiacritics(text: String): String {
        return text.replace(ARABIC_DIACRITICS_REGEX, "")
            .replace('ٱ', 'ا')
            .replace('إ', 'ا')
            .replace('أ', 'ا')
            .replace('آ', 'ا')
            .replace('ة', 'ه')
            .replace('ى', 'ي')
    }

    fun countWords(text: String): Int = calculateWordCount(text)

    fun countCharacters(text: String, includeSpaces: Boolean = true): Int =
        calculateCharCount(text, includeSpaces)

    fun truncate(text: String, maxLength: Int): String =
        truncateWithEllipsis(text, maxLength)

    fun isNotBlank(text: String?): Boolean = !text.isNullOrBlank()
}
