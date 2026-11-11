package com.example.noorapp.domain.usecase.quran

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.Surah
import com.example.noorapp.domain.repository.QuranRepository
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
