package com.example.noorapp.domain.repository

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.Hadith
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for fetching authentic Hadith collections.
 */
interface HadithRepository {
    /**
     * Fetches Hadiths for a specific collection edition and section chapter.
     */
    fun getHadithList(edition: String = "eng-bukhari", section: Int = 1): Flow<Resource<List<Hadith>>>

    /**
     * Fetches live Hadith entries for a specific section.
     */
    fun fetchLiveHadiths(edition: String = "eng-bukhari", section: Int = 1): Flow<Resource<List<Hadith>>> =
        getHadithList(edition, section)

    /**
     * Retrieves the localized/readable section title from local storage if available.
     */
    suspend fun getSectionTitle(edition: String, section: Int): String?
}
