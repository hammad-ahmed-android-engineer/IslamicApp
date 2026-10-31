package com.ne.muslimprayers.qiblafinder.domain.usecase.quran

import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.domain.repository.QuranRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to retrieve the list of Holy Quran Surahs.
 */
class GetQuranSurahsUseCase @Inject constructor(
    private val quranRepository: QuranRepository
) {
    operator fun invoke(): Flow<Resource<List<Surah>>> {
        return quranRepository.getSurahList()
    }
}
