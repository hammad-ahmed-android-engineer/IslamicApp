package com.ne.muslimprayers.qiblafinder.ui.ibadat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.domain.model.TasbihState
import com.ne.muslimprayers.qiblafinder.domain.usecase.dua.GetDuasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IbadatUiState(
    val tasbih: TasbihState = TasbihState(),
    val allDuas: List<DuaItem> = emptyList(),
    val filteredDuas: List<DuaItem> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val categories: List<String> = emptyList(),
    val selectedTab: Int = 0, // 0 = Tasbih, 1 = Duas
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class IbadatViewModel @Inject constructor(
    private val getDuasUseCase: GetDuasUseCase
) : ViewModel() {

    companion object {
        val PREFERRED_CATEGORY_ORDER = listOf(
            "All",
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
            "Death & Afterlife"
        )
    }

    private val _uiState = MutableStateFlow(IbadatUiState())
    val uiState: StateFlow<IbadatUiState> = _uiState.asStateFlow()

    init {
        loadDuas()
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

                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                allDuas = list,
                                filteredDuas = applyFilter(list, state.selectedCategory, state.searchQuery),
                                categories = orderedCats,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Failed to load Duas"
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
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
                filteredDuas = applyFilter(state.allDuas, category, state.searchQuery)
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

    fun incrementTasbih() {
        _uiState.update { state ->
            val newCount = state.tasbih.count + 1
            if (newCount >= state.tasbih.target) {
                state.copy(
                    tasbih = state.tasbih.copy(
                        count = 0,
                        totalLaps = state.tasbih.totalLaps + 1
                    )
                )
            } else {
                state.copy(
                    tasbih = state.tasbih.copy(count = newCount)
                )
            }
        }
    }

    fun resetTasbih() {
        _uiState.update {
            it.copy(
                tasbih = it.tasbih.copy(count = 0, totalLaps = 0)
            )
        }
    }

    fun setPhrase(english: String, arabic: String, target: Int = 33) {
        _uiState.update {
            it.copy(
                tasbih = it.tasbih.copy(
                    currentPhrase = english,
                    arabicPhrase = arabic,
                    target = target,
                    count = 0
                )
            )
        }
    }

    fun setTarget(target: Int) {
        _uiState.update {
            it.copy(
                tasbih = it.tasbih.copy(target = target, count = 0)
            )
        }
    }
}
