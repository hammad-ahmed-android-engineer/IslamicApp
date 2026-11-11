package com.example.noorapp.domain.usecase.quran

import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.repository.QuranRepository
import javax.inject.Inject

/**
 * Use case to retrieve and cache Tafseer commentary for a Quran Ayah.
 */
class GetAyahTafseerUseCase @Inject constructor(
    private val quranRepository: QuranRepository
) {
    suspend operator fun invoke(tafseerId: Int, verseKey: String): Resource<String> {
        return quranRepository.getAyahTafseer(tafseerId, verseKey)
    }
}

