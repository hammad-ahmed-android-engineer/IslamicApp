package com.ne.muslimprayers.qiblafinder.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ne.muslimprayers.qiblafinder.data.remote.dto.ScheduleResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

/**
 * Dedicated local storage cache for monthly prayer schedules and calendar data.
 * Keeps previously fetched online schedules persistently on disk keyed by coordinates,
 * calculation method, juristic school, and Hijri adjustment.
 */
@Singleton
class PrayerLocalCache @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val tag = "PrayerLocalCache"
    private val cacheDir: File by lazy {
        File(context.filesDir, "prayer_cache").apply { mkdirs() }
    }

    private fun getCacheKey(
        lat: Double,
        lng: Double,
        year: Int,
        month: Int,
        methodId: Int,
        school: Int,
        hijriOffset: Int
    ): String {
        val latKey = (lat * 10).roundToInt()
        val lngKey = (lng * 10).roundToInt()
        return "cal_${latKey}_${lngKey}_${year}_${month}_m${methodId}_s${school}_h${hijriOffset}.json"
    }

    suspend fun saveCalendar(
        lat: Double,
        lng: Double,
        year: Int,
        month: Int,
        data: List<ScheduleResponse>,
        methodId: Int = 3,
        school: Int = 0,
        hijriOffset: Int = 0
    ) = withContext(Dispatchers.IO) {
        if (data.isEmpty()) return@withContext
        try {
            val file = File(cacheDir, getCacheKey(lat, lng, year, month, methodId, school, hijriOffset))
            val json = gson.toJson(data)
            file.writeText(json)
        } catch (e: Exception) {
            Log.e(tag, "Failed to cache prayer calendar: ${e.message}")
        }
    }

    suspend fun getCalendar(
        lat: Double,
        lng: Double,
        year: Int,
        month: Int,
        methodId: Int = 3,
        school: Int = 0,
        hijriOffset: Int = 0
    ): List<ScheduleResponse>? = withContext(Dispatchers.IO) {
        try {
            val file = File(cacheDir, getCacheKey(lat, lng, year, month, methodId, school, hijriOffset))
            if (file.exists() && file.length() > 0) {
                val json = file.readText()
                val type = object : TypeToken<List<ScheduleResponse>>() {}.type
                return@withContext gson.fromJson<List<ScheduleResponse>>(json, type)
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to read cached prayer calendar: ${e.message}")
        }
        null
    }
}
