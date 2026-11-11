package com.example.noorapp.core.utils

import android.content.Context
import com.example.noorapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Centralized date, time, and countdown utilities across Islamic App.
 * Handles prayer time conversions, formatting, and human-friendly relative timestamps.
 */
object DateTimeUtils {

    private const val PATTERN_DATE = "MMM d, yyyy"
    private const val PATTERN_DATE_TIME = "MMM d, yyyy - h:mm a"
    private const val PATTERN_FULL_DATE = "EEEE, MMMM d, yyyy"
    private const val PATTERN_DAY_KEY = "yyyy-MM-dd"
    private const val PATTERN_MONTH_DAY_YEAR = "MMMM d, yyyy"
    private const val PATTERN_TIME_12H = "hh:mm a"
    private const val PATTERN_TIME_24H = "HH:mm"

    /**
     * Converts a 24-hour time string (e.g. "14:30") to 12-hour formatted string (e.g. "02:30 PM").
     */
    fun formatTo12Hour(time24: String, locale: Locale = Locale.US): String {
        return try {
            val sdf24 = SimpleDateFormat(PATTERN_TIME_24H, locale)
            val sdf12 = SimpleDateFormat(PATTERN_TIME_12H, locale)
            val date = sdf24.parse(time24.trim())
            if (date != null) sdf12.format(date) else time24
        } catch (_: Exception) {
            time24
        }
    }

    /**
     * Formats milliseconds into a digital countdown string "00h 00m 00s".
     */
    fun formatCountdown(remainingMillis: Long): String {
        val totalSeconds = (remainingMillis / 1000).coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02dh %02dm %02ds", hours, minutes, seconds)
    }

    /**
     * Converts HH:mm time string into total minutes from midnight.
     */
    fun timeToMinutes(timeStr: String): Int {
        val parts = timeStr.trim().split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }

    /**
     * Formats timestamp to standard readable date (e.g. "Oct 24, 2026").
     */
    fun formatDate(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_DATE, locale).format(Date(timestamp))
    }

    /**
     * Formats timestamp to date and time (e.g. "Oct 24, 2026 - 1:45 PM").
     */
    fun formatDateTime(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_DATE_TIME, locale).format(Date(timestamp))
    }

    /**
     * Formats timestamp to full executive date (e.g. "Saturday, October 24, 2026").
     */
    fun formatFullDate(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_FULL_DATE, locale).format(Date(timestamp))
    }

    /**
     * Formats timestamp to month day year string (e.g. "October 24, 2026").
     */
    fun formatMonthDayYear(timestamp: Long, locale: Locale = Locale.getDefault()): String {
        return SimpleDateFormat(PATTERN_MONTH_DAY_YEAR, locale).format(Date(timestamp))
    }

    /**
     * Generates a standardized ISO-style date key (e.g. "2026-10-24").
     */
    fun formatDayKey(timestamp: Long = System.currentTimeMillis(), locale: Locale = Locale.US): String {
        return SimpleDateFormat(PATTERN_DAY_KEY, locale).format(Date(timestamp))
    }

    /**
     * Generates a standardized ISO-style date key from a Date object.
     */
    fun formatDayKey(date: Date, locale: Locale = Locale.US): String {
        return SimpleDateFormat(PATTERN_DAY_KEY, locale).format(date)
    }

    /**
     * Parses an ISO-style date key ("yyyy-MM-dd") into a Date object safely.
     */
    fun parseDayKey(dayKey: String, locale: Locale = Locale.US): Date? {
        return try {
            SimpleDateFormat(PATTERN_DAY_KEY, locale).parse(dayKey.trim())
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Formats timestamp to human-friendly relative time description.
     */
    fun formatRelativeTime(context: Context, timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour

        return when {
            diff < 0L -> context.getString(R.string.relative_just_now)
            diff < minute -> context.getString(R.string.relative_just_now)
            diff < hour -> context.getString(R.string.relative_minutes_ago, (diff / minute).toInt())
            diff < day -> context.getString(R.string.relative_hours_ago, (diff / hour).toInt())
            diff < 2 * day -> context.getString(R.string.relative_yesterday)
            diff < 7 * day -> context.getString(R.string.relative_days_ago, (diff / day).toInt())
            else -> formatDate(timestamp)
        }
    }
}
