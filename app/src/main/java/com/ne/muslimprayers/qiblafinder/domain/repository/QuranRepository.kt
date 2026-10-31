package com.ne.muslimprayers.qiblafinder.domain.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for fetching Holy Quran Surahs and Verses.
 */
interface QuranRepository {
    /**
     * Fetches the complete list of Quran Surahs.
     */
    fun getSurahList(): Flow<Resource<List<Surah>>>

    /**
     * Fetches the verses (Ayahs) with Arabic and English translation for a specific Surah.
     */
    fun getSurahVerses(surahNumber: Int): Flow<Resource<List<Ayah>>>
}
