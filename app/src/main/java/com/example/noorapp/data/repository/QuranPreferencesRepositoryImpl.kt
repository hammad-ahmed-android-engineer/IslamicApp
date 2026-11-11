package com.example.noorapp.data.repository

import android.content.Context
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.domain.model.BookmarkItem
import com.example.noorapp.domain.model.DEFAULT_FAVORITE_SURAHS
import com.example.noorapp.domain.repository.QuranPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class QuranPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : QuranPreferencesRepository {

    private val prefs by lazy {
        ArabicFontManager.init(context)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun getSelectedReciter(): String {
        val raw = prefs.getString(KEY_SELECTED_RECITER, DEFAULT_RECITER) ?: DEFAULT_RECITER
        return if (raw == LEGACY_WARSH_RECITER) FALLBACK_HUSARY_RECITER else raw
    }

    override fun setSelectedReciter(reciterId: String) {
        prefs.edit().putString(KEY_SELECTED_RECITER, reciterId).apply()
    }

    override fun getArabicFontSize(): Float {
        return prefs.getFloat(KEY_ARABIC_FONT_SIZE, 26f).coerceIn(18f, 44f)
    }

    override fun setArabicFontSize(size: Float) {
        prefs.edit().putFloat(KEY_ARABIC_FONT_SIZE, size.coerceIn(18f, 44f)).apply()
    }

    override fun getTranslationFontSize(): Float {
        return prefs.getFloat(KEY_TRANSLATION_FONT_SIZE, 15f).coerceIn(12f, 26f)
    }

    override fun setTranslationFontSize(size: Float) {
        prefs.edit().putFloat(KEY_TRANSLATION_FONT_SIZE, size.coerceIn(12f, 26f)).apply()
    }

    override fun getSelectedQuranFont(): String {
        ArabicFontManager.init(context)
        return ArabicFontManager.currentFontKey.value
    }

    override fun setSelectedQuranFont(fontKey: String) {
        val normalized = ArabicFontManager.normalizeKey(fontKey)
        ArabicFontManager.setFont(context, normalized)
    }

    override fun isColoredTajweedQuran(): Boolean {
        return prefs.getBoolean(KEY_IS_COLORED_QURAN, true)
    }

    override fun setColoredTajweedQuran(isColored: Boolean) {
        prefs.edit().putBoolean(KEY_IS_COLORED_QURAN, isColored).apply()
    }

    override fun getFavoriteSurahs(): Set<Int> {
        val saved = prefs.getStringSet(KEY_FAVORITE_SURAHS, null)
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
        return if (saved.isNullOrEmpty()) DEFAULT_FAVORITE_SURAHS.toSet() else saved
    }

    override fun saveFavoriteSurahs(favorites: Set<Int>) {
        prefs.edit().putStringSet(KEY_FAVORITE_SURAHS, favorites.map { it.toString() }.toSet()).apply()
    }

    override fun getBookmarks(): List<BookmarkItem> {
        val count = prefs.getInt(KEY_BOOKMARK_COUNT, 0)
        val list = mutableListOf<BookmarkItem>()
        for (i in 0 until count) {
            val sNum = prefs.getInt("bm_${i}_surah", 0)
            val sName = prefs.getString("bm_${i}_name", "") ?: ""
            val aNum = prefs.getInt("bm_${i}_ayah", 0)
            val arabic = prefs.getString("bm_${i}_ar", "") ?: ""
            val eng = prefs.getString("bm_${i}_en", "") ?: ""
            if (sNum > 0 && aNum > 0) {
                list.add(BookmarkItem(sNum, sName, aNum, arabic, eng))
            }
        }
        return list
    }

    override fun saveBookmarks(bookmarks: List<BookmarkItem>) {
        val editor = prefs.edit()
        editor.putInt(KEY_BOOKMARK_COUNT, bookmarks.size)
        bookmarks.forEachIndexed { i, bm ->
            editor.putInt("bm_${i}_surah", bm.surahNumber)
            editor.putString("bm_${i}_name", bm.surahName)
            editor.putInt("bm_${i}_ayah", bm.ayahNumber)
            editor.putString("bm_${i}_ar", bm.arabicSnippet)
            editor.putString("bm_${i}_en", bm.englishSnippet)
        }
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "quran_prefs"
        private const val KEY_SELECTED_RECITER = "selected_reciter"
        private const val KEY_ARABIC_FONT_SIZE = "arabic_font_size"
        private const val KEY_TRANSLATION_FONT_SIZE = "translation_font_size"
        private const val KEY_IS_COLORED_QURAN = "is_colored_quran"
        private const val KEY_FAVORITE_SURAHS = "favorite_surahs"
        private const val KEY_BOOKMARK_COUNT = "bookmark_count"
        private const val DEFAULT_RECITER = "ar.alafasy"
        private const val LEGACY_WARSH_RECITER = "ar.warsh"
        private const val FALLBACK_HUSARY_RECITER = "ar.husary"
    }
}

