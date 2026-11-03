package com.ne.muslimprayers.qiblafinder.domain.usecase.quran

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve all Verses (Ayahs) belonging to an entire Juz (1 to 30).
 */
class GetJuzVersesUseCase @Inject constructor(
    private val quranRepository: QuranRepository
) {
    operator fun invoke(juzNumber: Int): Flow<Resource<List<Ayah>>> {
        return quranRepository.getJuzVerses(juzNumber)
    }
}

