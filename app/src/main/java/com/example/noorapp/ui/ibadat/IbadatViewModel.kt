package com.example.noorapp.ui.ibadat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.core.common.Resource
import com.example.noorapp.domain.model.DuaItem
import com.example.noorapp.domain.usecase.dua.GetDuasUseCase
import com.example.noorapp.domain.usecase.quran.ManageQuranPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IbadatViewModel @Inject constructor(
    private val getDuasUseCase: GetDuasUseCase,
    private val manageQuranPreferencesUseCase: ManageQuranPreferencesUseCase
) : ViewModel() {

    companion object {
        val PREFERRED_CATEGORY_ORDER = listOf(
            "Quranic (Rabbana)",
            "Morning & Evening",
            "Prayer & Mosque",
            "Forgiveness & Mercy",
            "Protection & Hardship",
            "Daily Life & Home",
            "Food & Drink",
            "Travel & Journey",
            "Illness & Healing",
            "Praise & Remembrance",
            "Family & Society",
            "Nature & Weather",
            "Death & Afterlife",
            "All"
        )
    }

    private val _uiState = MutableStateFlow(IbadatUiState())
    val uiState: StateFlow<IbadatUiState> = _uiState.asStateFlow()

    init {
        refreshTypographyPreferences()
        loadDuas()
    }

    fun refreshTypographyPreferences() {
        _uiState.update {
            it.copy(
                selectedQuranFont = manageQuranPreferencesUseCase.getSelectedQuranFont(),
                arabicFontSize = manageQuranPreferencesUseCase.getArabicFontSize(),
                translationFontSize = manageQuranPreferencesUseCase.getTranslationFontSize()
            )
        }
    }

    fun loadDuas() {
        viewModelScope.launch {
            getDuasUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        val list = result.data.orEmpty()
                        val distinctCats = list.map { it.category }.distinct()
                        val orderedCats = PREFERRED_CATEGORY_ORDER.filter { it == "All" || it in distinctCats } +
                                distinctCats.filter { it !in PREFERRED_CATEGORY_ORDER }

                        val counts = mutableMapOf<String, Int>()
                        counts["All"] = list.size
                        for (cat in distinctCats) {
                            counts[cat] = list.count { it.category.equals(cat, ignoreCase = true) }
                        }

                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                allDuas = list,
                                filteredDuas = applyFilter(list, state.selectedCategory, state.searchQuery),
                                categories = orderedCats,
                                categoryCounts = counts,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredDuas = applyFilter(state.allDuas, state.selectedCategory, query)
            )
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                isCategoryOpened = true,
                searchQuery = "",
                filteredDuas = applyFilter(state.allDuas, category, "")
            )
        }
    }

    fun closeCategory() {
        _uiState.update { state ->
            state.copy(
                isCategoryOpened = false,
                searchQuery = "",
                filteredDuas = applyFilter(state.allDuas, state.selectedCategory, "")
            )
        }
    }

    private fun applyFilter(list: List<DuaItem>, category: String, query: String): List<DuaItem> {
        val catFiltered = if (category == "All") list else list.filter { it.category.equals(category, ignoreCase = true) }
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return catFiltered
        return catFiltered.filter {
            it.title.contains(cleanQuery, ignoreCase = true) ||
                    it.category.contains(cleanQuery, ignoreCase = true) ||
                    it.translation.contains(cleanQuery, ignoreCase = true) ||
                    it.transliteration.contains(cleanQuery, ignoreCase = true) ||
                    it.reference.contains(cleanQuery, ignoreCase = true) ||
                    it.arabicText.contains(cleanQuery, ignoreCase = true)
        }
    }
}
