package com.example.noorapp.domain.usecase.quran

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.repository.QuranRepository
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
