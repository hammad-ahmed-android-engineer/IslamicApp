package com.example.noorapp.ui.ibadat

import com.example.noorapp.domain.model.DuaItem

data class IbadatUiState(
    val allDuas: List<DuaItem> = emptyList(),
    val filteredDuas: List<DuaItem> = emptyList(),
    val selectedCategory: String = "All",
    val isCategoryOpened: Boolean = false,
    val searchQuery: String = "",
    val categories: List<String> = emptyList(),
    val categoryCounts: Map<String, Int> = emptyMap(),
    val selectedQuranFont: String = "muhammadi",
    val arabicFontSize: Float = 26f,
    val translationFontSize: Float = 15f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
