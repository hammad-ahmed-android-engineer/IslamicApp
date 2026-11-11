package com.example.noorapp.core.utils.prayer

import com.example.noorapp.core.common.PrayerCalculationMethod
import com.example.noorapp.core.common.PrayerSettingsManager
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * Data holder for the 6 canonical Islamic prayer times calculated for a specific date.
 */
data class CalculatedPrayerTimes(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String
)

/**
 * High-precision offline Astronomical Islamic Prayer Times Calculator.
 *
 * Supports all major world conventions (Karachi, ISNA, MWL, Umm Al-Qura, Egyptian,
 * Tehran, Gulf, Turkey Diyanet, MUIS/JAKIM) and both Standard (Shafi'i 1x) and Hanafi (2x)
 * Asr juristic schools.
 */
object PrayerTimeCalculator {

    fun calculate(
        latitude: Double,
        longitude: Double,
        year: Int,
        month: Int,
        day: Int,
        method: PrayerCalculationMethod? = null,
        asrJuristic: Int? = null,
        timeZone: TimeZone = TimeZone.getDefault()
    ): CalculatedPrayerTimes {
        val cal = Calendar.getInstance(timeZone).apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val tzOffsetHours = timeZone.getOffset(cal.timeInMillis) / 3600000.0

        val effectiveMethod = method
            ?: PrayerSettingsManager.detectMethodFromDemographyAndCoordinates(latitude, longitude)
        val effectiveAsrSchool = asrJuristic ?: effectiveMethod.defaultAsrSchool

        val fajrAngle = effectiveMethod.fajrAngle
        val ishaAngle = effectiveMethod.ishaAngle
        val ishaFixedMinutes = effectiveMethod.ishaFixedMinutes
        val asrFactor = if (effectiveAsrSchool == 1) 2.0 else 1.0

        // 1. Calculate Julian Date at local noon
        val y = if (month <= 2) year - 1 else year
        val m = if (month <= 2) month + 12 else month
        val a = y / 100
        val b = 2 - a + a / 4
        val jd = (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + day + b - 1524.5 - (longitude / 360.0)

        val d = jd - 2451545.0

        // 2. Solar coordinates (Mean anomaly & Mean longitude)
        val meanAnomalyDeg = normalizeDegrees(357.529 + 0.98560028 * d)
        val meanAnomaly = Math.toRadians(meanAnomalyDeg)
        val meanLongitude = normalizeDegrees(280.459 + 0.98564736 * d)

        // Ecliptic longitude
        val eclipticLongitudeDeg = normalizeDegrees(
            meanLongitude + 1.915 * sin(meanAnomaly) + 0.020 * sin(2 * meanAnomaly)
        )
        val eclipticLongitude = Math.toRadians(eclipticLongitudeDeg)
        val obliquity = Math.toRadians(23.439 - 0.00000036 * d)

        // Right ascension & Declination
        val raRad = atan2(cos(obliquity) * sin(eclipticLongitude), cos(eclipticLongitude))
        val raDeg = normalizeDegrees(Math.toDegrees(raRad))
        val raHours = raDeg / 15.0

        val declinationRad = asin(sin(obliquity) * sin(eclipticLongitude))
        val declinationDeg = Math.toDegrees(declinationRad)

        // Equation of Time in hours
        val qHours = meanLongitude / 15.0
        var eqt = qHours - raHours
        while (eqt > 12.0) eqt -= 24.0
        while (eqt < -12.0) eqt += 24.0

        // Solar Noon (Midday transit)
        val noon = 12.0 + tzOffsetHours - (longitude / 15.0) - eqt

        fun hourAngle(altitudeDeg: Double): Double {
            val altRad = Math.toRadians(altitudeDeg)
            val latRad = Math.toRadians(latitude)
            val cosHa = (sin(altRad) - sin(latRad) * sin(declinationRad)) /
                    (cos(latRad) * cos(declinationRad))
            return when {
                cosHa > 1.0 -> 0.0
                cosHa < -1.0 -> 12.0
                else -> Math.toDegrees(acos(cosHa)) / 15.0
            }
        }

        // Sunrise and Sunset (Sun semi-diameter + atmospheric refraction: -0.8333°)
        val haSun = hourAngle(-0.8333)
        val sunrise = noon - haSun
        val sunset = noon + haSun

        // Fajr
        val haFajr = hourAngle(-fajrAngle)
        val fajr = noon - haFajr

        // Maghrib (Standard astronomical sunset; Tehran uses 4.5° twilight)
        val maghrib = if (effectiveMethod == PrayerCalculationMethod.TEHRAN) {
            noon + hourAngle(-4.5)
        } else {
            sunset
        }

        // Isha (either fixed interval after Maghrib for Umm Al-Qura / Gulf, or solar depression angle)
        val isha = if (ishaFixedMinutes != null) {
            maghrib + (ishaFixedMinutes / 60.0)
        } else {
            val haIsha = hourAngle(-ishaAngle)
            noon + haIsha
        }

        // Asr (Shadow angle calculation based on shadow multiplier: 1x Standard, 2x Hanafi)
        val angleDiff = Math.toRadians(abs(latitude - declinationDeg))
        val cotAlt = asrFactor + tan(angleDiff)
        val altAsrDeg = Math.toDegrees(atan(1.0 / cotAlt))
        val haAsr = hourAngle(altAsrDeg)
        val asr = noon + haAsr

        // Dhuhr (1 minute past solar zenith to ensure sun has crossed the meridian)
        val dhuhr = noon + (1.0 / 60.0)

        fun formatHours(h: Double): String {
            var hNorm = h % 24.0
            if (hNorm < 0.0) hNorm += 24.0
            val totalMinutes = (hNorm * 60.0 + 0.5).toInt()
            val hh = (totalMinutes / 60) % 24
            val mm = totalMinutes % 60
            return String.format(Locale.US, "%02d:%02d", hh, mm)
        }

        return CalculatedPrayerTimes(
            fajr = formatHours(fajr),
            sunrise = formatHours(sunrise),
            dhuhr = formatHours(dhuhr),
            asr = formatHours(asr),
            maghrib = formatHours(maghrib),
            isha = formatHours(isha)
        )
    }

    private fun normalizeDegrees(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0.0) d += 360.0
        return d
    }
}
