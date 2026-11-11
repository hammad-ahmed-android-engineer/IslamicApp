package com.example.noorapp.core.utils

import androidx.annotation.StringRes
import com.example.noorapp.R
import com.example.noorapp.domain.model.PrayerType

/**
 * Maps domain [PrayerType] values to localized English and Arabic string resource IDs.
 */
@StringRes
fun PrayerType.titleRes(): Int = when (this) {
    PrayerType.FAJR -> R.string.prayer_fajr
    PrayerType.SUNRISE -> R.string.prayer_sunrise
    PrayerType.DHUHR -> R.string.prayer_dhuhr
    PrayerType.ASR -> R.string.prayer_asr
    PrayerType.MAGHRIB -> R.string.prayer_maghrib
    PrayerType.ISHA -> R.string.prayer_isha
}

@StringRes
fun PrayerType.arabicTitleRes(): Int = when (this) {
    PrayerType.FAJR -> R.string.prayer_fajr_arabic
    PrayerType.SUNRISE -> R.string.prayer_sunrise_arabic
    PrayerType.DHUHR -> R.string.prayer_dhuhr_arabic
    PrayerType.ASR -> R.string.prayer_asr_arabic
    PrayerType.MAGHRIB -> R.string.prayer_maghrib_arabic
    PrayerType.ISHA -> R.string.prayer_isha_arabic
}

@StringRes
fun prayerNameResByKey(key: String): Int? = when (key.lowercase()) {
    "fajr" -> R.string.prayer_fajr
    "sunrise" -> R.string.prayer_sunrise
    "dhuhr" -> R.string.prayer_dhuhr
    "asr" -> R.string.prayer_asr
    "maghrib" -> R.string.prayer_maghrib
    "isha" -> R.string.prayer_isha
    else -> null
}

@StringRes
fun prayerArabicResByKey(key: String): Int? = when (key.lowercase()) {
    "fajr" -> R.string.prayer_fajr_arabic
    "sunrise" -> R.string.prayer_sunrise_arabic
    "dhuhr" -> R.string.prayer_dhuhr_arabic
    "asr" -> R.string.prayer_asr_arabic
    "maghrib" -> R.string.prayer_maghrib_arabic
    "isha" -> R.string.prayer_isha_arabic
    else -> null
}

