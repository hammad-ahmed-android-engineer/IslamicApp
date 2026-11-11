package com.example.noorapp.domain.usecase.quran

import com.example.noorapp.domain.model.BookmarkItem
import com.example.noorapp.domain.repository.QuranPreferencesRepository
import javax.inject.Inject

class ManageQuranPreferencesUseCase @Inject constructor(
    private val preferencesRepository: QuranPreferencesRepository
) {
    fun getSelectedReciter(): String = preferencesRepository.getSelectedReciter()
    fun setSelectedReciter(reciterId: String) = preferencesRepository.setSelectedReciter(reciterId)
    fun getArabicFontSize(): Float = preferencesRepository.getArabicFontSize()
    fun setArabicFontSize(size: Float) = preferencesRepository.setArabicFontSize(size)
    fun getTranslationFontSize(): Float = preferencesRepository.getTranslationFontSize()
    fun setTranslationFontSize(size: Float) = preferencesRepository.setTranslationFontSize(size)
    fun getSelectedQuranFont(): String = preferencesRepository.getSelectedQuranFont()
    fun setSelectedQuranFont(fontKey: String) = preferencesRepository.setSelectedQuranFont(fontKey)
    fun isColoredTajweedQuran(): Boolean = preferencesRepository.isColoredTajweedQuran()
    fun setColoredTajweedQuran(isColored: Boolean) = preferencesRepository.setColoredTajweedQuran(isColored)
    fun getFavoriteSurahs(): Set<Int> = preferencesRepository.getFavoriteSurahs()
    fun saveFavoriteSurahs(favorites: Set<Int>) = preferencesRepository.saveFavoriteSurahs(favorites)
    fun getBookmarks(): List<BookmarkItem> = preferencesRepository.getBookmarks()
    fun saveBookmarks(bookmarks: List<BookmarkItem>) = preferencesRepository.saveBookmarks(bookmarks)
}

