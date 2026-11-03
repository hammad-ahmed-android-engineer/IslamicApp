package com.ne.muslimprayers.qiblafinder.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

data class HadithBookInfo(
    val bookKey: String,
    val nameEn: String,
    val nameAr: String,
    val maxSections: Int
)

/**
 * Dedicated, isolated SQLite Database Manager for authentic Hadith collections.
 *
 * GUARANTEES:
 * 1. Physical Isolation: Operates solely on "hadith_offline.db". Never touches user alarms or salah tracker.
 * 2. 6 Major Hadith Books: Bukhari, Muslim, Abu Dawud, Tirmidhi, Nasa'i, Ibn Majah.
 * 3. 100% Offline Availability for chapters, titles, and core collections, with online sync caching.
 */
@Singleton
class HadithDatabaseHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tag = "HadithDatabaseHelper"
    private val dbName = "hadith_offline.db"
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

        if (!dbFile.exists() || dbFile.length() < 1_000_000) {
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
            Log.e(tag, "Failed to open Hadith database, recreating: ${e.message}")
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
        return context.assets.open("databases/hadith.db")
    }

    private fun extractAssetDatabase(destFile: File) {
        destFile.parentFile?.mkdirs()
        if (destFile.exists()) {
            destFile.delete()
        }

        Log.i(tag, "Extracting offline Hadith database to ${destFile.absolutePath}...")
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
        Log.i(tag, "Offline Hadith database extracted (${destFile.length()} bytes)")
    }

    suspend fun getBooks(): List<HadithBookInfo> = withContext(Dispatchers.IO) {
        try {
            val db = getReadableDb()
            val list = mutableListOf<HadithBookInfo>()
            val cursor: Cursor = db.rawQuery(
                "SELECT book_key, name_en, name_ar, max_sections FROM hadith_books ORDER BY id ASC",
                null
            )
            cursor.use {
                while (it.moveToNext()) {
                    list.add(
                        HadithBookInfo(
                            bookKey = it.getString(0),
                            nameEn = it.getString(1),
                            nameAr = it.getString(2),
                            maxSections = it.getInt(3)
                        )
                    )
                }
            }
            list
        } catch (e: Exception) {
            Log.e(tag, "Error loading Hadith books: ${e.message}")
            emptyList()
        }
    }

    suspend fun getSectionTitle(bookKey: String, sectionNumber: Int): String? = withContext(Dispatchers.IO) {
        try {
            val db = getReadableDb()
            val cursor: Cursor = db.rawQuery(
                "SELECT title FROM hadith_sections WHERE book_key = ? AND section_number = ? LIMIT 1",
                arrayOf(bookKey, sectionNumber.toString())
            )
            cursor.use {
                if (it.moveToNext()) {
                    it.getString(0)
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getHadiths(bookKey: String, sectionNumber: Int): List<Hadith> = withContext(Dispatchers.IO) {
        try {
            val db = getReadableDb()
            val sectionTitle = getSectionTitle(bookKey, sectionNumber)
            val list = mutableListOf<Hadith>()
            val cursor: Cursor = db.rawQuery(
                """
                SELECT id, chapter, narrator, arabic_text, english_text, source 
                FROM hadiths 
                WHERE book_key = ? AND section_number = ? 
                ORDER BY id ASC
                """.trimIndent(),
                arrayOf(bookKey, sectionNumber.toString())
            )
            cursor.use {
                var index = 1
                while (it.moveToNext()) {
                    val rawChapter = it.getString(1) ?: ""
                    val bookPrefix = when (bookKey) {
                        "eng-bukhari" -> "Sahih al-Bukhari"
                        "eng-muslim" -> "Sahih Muslim"
                        "eng-abudawud" -> "Sunan Abu Dawud"
                        "eng-tirmidhi" -> "Jami' at-Tirmidhi"
                        "eng-nasai" -> "Sunan an-Nasa'i"
                        "eng-ibnmajah" -> "Sunan Ibn Majah"
                        else -> "Hadith"
                    }
                    val displayChapter = if (!sectionTitle.isNullOrBlank()) {
                        "$bookPrefix • $sectionTitle"
                    } else if (rawChapter.isNotBlank()) {
                        rawChapter
                    } else {
                        "$bookPrefix • Section $sectionNumber"
                    }

                    list.add(
                        Hadith(
                            id = index++,
                            chapter = displayChapter,
                            narrator = it.getString(2) ?: "",
                            arabicText = it.getString(3) ?: "",
                            englishText = it.getString(4) ?: "",
                            source = it.getString(5) ?: ""
                        )
                    )
                }
            }
            list
        } catch (e: Exception) {
            Log.e(tag, "Error reading Hadiths for $bookKey section $sectionNumber: ${e.message}")
            emptyList()
        }
    }

    suspend fun insertHadiths(bookKey: String, sectionNumber: Int, hadiths: List<Hadith>) = withContext(Dispatchers.IO) {
        if (hadiths.isEmpty()) return@withContext
        try {
            val db = getReadableDb()
            db.beginTransaction()
            try {
                for (h in hadiths) {
                    val cv = ContentValues().apply {
                        put("book_key", bookKey)
                        put("section_number", sectionNumber)
                        put("hadith_number", h.narrator.removePrefix("Hadith #").trim())
                        put("chapter", h.chapter)
                        put("narrator", h.narrator)
                        put("arabic_text", h.arabicText)
                        put("english_text", h.englishText)
                        put("source", h.source)
                    }
                    db.insertWithOnConflict("hadiths", null, cv, SQLiteDatabase.CONFLICT_IGNORE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to cache Hadiths: ${e.message}")
        }
    }
}
