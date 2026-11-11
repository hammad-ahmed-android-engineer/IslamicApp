package com.example.noorapp.core.utils.prayer

import java.util.Calendar

/**
 * Result data for offline Islamic lunar calendar conversion.
 */
data class HijriDateResult(
    val day: Int,
    val monthNumber: Int,
    val monthName: String,
    val year: Int,
    val formatted: String,
    val holidays: List<String>
)

/**
 * Offline Astronomical Hijri Calendar Calculator.
 *
 * Implements the standard Umm Al-Qura / Julian Day Islamic lunar epoch conversion algorithm
 * with support for user-configured Hijri day offset (-2 to +2 days).
 */
object HijriCalendarCalculator {

    private val HIJRI_MONTH_NAMES = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    fun calculate(
        year: Int,
        month: Int,
        day: Int,
        hijriOffsetDays: Int = 0
    ): HijriDateResult {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            if (hijriOffsetDays != 0) {
                add(Calendar.DAY_OF_MONTH, hijriOffsetDays)
            }
        }
        var y = cal.get(Calendar.YEAR)
        var m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)

        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + a / 4
        val jd = (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + d + b - 1524

        // Julian Day to Islamic Epoch (JD 1948439.5 - July 16, 622 AD)
        var jdIslamic = jd - 1948440 + 10632
        val n = (jdIslamic - 1) / 10631
        jdIslamic = jdIslamic - 10631 * n + 354
        val j = ((10985 - jdIslamic) / 5316) * ((50 * jdIslamic) / 17719) +
                (jdIslamic / 5670) * ((43 * jdIslamic) / 15238)
        jdIslamic = jdIslamic - ((30 - j) / 15) * ((17719 * j) / 50) -
                (j / 16) * ((15238 * j) / 43) + 29
        val hMonth = (24 * jdIslamic) / 709
        val hDay = jdIslamic - (709 * hMonth) / 24
        val hYear = 30 * n + j - 30

        val monthName = HIJRI_MONTH_NAMES.getOrElse(hMonth - 1) { "Islamic Month" }
        val formatted = "$hDay $monthName $hYear AH"

        val holidays = mutableListOf<String>()
        if (hMonth == 1 && hDay == 1) holidays.add("Islamic New Year")
        if (hMonth == 1 && hDay == 10) holidays.add("Day of Ashura")
        if (hMonth == 3 && hDay == 12) holidays.add("Mawlid al-Nabi")
        if (hMonth == 7 && hDay == 27) holidays.add("Isra and Mi'raj")
        if (hMonth == 8 && hDay == 15) holidays.add("Mid-Sha'ban (Shab-e-Barat)")
        if (hMonth == 9 && hDay == 1) holidays.add("First Day of Ramadan")
        if (hMonth == 9 && hDay == 27) holidays.add("Laylat al-Qadr")
        if (hMonth == 10 && hDay == 1) holidays.add("Eid al-Fitr")
        if (hMonth == 12 && hDay == 9) holidays.add("Day of Arafah")
        if (hMonth == 12 && hDay == 10) holidays.add("Eid al-Adha")

        return HijriDateResult(
            day = hDay,
            monthNumber = hMonth,
            monthName = monthName,
            year = hYear,
            formatted = formatted,
            holidays = holidays
        )
    }
}
