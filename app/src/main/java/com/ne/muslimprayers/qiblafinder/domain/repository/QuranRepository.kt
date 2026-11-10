package com.ne.muslimprayers.qiblafinder.domain.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.JuzStartBoundary
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for fetching Holy Quran Surahs, Juz, and Verses.
 */
interface QuranRepository {
    /**
     * Fetches the complete list of Quran Surahs.
     */
    fun getSurahList(): Flow<Resource<List<Surah>>>

    /**
     * Fetches the verses (Ayahs) with Arabic, Indo-Pak, and translations for a specific Surah.
     */
    fun getSurahVerses(surahNumber: Int): Flow<Resource<List<Ayah>>>

    /**
     * Fetches all verses (Ayahs) belonging to a specific Juz (1 to 30) for entire Juz reading.
     */
    fun getJuzVerses(juzNumber: Int): Flow<Resource<List<Ayah>>>

    /**
     * Fetches the dynamic starting (Surah, Ayah) boundary for all 30 Juz from the Quran structure.
     */
    suspend fun getJuzStartBoundaries(): Map<Int, JuzStartBoundary>
}
