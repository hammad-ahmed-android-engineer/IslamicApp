package com.example.noorapp.domain.repository

import com.example.noorapp.domain.model.BookmarkItem

interface QuranPreferencesRepository {
    fun getSelectedReciter(): String
    fun setSelectedReciter(reciterId: String)
    fun getArabicFontSize(): Float
    fun setArabicFontSize(size: Float)
    fun getTranslationFontSize(): Float
    fun setTranslationFontSize(size: Float)
    fun getSelectedQuranFont(): String
    fun setSelectedQuranFont(fontKey: String)
    fun isColoredTajweedQuran(): Boolean
    fun setColoredTajweedQuran(isColored: Boolean)
    fun getFavoriteSurahs(): Set<Int>
    fun saveFavoriteSurahs(favorites: Set<Int>)
    fun getBookmarks(): List<BookmarkItem>
    fun saveBookmarks(bookmarks: List<BookmarkItem>)
}

