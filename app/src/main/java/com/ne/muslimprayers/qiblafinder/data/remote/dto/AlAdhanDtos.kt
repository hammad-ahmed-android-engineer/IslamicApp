package com.ne.muslimprayers.qiblafinder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PrayerScheduleResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("data")
    val data: List<ScheduleResponse>? = null
)

data class ScheduleResponse(
    @SerializedName("timings")
    val timingResponse: TimingResponse? = null,
    @SerializedName("date")
    val dateResponse: DateResponse? = null,
    @SerializedName("meta")
    val metaResponse: MetaResponse? = null
)

data class TimingResponse(
    @SerializedName("Fajr")
    val fajr: String? = null,
    @SerializedName("Sunrise")
    val sunrise: String? = null,
    @SerializedName("Dhuhr")
    val dhuhr: String? = null,
    @SerializedName("Asr")
    val asr: String? = null,
    @SerializedName("Sunset")
    val sunset: String? = null,
    @SerializedName("Maghrib")
    val maghrib: String? = null,
    @SerializedName("Isha")
    val isha: String? = null,
    @SerializedName("Imsak")
    val imsak: String? = null,
    @SerializedName("Midnight")
    val midnight: String? = null
)

data class DateResponse(
    @SerializedName("readable")
    val readable: String? = null,
    @SerializedName("timestamp")
    val timestamp: String? = null,
    @SerializedName("gregorian")
    val gregorian: CalendarFormatResponse? = null,
    @SerializedName("hijri")
    val hijri: CalendarFormatResponse? = null
)

data class CalendarFormatResponse(
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("format")
    val format: String? = null,
    @SerializedName("day")
    val day: String? = null,
    @SerializedName("weekday")
    val weekdayResponse: WeekdayResponse? = null,
    @SerializedName("month")
    val monthResponse: MonthResponse? = null,
    @SerializedName("year")
    val year: String? = null,
    @SerializedName("holidays")
    val holidays: List<String>? = null
) {
    val weekday: WeekdayResponse?
        get() = weekdayResponse

    val month: MonthResponse?
        get() = monthResponse
}

data class WeekdayResponse(
    @SerializedName("en")
    val en: String? = null,
    @SerializedName("ar")
    val ar: String? = null
)

data class MonthResponse(
    @SerializedName("number")
    val number: Int? = null,
    @SerializedName("en")
    val en: String? = null,
    @SerializedName("ar")
    val ar: String? = null
)

data class MetaResponse(
    @SerializedName("latitude")
    val latitude: Double? = null,
    @SerializedName("longitude")
    val longitude: Double? = null,
    @SerializedName("timezone")
    val timezone: String? = null
)
