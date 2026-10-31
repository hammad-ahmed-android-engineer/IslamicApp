package com.ne.muslimprayers.qiblafinder.domain.repository

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
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
}
