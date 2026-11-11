package com.example.noorapp.data.repository

import android.content.Context
import android.media.MediaPlayer
import com.example.noorapp.data.local.QuranAudioDownloadManager
import com.example.noorapp.data.local.QuranSurahUtils
import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.model.QuranDownloadStatus
import com.example.noorapp.domain.model.Surah
import com.example.noorapp.domain.repository.QuranAudioRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import javax.inject.Inject

class QuranAudioRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) : QuranAudioRepository {

    private var mediaPlayer: MediaPlayer? = null

    override val downloadStatus: StateFlow<QuranDownloadStatus>
        get() = QuranAudioDownloadManager.downloadStatus

    override fun isSurahDownloaded(reciterId: String, surahNumber: Int, totalVerses: Int): Boolean {
        return QuranAudioDownloadManager.isSurahDownloaded(context, reciterId, surahNumber, totalVerses)
    }

    override fun isJuzDownloaded(reciterId: String, juzNumber: Int): Boolean {
        return QuranAudioDownloadManager.isJuzDownloaded(context, reciterId, juzNumber)
    }

    override fun getTotalAudioStorageMb(): Double {
        return QuranAudioDownloadManager.getTotalAudioStorageMb(context)
    }

    override suspend fun downloadSurahAudio(
        surah: Surah,
        verses: List<Ayah>,
        reciterId: String
    ): Boolean {
        return QuranAudioDownloadManager.downloadSurahAudio(
            context = context,
            okHttpClient = okHttpClient,
            surah = surah,
            verses = verses,
            reciterId = reciterId
        )
    }

    override suspend fun downloadJuzAudio(
        juzNumber: Int,
        juzTitle: String,
        verses: List<Ayah>,
        reciterId: String
    ): Boolean {
        return QuranAudioDownloadManager.downloadJuzAudio(
            context = context,
            okHttpClient = okHttpClient,
            juzNumber = juzNumber,
            juzTitle = juzTitle,
            verses = verses,
            reciterId = reciterId
        )
    }

    override fun cancelDownload() {
        QuranAudioDownloadManager.cancelDownload()
    }

    override fun deleteSurahAudio(reciterId: String, surahNumber: Int): Boolean {
        return QuranAudioDownloadManager.deleteSurahAudio(context, reciterId, surahNumber)
    }

    override fun deleteAllAudio(): Boolean {
        return QuranAudioDownloadManager.deleteAllAudio(context)
    }

    override fun playAyahAudio(
        ayah: Ayah,
        surahNumber: Int,
        reciterId: String,
        onPrepared: () -> Unit,
        onCompleted: () -> Unit,
        onError: () -> Unit
    ) {
        try {
            stopAyahAudio()
            val localFile = QuranAudioDownloadManager.getLocalAyahFile(
                context = context,
                reciterId = reciterId,
                surahNumber = surahNumber,
                ayahNumberInSurah = ayah.numberInSurah
            )

            mediaPlayer = MediaPlayer().apply {
                if (localFile.exists() && localFile.length() > 512) {
                    setDataSource(localFile.absolutePath)
                } else {
                    val audioUrl = QuranSurahUtils.getAudioUrl(reciterId, surahNumber, ayah.numberInSurah)
                    setDataSource(audioUrl)
                }
                setOnPreparedListener {
                    start()
                    onPrepared()
                }
                setOnCompletionListener {
                    onCompleted()
                }
                setOnErrorListener { _, _, _ ->
                    if (reciterId != FALLBACK_RECITER_ID) {
                        try {
                            reset()
                            setDataSource(QuranSurahUtils.getAudioUrl(FALLBACK_RECITER_ID, surahNumber, ayah.numberInSurah))
                            prepareAsync()
                        } catch (_: Exception) {
                            onError()
                        }
                    } else {
                        onError()
                    }
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            onError()
        }
    }

    override fun stopAyahAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {
            // ignore
        } finally {
            mediaPlayer = null
        }
    }

    companion object {
        private const val FALLBACK_RECITER_ID = "ar.alafasy"
    }
}

