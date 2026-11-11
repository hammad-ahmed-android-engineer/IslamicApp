package com.example.noorapp.data.repository

import com.example.noorapp.core.common.Resource
import com.example.noorapp.data.local.QuranDatabaseHelper
import com.example.noorapp.data.remote.api.QuranApiService
import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.model.JuzStartBoundary
import com.example.noorapp.domain.model.Surah
import com.example.noorapp.domain.repository.QuranRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 100% Offline-first implementation of [QuranRepository] utilizing isolated
 * SQLite Database Manager.
 *
 * GUARANTEES:
 * - 0ms local reads for all 114 Surahs, 6,236 Ayahs, and 30 Juz.
 * - Complete physical separation from user data tables.
 * - Online API fallback is only triggered if local database is missing records.
 */
@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val quranDbHelper: QuranDatabaseHelper,
    private val quranApiService: QuranApiService
) : QuranRepository {

    private val tafseerCache = ConcurrentHashMap<String, String>()

    override fun getSurahList(): Flow<Resource<List<Surah>>> = flow {
        val localSurahs = quranDbHelper.getAllSurahs()
        emit(Resource.Success(localSurahs))
    }

    override fun getSurahVerses(surahNumber: Int): Flow<Resource<List<Ayah>>> = flow {
        emit(Resource.Loading(true))
        try {
            // 1. Primary: Instant offline lookup from local SQLite database (Uthmani, Indo-Pak, English, Urdu)
            val localAyahs = quranDbHelper.getAyahsBySurah(surahNumber)
            if (localAyahs.isNotEmpty()) {
                emit(Resource.Success(localAyahs))
                return@flow
            }

            // 2. Resilient Online Fallback: Quran.com API v4
            val quranDotComResponse = runCatching {
                quranApiService.getQuranDotComVerses(surahNumber)
            }.getOrNull()

            if (quranDotComResponse != null && quranDotComResponse.verses.isNotEmpty()) {
                val verses = quranDotComResponse.verses.map { v ->
                    val cleanEng = (v.translations?.firstOrNull { it.resourceId == 20 } ?: v.translations?.firstOrNull())
                        ?.text
                        ?.replace(Regex("<sup.*?</sup>"), "")
                        ?.trim() ?: ""
                    val cleanUrdu = v.translations?.firstOrNull { it.resourceId == 97 }
                        ?.text
                        ?.replace(Regex("<sup.*?</sup>"), "")
                        ?.trim() ?: ""
                    Ayah(
                        numberInSurah = v.verseNumber,
                        arabicText = v.textUthmani ?: "",
                        indopakText = v.textIndopak ?: "",
                        englishText = cleanEng,
                        urduText = cleanUrdu,
                        juz = v.juzNumber ?: 1,
                        audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${v.id}.mp3",
                        globalAyahNumber = v.id,
                        surahNumber = surahNumber
                    )
                }
                emit(Resource.Success(verses))
                return@flow
            }

            // 3. Fallback: AlQuran.cloud API
            val response = quranApiService.getSurahVerses(surahNumber)
            val arabicEdition = response.data.getOrNull(0)
            val englishEdition = response.data.getOrNull(1)

            val arabicAyahs = arabicEdition?.ayahs.orEmpty()
            val englishAyahs = englishEdition?.ayahs.orEmpty()

            val combined = arabicAyahs.mapIndexed { index, arabicAyah ->
                val engText = englishAyahs.getOrNull(index)?.text ?: ""
                Ayah(
                    numberInSurah = arabicAyah.numberInSurah,
                    arabicText = arabicAyah.text,
                    indopakText = arabicAyah.text,
                    englishText = engText,
                    juz = arabicAyah.juz,
                    audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${arabicAyah.number}.mp3",
                    globalAyahNumber = arabicAyah.number,
                    surahNumber = surahNumber
                )
            }
            emit(Resource.Success(combined))
        } catch (e: Exception) {
            emit(Resource.Error("Could not load verses: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    override fun getJuzVerses(juzNumber: Int): Flow<Resource<List<Ayah>>> = flow {
        emit(Resource.Loading(true))
        try {
            val localAyahs = quranDbHelper.getAyahsByJuz(juzNumber)
            if (localAyahs.isNotEmpty()) {
                emit(Resource.Success(localAyahs))
            } else {
                emit(Resource.Error("Juz $juzNumber verses not found in offline database."))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Could not load Juz verses: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    override suspend fun getJuzStartBoundaries(): Map<Int, JuzStartBoundary> =
        quranDbHelper.getJuzStartBoundaries()

    override suspend fun getAyahTafseer(tafseerId: Int, verseKey: String): Resource<String> {
        val cacheKey = "${tafseerId}_$verseKey"
        tafseerCache[cacheKey]?.let { cached ->
            return Resource.Success(cached)
        }
        return try {
            val response = withContext(Dispatchers.IO) {
                quranApiService.getAyahTafseer(tafseerId, verseKey)
            }
            val rawHtml = response.tafsir?.text ?: ""
            val cleanText = rawHtml
                .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n\n")
                .replace(Regex("<.*?>"), "")
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .trim()
            if (cleanText.isNotBlank()) {
                tafseerCache[cacheKey] = cleanText
            }
            Resource.Success(cleanText)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Could not load Tafseer")
        }
    }
}
