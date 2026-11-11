package com.example.noorapp.ui.hadith

import androidx.annotation.StringRes
import com.example.noorapp.domain.model.Hadith

data class HadithCollectionTab(
    @StringRes val titleRes: Int,
    @StringRes val arabicTitleRes: Int,
    @StringRes val compilerNameRes: Int,
    @StringRes val descriptionRes: Int,
    val edition: String,
    val maxSections: Int
)

data class HadithUiState(
    val hadiths: List<Hadith> = emptyList(),
    val filteredHadiths: List<Hadith> = emptyList(),
    val selectedHadith: Hadith? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCollectionIndex: Int = 0,
    val isBookOpened: Boolean = false,
    val currentSection: Int = 1,
    val maxSections: Int = 97,
    val currentChapterTitle: String = "",
    val errorMessage: String? = null
)

