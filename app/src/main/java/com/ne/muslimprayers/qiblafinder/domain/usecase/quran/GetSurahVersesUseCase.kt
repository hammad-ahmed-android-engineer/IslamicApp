package com.ne.muslimprayers.qiblafinder.domain.usecase.quran

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve Verses (Ayahs) for a given Surah number.
 */
class GetSurahVersesUseCase @Inject constructor(
    private val quranRepository: QuranRepository
) {
    operator fun invoke(surahNumber: Int): Flow<Resource<List<Ayah>>> {
        return quranRepository.getSurahVerses(surahNumber)
    }
}
