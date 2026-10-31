package com.ne.muslimprayers.qiblafinder.ui.quran.util

import android.content.Context
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

data class QuranDownloadStatus(
    val isDownloading: Boolean = false,
    val title: String = "",
    val targetKey: String = "", // e.g. "surah_18" or "juz_1"
    val downloadedVerses: Int = 0,
    val totalVerses: Int = 0,
    val progressPercentage: Int = 0,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Robust, offline-first Quran Audio Download Manager.
 * Downloads MP3 recitations per Surah or Parah (Juz) with live progress tracking,
 * local storage caching, and atomic file creation.
 */
object QuranAudioDownloadManager {

    private val _downloadStatus = MutableStateFlow(QuranDownloadStatus())
    val downloadStatus: StateFlow<QuranDownloadStatus> = _downloadStatus.asStateFlow()

    @Volatile
    private var isCancelled: Boolean = false

    private fun getAudioRootDir(context: Context): File {
        val externalDir = context.getExternalFilesDir("quran_audio")
        return externalDir ?: File(context.filesDir, "quran_audio")
    }

    private fun getSurahAudioDir(context: Context, reciterId: String, surahNumber: Int): File {
        val root = getAudioRootDir(context)
        val dir = File(root, "$reciterId/surah_$surahNumber")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun getLocalAyahFile(context: Context, reciterId: String, surahNumber: Int, ayahNumberInSurah: Int): File {
        val dir = getSurahAudioDir(context, reciterId, surahNumber)
        return File(dir, "ayah_$ayahNumberInSurah.mp3")
    }

    fun isAyahDownloaded(context: Context, reciterId: String, surahNumber: Int, ayahNumberInSurah: Int): Boolean {
        val file = getLocalAyahFile(context, reciterId, surahNumber, ayahNumberInSurah)
        return file.exists() && file.length() > 512
    }

    fun isSurahDownloaded(context: Context, reciterId: String, surahNumber: Int, totalVerses: Int): Boolean {
        if (totalVerses <= 0) return false
        val dir = getSurahAudioDir(context, reciterId, surahNumber)
        if (!dir.exists()) return false
        for (i in 1..totalVerses) {
            val f = File(dir, "ayah_$i.mp3")
            if (!f.exists() || f.length() < 512) {
                return false
            }
        }
        return true
    }

    fun getDownloadedSurahAyahsCount(context: Context, reciterId: String, surahNumber: Int, totalVerses: Int): Int {
        val dir = getSurahAudioDir(context, reciterId, surahNumber)
        if (!dir.exists()) return 0
        var count = 0
        for (i in 1..totalVerses) {
            val f = File(dir, "ayah_$i.mp3")
            if (f.exists() && f.length() > 512) {
                count++
            }
        }
        return count
    }

    fun getSurahAudioSizeMb(context: Context, reciterId: String, surahNumber: Int): Double {
        val dir = getSurahAudioDir(context, reciterId, surahNumber)
        if (!dir.exists()) return 0.0
        val bytes = dir.listFiles()?.sumOf { it.length() } ?: 0L
        return bytes.toDouble() / (1024.0 * 1024.0)
    }

    fun getTotalAudioStorageMb(context: Context): Double {
        val root = getAudioRootDir(context)
        if (!root.exists()) return 0.0
        val totalBytes = root.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        return totalBytes.toDouble() / (1024.0 * 1024.0)
    }

    fun deleteSurahAudio(context: Context, reciterId: String, surahNumber: Int): Boolean {
        val dir = getSurahAudioDir(context, reciterId, surahNumber)
        return dir.deleteRecursively()
    }

    fun deleteAllAudio(context: Context): Boolean {
        val root = getAudioRootDir(context)
        return root.deleteRecursively()
    }

    fun cancelDownload() {
        isCancelled = true
        _downloadStatus.value = QuranDownloadStatus(isDownloading = false, isCompleted = false)
    }

    fun dismissProgress() {
        _downloadStatus.value = QuranDownloadStatus(isDownloading = false)
    }

    /**
     * Downloads an entire Surah's verse audio files.
     */
    suspend fun downloadSurahAudio(
        context: Context,
        okHttpClient: OkHttpClient,
        surah: Surah,
        verses: List<Ayah>,
        reciterId: String
    ): Boolean = withContext(Dispatchers.IO) {
        if (verses.isEmpty()) return@withContext false
        isCancelled = false
        val targetKey = "surah_${surah.number}"

        _downloadStatus.value = QuranDownloadStatus(
            isDownloading = true,
            title = "Surah ${surah.name}",
            targetKey = targetKey,
            downloadedVerses = 0,
            totalVerses = verses.size,
            progressPercentage = 0,
            isCompleted = false
        )

        val surahDir = getSurahAudioDir(context, reciterId, surah.number)
        var downloadedCount = 0

        try {
            for ((index, ayah) in verses.withIndex()) {
                if (isCancelled) {
                    _downloadStatus.value = QuranDownloadStatus(isDownloading = false)
                    return@withContext false
                }

                val finalFile = File(surahDir, "ayah_${ayah.numberInSurah}.mp3")
                if (finalFile.exists() && finalFile.length() > 512) {
                    downloadedCount++
                } else {
                    // Download audio from CDN with exact deterministic global Ayah number
                    val url = QuranSurahUtils.getAudioUrl(reciterId, surah.number, ayah.numberInSurah)
                    val tempFile = File(surahDir, "ayah_${ayah.numberInSurah}.tmp")

                    try {
                        val request = Request.Builder().url(url).build()
                        val response = okHttpClient.newCall(request).execute()

                        if (response.isSuccessful && response.body != null) {
                            response.body!!.byteStream().use { input ->
                                FileOutputStream(tempFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            if (tempFile.exists() && tempFile.length() > 512) {
                                if (finalFile.exists()) finalFile.delete()
                                tempFile.renameTo(finalFile)
                                downloadedCount++
                            } else {
                                tempFile.delete()
                            }
                        } else {
                            tempFile.delete()
                        }
                    } catch (e: Exception) {
                        tempFile.delete()
                        // Continue to next ayah or bubble up if cancelled
                        if (isCancelled) throw e
                    }
                }

                val percent = (((index + 1).toFloat() / verses.size.toFloat()) * 100f).toInt()
                _downloadStatus.value = _downloadStatus.value.copy(
                    downloadedVerses = downloadedCount,
                    progressPercentage = percent
                )
            }

            _downloadStatus.value = QuranDownloadStatus(
                isDownloading = false,
                title = "Surah ${surah.name}",
                targetKey = targetKey,
                downloadedVerses = downloadedCount,
                totalVerses = verses.size,
                progressPercentage = 100,
                isCompleted = true
            )
            return@withContext true
        } catch (e: Exception) {
            if (e is CancellationException) {
                _downloadStatus.value = QuranDownloadStatus(isDownloading = false)
            } else {
                _downloadStatus.value = _downloadStatus.value.copy(
                    isDownloading = false,
                    errorMessage = e.localizedMessage ?: "Failed to download audio"
                )
            }
            return@withContext false
        }
    }
}
