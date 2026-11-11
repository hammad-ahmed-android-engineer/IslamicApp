package com.example.noorapp.domain.repository

import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.model.QuranDownloadStatus
import com.example.noorapp.domain.model.Surah
import kotlinx.coroutines.flow.StateFlow

interface QuranAudioRepository {
    val downloadStatus: StateFlow<QuranDownloadStatus>
    fun isSurahDownloaded(reciterId: String, surahNumber: Int, totalVerses: Int): Boolean
    fun isJuzDownloaded(reciterId: String, juzNumber: Int): Boolean
    fun getTotalAudioStorageMb(): Double
    suspend fun downloadSurahAudio(surah: Surah, verses: List<Ayah>, reciterId: String): Boolean
    suspend fun downloadJuzAudio(juzNumber: Int, juzTitle: String, verses: List<Ayah>, reciterId: String): Boolean
    fun cancelDownload()
    fun deleteSurahAudio(reciterId: String, surahNumber: Int): Boolean
    fun deleteAllAudio(): Boolean
    fun playAyahAudio(
        ayah: Ayah,
        surahNumber: Int,
        reciterId: String,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit,
        onError: () -> Unit
    )
    fun stopAyahAudio()
}

