package com.ne.muslimprayers.qiblafinder.core.utils

/**
 * Centralized validation utilities for user input, URLs, colors, and coordinates.
 * Pure Kotlin implementation for JVM unit-testability and safety.
 */
object ValidationUtils {

    private val URL_REGEX = Regex("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", RegexOption.IGNORE_CASE)

    /**
     * Validates whether latitude and longitude are within legitimate geographical bounds.
     */
    fun isValidCoordinates(latitude: Double?, longitude: Double?): Boolean {
        if (latitude == null || longitude == null) return false
        return latitude in -90.0..90.0 && longitude in -180.0..180.0 && !(latitude == 0.0 && longitude == 0.0)
    }

    /**
     * Validates whether a title or identifier is non-empty and within bounds.
     */
    fun isValidTitle(title: String?, maxLength: Int = 250): Boolean {
        return !title.isNullOrBlank() && title.trim().length <= maxLength
    }

    /**
     * Validates a hex color code.
     */
    fun isValidHexColor(hex: String?): Boolean {
        return ColorUtils.isValidHexColor(hex)
    }

    /**
     * Validates whether a string is a valid web URL.
     */
    fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val normalized = normalizeUrl(url)
        return URL_REGEX.matches(normalized)
    }

    /**
     * Normalizes a web URL by ensuring it contains an http:// or https:// prefix.
     */
    fun normalizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://", ignoreCase = true) &&
            !trimmed.startsWith("https://", ignoreCase = true)
        ) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }
}
