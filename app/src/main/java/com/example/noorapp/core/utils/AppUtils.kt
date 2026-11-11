package com.example.noorapp.core.utils

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import com.example.noorapp.R
import java.util.Locale

/**
 * Centralized AppUtils facade providing clean, unified access to all application utilities.
 *
 * Developers can either access modular namespaces (`AppUtils.dateTime`, `AppUtils.string`,
 * `AppUtils.validation`, `AppUtils.ui`, `AppUtils.color`, `AppUtils.clipboard`, `AppUtils.share`,
 * `AppUtils.location`) or call top-level convenience methods directly.
 */
object AppUtils {

    val dateTime: DateTimeUtils = DateTimeUtils
    val string: StringUtils = StringUtils
    val validation: ValidationUtils = ValidationUtils
    val ui: UiUtils = UiUtils
    val color: ColorUtils = ColorUtils
    val clipboard: ClipboardUtils = ClipboardUtils
    val share: ShareUtils = ShareUtils
    val location: LocationUtils = LocationUtils

    // --- Date & Time Conveniences ---
    fun formatTo12Hour(time24: String, locale: Locale = Locale.US): String =
        DateTimeUtils.formatTo12Hour(time24, locale)

    fun formatCountdown(remainingMillis: Long): String =
        DateTimeUtils.formatCountdown(remainingMillis)

    fun timeToMinutes(timeStr: String): Int =
        DateTimeUtils.timeToMinutes(timeStr)

    fun formatDate(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatDate(timestamp, locale)

    fun formatDateTime(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatDateTime(timestamp, locale)

    fun formatFullDate(timestamp: Long, locale: Locale = Locale.getDefault()): String =
        DateTimeUtils.formatFullDate(timestamp, locale)

    fun formatRelativeTime(context: Context, timestamp: Long): String =
        DateTimeUtils.formatRelativeTime(context, timestamp)

    fun formatDayKey(timestamp: Long = System.currentTimeMillis()): String =
        DateTimeUtils.formatDayKey(timestamp)

    // --- String & Text Conveniences ---
    fun calculateWordCount(text: String): Int =
        StringUtils.calculateWordCount(text)

    fun calculateCharCount(text: String, includeSpaces: Boolean = true): Int =
        StringUtils.calculateCharCount(text, includeSpaces)

    fun calculateReadingTimeMinutes(wordCount: Int, wordsPerMinute: Int = 200): Int =
        StringUtils.calculateReadingTimeMinutes(wordCount, wordsPerMinute)

    fun truncate(text: String, maxLength: Int): String =
        StringUtils.truncateWithEllipsis(text, maxLength)

    fun cleanSearchQuery(query: String?): String =
        StringUtils.cleanSearchQuery(query)

    fun toArabicDigits(number: Int): String =
        StringUtils.toArabicDigits(number)

    fun removeArabicDiacritics(text: String): String =
        StringUtils.removeArabicDiacritics(text)

    // --- Validation Conveniences ---
    fun isValidCoordinates(latitude: Double?, longitude: Double?): Boolean =
        ValidationUtils.isValidCoordinates(latitude, longitude)

    fun isValidTitle(title: String?): Boolean =
        ValidationUtils.isValidTitle(title)

    fun isValidUrl(url: String?): Boolean =
        ValidationUtils.isValidUrl(url)

    fun normalizeUrl(url: String): String =
        ValidationUtils.normalizeUrl(url)

    fun isValidHexColor(hex: String?): Boolean =
        ColorUtils.isValidHexColor(hex)

    // --- UI & Orientation Conveniences ---
    fun isLandscape(configuration: Configuration): Boolean =
        UiUtils.isLandscape(configuration)

    fun getGridColumnCount(configuration: Configuration, portraitColumns: Int = 2, landscapeColumns: Int = 3): Int =
        UiUtils.getGridColumnCount(configuration, portraitColumns, landscapeColumns)

    fun getGridColumnCount(isLandscape: Boolean, portraitColumns: Int = 2, landscapeColumns: Int = 3): Int =
        UiUtils.getGridColumnCount(isLandscape, portraitColumns, landscapeColumns)

    // --- Color Conveniences ---
    fun parseHexColor(hex: String?, fallback: Color = Color.Unspecified): Color =
        ColorUtils.parseHexColor(hex, fallback)

    fun toHexColor(color: Color): String =
        ColorUtils.toHexColor(color)

    // --- Clipboard & Share Conveniences ---
    fun copyToClipboard(
        context: Context,
        label: String,
        text: String,
        toastMessage: String = context.getString(R.string.copied_to_clipboard)
    ) = ClipboardUtils.copyToClipboard(context, label, text, toastMessage)

    fun shareText(
        context: Context,
        text: String,
        chooserTitle: String = context.getString(R.string.share_via)
    ) = ShareUtils.shareText(context, text, chooserTitle)

    // --- Location Conveniences ---
    fun hasLocationPermission(context: Context): Boolean =
        LocationUtils.hasLocationPermission(context)

    fun isLocationEnabled(context: Context): Boolean =
        LocationUtils.isLocationEnabled(context)
}
