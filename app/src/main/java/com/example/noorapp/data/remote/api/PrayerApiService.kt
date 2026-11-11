package com.example.noorapp.data.remote.api

import com.example.noorapp.data.remote.dto.PrayerScheduleResponse
import com.example.noorapp.data.remote.dto.QiblaApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerApiService {
    @GET("calendar")
    suspend fun getCalendar(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("method") method: Int = 3,
        @Query("school") school: Int = 0,
        @Query("adjustment") adjustment: Int = 0
    ): PrayerScheduleResponse

    @GET("qibla/{latitude}/{longitude}")
    suspend fun getQibla(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): QiblaApiResponse
}
