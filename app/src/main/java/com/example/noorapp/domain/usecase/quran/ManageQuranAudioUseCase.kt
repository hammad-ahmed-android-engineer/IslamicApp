package com.example.noorapp.domain.usecase.quran

import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.model.QuranDownloadStatus
import com.example.noorapp.domain.model.Surah
import com.example.noorapp.domain.repository.QuranAudioRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ManageQuranAudioUseCase @Inject constructor(
    private val audioRepository: QuranAudioRepository
) {
    val downloadStatus: StateFlow<QuranDownloadStatus>
        get() = audioRepository.downloadStatus

    fun isSurahDownloaded(reciterId: String, surahNumber: Int, totalVerses: Int): Boolean =
        audioRepository.isSurahDownloaded(reciterId, surahNumber, totalVerses)

    fun isJuzDownloaded(reciterId: String, juzNumber: Int): Boolean =
        audioRepository.isJuzDownloaded(reciterId, juzNumber)

    fun getTotalAudioStorageMb(): Double = audioRepository.getTotalAudioStorageMb()

    suspend fun downloadSurahAudio(surah: Surah, verses: List<Ayah>, reciterId: String): Boolean =
        audioRepository.downloadSurahAudio(surah, verses, reciterId)

    suspend fun downloadJuzAudio(juzNumber: Int, juzTitle: String, verses: List<Ayah>, reciterId: String): Boolean =
        audioRepository.downloadJuzAudio(juzNumber, juzTitle, verses, reciterId)

    fun cancelDownload() = audioRepository.cancelDownload()

    fun deleteSurahAudio(reciterId: String, surahNumber: Int): Boolean =
        audioRepository.deleteSurahAudio(reciterId, surahNumber)

    fun deleteAllAudio(): Boolean = audioRepository.deleteAllAudio()

    fun playAyahAudio(
        ayah: Ayah,
        surahNumber: Int,
        reciterId: String,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit,
        onError: () -> Unit
    ) {
        audioRepository.playAyahAudio(ayah, surahNumber, reciterId, onPrepared, onCompleted, onError)
    }

    fun stopAyahAudio() = audioRepository.stopAyahAudio()
}

