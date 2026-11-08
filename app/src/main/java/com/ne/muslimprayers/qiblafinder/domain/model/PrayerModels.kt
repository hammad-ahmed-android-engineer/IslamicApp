package com.ne.muslimprayers.qiblafinder.domain.model

enum class PrayerType(val displayName: String, val audioResName: String) {
    FAJR("Fajr", "adzan_fajr"),
    SUNRISE("Sunrise", ""),
    DHUHR("Dhuhr", "adzan_makkah"),
    ASR("Asr", "adzan_makkah"),
    MAGHRIB("Maghrib", "adzan_makkah"),
    ISHA("Isha", "adzan_makkah")
}

data class SinglePrayer(
    val type: PrayerType,
    val time24: String,
    val timeFormatted: String,
    val isNext: Boolean = false,
    val isAlarmActive: Boolean = false
)

data class PrayerDaySchedule(
    val dateGregorian: String,
    val dateHijri: String,
    val fajr: SinglePrayer,
    val sunrise: SinglePrayer,
    val dhuhr: SinglePrayer,
    val asr: SinglePrayer,
    val maghrib: SinglePrayer,
    val isha: SinglePrayer,
    val nextPrayer: SinglePrayer? = null,
    val currentPrayer: SinglePrayer? = null,
    val remainingMillisToNextPrayer: Long = 0L,
    val nextPrayerTargetEpochMillis: Long = 0L
) {
    val allPrayers: List<SinglePrayer>
        get() = listOf(fajr, sunrise, dhuhr, asr, maghrib, isha)

    val prayers: List<SinglePrayer>
        get() = allPrayers

    val mainSalawat: List<SinglePrayer>
        get() = listOf(fajr, dhuhr, asr, maghrib, isha)
}

data class IslamicCalendarInfo(
    val gregorianDay: Int,
    val gregorianMonth: Int,
    val gregorianYear: Int,
    val gregorianFormatted: String,
    val hijriDay: Int,
    val hijriMonthName: String,
    val hijriYear: Int,
    val hijriFormatted: String,
    val weekday: String,
    val holidays: List<String>,
    val prayerTimings: List<SinglePrayer>
)

data class PrayerAlarmItem(
    val id: Int,
    val prayerType: PrayerType,
    val prayerTime: String,
    val isAlarmActive: Boolean
)

data class QiblaState(
    val qiblaBearing: Float = 0f,
    val compassAzimuth: Float = 0f,
    val needleAngle: Float = 0f,
    val isFacingQibla: Boolean = false,
    val userLatitude: Double = 0.0,
    val userLongitude: Double = 0.0,
    val cityName: String = "",
    val isLocationAvailable: Boolean = false,
    val distanceToKaabaKm: Double = 0.0,
    val sensorAccuracy: Int = 3,
    val isSensorAvailable: Boolean = true
)
