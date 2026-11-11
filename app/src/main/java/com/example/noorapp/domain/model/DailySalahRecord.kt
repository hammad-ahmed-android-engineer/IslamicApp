package com.example.noorapp.domain.model

data class DailySalahRecord(
    val date: String, // "yyyy-MM-dd"
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false
) {
    val completedCount: Int
        get() = (if (fajr) 1 else 0) + (if (dhuhr) 1 else 0) + (if (asr) 1 else 0) + (if (maghrib) 1 else 0) + (if (isha) 1 else 0)

    val isAllCompleted: Boolean
        get() = completedCount == 5

    fun isPrayerOffered(type: PrayerType): Boolean {
        return when (type) {
            PrayerType.FAJR -> fajr
            PrayerType.DHUHR -> dhuhr
            PrayerType.ASR -> asr
            PrayerType.MAGHRIB -> maghrib
            PrayerType.ISHA -> isha
            else -> false
        }
    }

    fun copyWithPrayer(type: PrayerType, offered: Boolean): DailySalahRecord {
        return when (type) {
            PrayerType.FAJR -> copy(fajr = offered)
            PrayerType.DHUHR -> copy(dhuhr = offered)
            PrayerType.ASR -> copy(asr = offered)
            PrayerType.MAGHRIB -> copy(maghrib = offered)
            PrayerType.ISHA -> copy(isha = offered)
            else -> this
        }
    }
}
