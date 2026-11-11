package com.example.noorapp.domain.usecase.hadith

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.Hadith
import com.example.noorapp.domain.repository.HadithRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to fetch authentic Hadiths by edition and section chapter.
 */
class GetHadithCollectionUseCase @Inject constructor(
    private val hadithRepository: HadithRepository
) {
    operator fun invoke(edition: String = "eng-bukhari", section: Int = 1): Flow<Resource<List<Hadith>>> {
        return hadithRepository.fetchLiveHadiths(edition, section)
    }

    suspend fun getSectionTitle(edition: String, section: Int): String? {
        return hadithRepository.getSectionTitle(edition, section)
    }
}
