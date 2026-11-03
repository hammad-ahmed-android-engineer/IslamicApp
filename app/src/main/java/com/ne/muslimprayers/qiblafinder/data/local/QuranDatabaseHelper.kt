package com.ne.muslimprayers.qiblafinder.data.local

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranStaticData
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranSurahUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dedicated, resilient, isolated Offline Quran Database Manager.
 *
 * GUARANTEES:
 * 1. Physical Isolation: Operates solely on "quran_offline.db". User tables
 *    (alarms, salah tracker, bookmarks) are never touched or modified.
 * 2. Corrupt-Proof Asset Copy: Directly streams from assets and validates file size (>5MB).
 *    If any previous run left a partial or locked file, it safely resets and recopies.
 * 3. 100% Offline Availability: Instantly queries Surahs, Verses, and entire Juz.
 */
@Singleton
class QuranDatabaseHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tag = "QuranDatabaseHelper"
    private val dbName = "quran_offline.db"
    private var sqliteDb: SQLiteDatabase? = null

    @Synchronized
    private fun getReadableDb(): SQLiteDatabase {
        sqliteDb?.let {
            if (it.isOpen) {
                try {
                    it.rawQuery("SELECT 1", null).use { cursor -> cursor.moveToFirst() }
                    return it
                } catch (e: Exception) {
                    try { it.close() } catch (ignored: Exception) {}
                    sqliteDb = null
                }
            }
        }

        val dbFile = context.getDatabasePath(dbName)
        dbFile.parentFile?.mkdirs()

        // Check if database needs extraction
        if (!dbFile.exists() || dbFile.length() < 5_000_000) {
            extractAssetDatabase(dbFile)
        }

        return try {
            val db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READWRITE
            )
            sqliteDb = db
            db
        } catch (e: Exception) {
            Log.e(tag, "Failed to open existing database, resetting from asset: ${e.message}")
            try { dbFile.delete() } catch (ignored: Exception) {}
            extractAssetDatabase(dbFile)
            val db = SQLiteDatabase.openDatabase(
                dbFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READWRITE
            )
            sqliteDb = db
            db
        }
    }

    private fun openAssetStream(): InputStream {
        return context.assets.open("databases/quran.db")
    }

    private fun extractAssetDatabase(destFile: File) {
        destFile.parentFile?.mkdirs()
        if (destFile.exists()) {
            destFile.delete()
        }

        Log.i(tag, "Extracting offline Quran database to ${destFile.absolutePath}...")
        openAssetStream().use { input ->
            FileOutputStream(destFile).use { output ->
                val buffer = ByteArray(64 * 1024)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } > 0) {
                    output.write(buffer, 0, bytesRead)
                }
                output.flush()
            }
        }
        Log.i(tag, "Offline Quran database extracted successfully (${destFile.length()} bytes)")
    }

    suspend fun getAllSurahs(): List<Surah> = withContext(Dispatchers.IO) {
        try {
            val db = getReadableDb()
            val list = mutableListOf<Surah>()
            val cursor: Cursor = db.rawQuery(
                "SELECT number, name_en, name_ar, name_en_translation, total_verses, type FROM surahs ORDER BY number ASC",
                null
            )
            cursor.use {
                while (it.moveToNext()) {
                    val num = it.getInt(0)
                    val nameEn = it.getString(1) ?: ""
                    val nameAr = it.getString(2) ?: ""
                    val meaning = it.getString(3) ?: ""
                    val verses = it.getInt(4)
                    val rawType = it.getString(5) ?: "Meccan"
                    val type = if (rawType.contains("mecca", ignoreCase = true)) "Meccan" else "Medinan"

                    list.add(
                        Surah(
                            number = num,
                            name = nameEn,
                            arabicName = nameAr,
                            englishMeaning = meaning,
                            totalVerses = verses,
                            revelationType = type
                        )
                    )
                }
            }
            if (list.isNotEmpty()) list else QuranStaticData.ALL_SURAHS
        } catch (e: Exception) {
            Log.e(tag, "Error reading Surahs from database, using static fallback: ${e.message}")
            QuranStaticData.ALL_SURAHS
        }
    }

    suspend fun getAyahsBySurah(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        try {
            val db = getReadableDb()
            val list = mutableListOf<Ayah>()
            val cursor: Cursor = db.rawQuery(
                """
                SELECT id, surah_number, number_in_surah, text_uthmani, text_indopak, text_en, text_ur, juz_number 
                FROM ayahs 
                WHERE surah_number = ? 
                ORDER BY number_in_surah ASC
                """.trimIndent(),
                arrayOf(surahNumber.toString())
            )
            cursor.use {
                while (it.moveToNext()) {
                    val globalId = it.getInt(0)
                    val sNum = it.getInt(1)
                    val vNum = it.getInt(2)
                    val textUthmani = it.getString(3) ?: ""
                    val textIndopak = it.getString(4) ?: ""
                    val textEn = it.getString(5) ?: ""
                    val textUr = it.getString(6) ?: ""
                    val juz = it.getInt(7)

                    list.add(
                        Ayah(
                            numberInSurah = vNum,
                            arabicText = textUthmani,
                            indopakText = textIndopak.ifBlank { textUthmani },
                            englishText = textEn,
                            urduText = textUr,
                            juz = juz,
                            audioUrl = QuranSurahUtils.getAudioUrl("ar.alafasy", sNum, vNum),
                            globalAyahNumber = globalId
                        )
                    )
                }
            }
            list
        } catch (e: Exception) {
            Log.e(tag, "Error reading Ayahs for Surah $surahNumber: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAyahsByJuz(juzNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        try {
            val db = getReadableDb()
            val list = mutableListOf<Ayah>()
            val cursor: Cursor = db.rawQuery(
                """
                SELECT id, surah_number, number_in_surah, text_uthmani, text_indopak, text_en, text_ur, juz_number 
                FROM ayahs 
                WHERE juz_number = ? 
                ORDER BY id ASC
                """.trimIndent(),
                arrayOf(juzNumber.toString())
            )
            cursor.use {
                while (it.moveToNext()) {
                    val globalId = it.getInt(0)
                    val sNum = it.getInt(1)
                    val vNum = it.getInt(2)
                    val textUthmani = it.getString(3) ?: ""
                    val textIndopak = it.getString(4) ?: ""
                    val textEn = it.getString(5) ?: ""
                    val textUr = it.getString(6) ?: ""
                    val juz = it.getInt(7)

                    list.add(
                        Ayah(
                            numberInSurah = vNum,
                            arabicText = textUthmani,
                            indopakText = textIndopak.ifBlank { textUthmani },
                            englishText = textEn,
                            urduText = textUr,
                            juz = juz,
                            audioUrl = QuranSurahUtils.getAudioUrl("ar.alafasy", sNum, vNum),
                            globalAyahNumber = globalId
                        )
                    )
                }
            }
            list
        } catch (e: Exception) {
            Log.e(tag, "Error reading Ayahs for Juz $juzNumber: ${e.message}")
            emptyList()
        }
    }
}
