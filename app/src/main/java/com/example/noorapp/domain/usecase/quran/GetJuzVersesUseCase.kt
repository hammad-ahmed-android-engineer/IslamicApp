package com.example.noorapp.domain.usecase.quran

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.model.JuzStartBoundary
import com.example.noorapp.domain.repository.QuranRepository
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

    suspend fun getJuzStartBoundaries(): Map<Int, JuzStartBoundary> =
        quranRepository.getJuzStartBoundaries()
}

