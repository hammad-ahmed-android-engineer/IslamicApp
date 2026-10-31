package com.ne.muslimprayers.qiblafinder.domain.usecase.hadith

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
import com.ne.muslimprayers.qiblafinder.domain.repository.HadithRepository
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
}
