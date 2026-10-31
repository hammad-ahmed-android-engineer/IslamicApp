package com.ne.muslimprayers.qiblafinder.data.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.data.remote.api.QuranApiService
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [QuranRepository] communicating with [QuranApiService].
 */
@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val quranApiService: QuranApiService
) : QuranRepository {

    override fun getSurahList(): Flow<Resource<List<Surah>>> = flow {
        emit(Resource.Loading(true))
        try {
            val response = quranApiService.getSurahs()
            val list = response.data.map { dto ->
                Surah(
                    number = dto.number,
                    name = dto.englishName,
                    arabicName = dto.name,
                    englishMeaning = dto.englishNameTranslation,
                    totalVerses = dto.numberOfAyahs,
                    revelationType = dto.revelationType
                )
            }
            emit(Resource.Success(list))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to load Holy Quran surahs from API"))
        }
    }

    override fun getSurahVerses(surahNumber: Int): Flow<Resource<List<Ayah>>> = flow {
        emit(Resource.Loading(true))
        try {
            // 1. Primary: Fetch high-fidelity text with both Uthmani & Indo-Pak scripts and authentic Waqf marks from Quran.com API v4
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
                        globalAyahNumber = v.id
                    )
                }
                emit(Resource.Success(verses))
                return@flow
            }

            // 2. Resilient Fallback: AlQuran.cloud API
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
                    globalAyahNumber = arabicAyah.number
                )
            }
            emit(Resource.Success(combined))
        } catch (e: Exception) {
            emit(Resource.Error("Could not load verses: ${e.localizedMessage ?: "Network error"}"))
        }
    }
}
