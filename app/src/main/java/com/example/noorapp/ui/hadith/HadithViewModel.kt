package com.example.noorapp.ui.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.R
import com.example.noorapp.core.common.Resource
import com.example.noorapp.core.utils.StringUtils
import com.example.noorapp.domain.model.Hadith
import com.example.noorapp.domain.usecase.hadith.GetHadithCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val getHadithCollectionUseCase: GetHadithCollectionUseCase
) : ViewModel() {

    // The Six Canonical Hadith Books (Al-Kutub al-Sittah)
    val collections = listOf(
        HadithCollectionTab(
            titleRes = R.string.hadith_bukhari_title,
            arabicTitleRes = R.string.hadith_bukhari_arabic,
            compilerNameRes = R.string.hadith_bukhari_compiler,
            descriptionRes = R.string.hadith_bukhari_desc,
            edition = "eng-bukhari",
            maxSections = 97
        ),
        HadithCollectionTab(
            titleRes = R.string.hadith_muslim_title,
            arabicTitleRes = R.string.hadith_muslim_arabic,
            compilerNameRes = R.string.hadith_muslim_compiler,
            descriptionRes = R.string.hadith_muslim_desc,
            edition = "eng-muslim",
            maxSections = 56
        ),
        HadithCollectionTab(
            titleRes = R.string.hadith_abudawud_title,
            arabicTitleRes = R.string.hadith_abudawud_arabic,
            compilerNameRes = R.string.hadith_abudawud_compiler,
            descriptionRes = R.string.hadith_abudawud_desc,
            edition = "eng-abudawud",
            maxSections = 43
        ),
        HadithCollectionTab(
            titleRes = R.string.hadith_tirmidhi_title,
            arabicTitleRes = R.string.hadith_tirmidhi_arabic,
            compilerNameRes = R.string.hadith_tirmidhi_compiler,
            descriptionRes = R.string.hadith_tirmidhi_desc,
            edition = "eng-tirmidhi",
            maxSections = 49
        ),
        HadithCollectionTab(
            titleRes = R.string.hadith_nasai_title,
            arabicTitleRes = R.string.hadith_nasai_arabic,
            compilerNameRes = R.string.hadith_nasai_compiler,
            descriptionRes = R.string.hadith_nasai_desc,
            edition = "eng-nasai",
            maxSections = 51
        ),
        HadithCollectionTab(
            titleRes = R.string.hadith_ibnmajah_title,
            arabicTitleRes = R.string.hadith_ibnmajah_arabic,
            compilerNameRes = R.string.hadith_ibnmajah_compiler,
            descriptionRes = R.string.hadith_ibnmajah_desc,
            edition = "eng-ibnmajah",
            maxSections = 37
        )
    )

    private val _uiState = MutableStateFlow(
        HadithUiState(
            maxSections = collections[0].maxSections
        )
    )
    val uiState: StateFlow<HadithUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val title = getHadithCollectionUseCase.getSectionTitle(collections[0].edition, 1).orEmpty()
            _uiState.update { it.copy(currentChapterTitle = title) }
        }
        loadHadiths(collections[0].edition, 1)
    }

    fun selectCollection(index: Int) {
        if (index in collections.indices) {
            val item = collections[index]
            viewModelScope.launch {
                val chapterTitle = getHadithCollectionUseCase.getSectionTitle(item.edition, 1).orEmpty()
                _uiState.update {
                    it.copy(
                        selectedCollectionIndex = index,
                        isBookOpened = true,
                        searchQuery = "",
                        currentSection = 1,
                        maxSections = item.maxSections,
                        currentChapterTitle = chapterTitle
                    )
                }
                loadHadiths(item.edition, 1)
            }
        }
    }

    fun closeBook() {
        _uiState.update {
            it.copy(
                isBookOpened = false,
                searchQuery = ""
            )
        }
    }

    fun nextSection() {
        val next = _uiState.value.currentSection + 1
        if (next <= _uiState.value.maxSections) {
            goToSection(next)
        }
    }

    fun previousSection() {
        val prev = _uiState.value.currentSection - 1
        if (prev >= 1) {
            goToSection(prev)
        }
    }

    fun goToSection(section: Int) {
        val target = section.coerceIn(1, _uiState.value.maxSections)
        val item = collections[_uiState.value.selectedCollectionIndex]
        viewModelScope.launch {
            val chapterTitle = getHadithCollectionUseCase.getSectionTitle(item.edition, target).orEmpty()
            _uiState.update {
                it.copy(
                    currentSection = target,
                    currentChapterTitle = chapterTitle
                )
            }
            loadHadiths(item.edition, target)
        }
    }

    fun loadHadiths(edition: String, section: Int) {
        viewModelScope.launch {
            getHadithCollectionUseCase(edition, section).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Resource.Success -> {
                        val list = result.data.orEmpty()
                        val chapterTitle = getHadithCollectionUseCase.getSectionTitle(edition, section)
                            ?: list.firstOrNull()?.chapter.orEmpty()
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                hadiths = list,
                                filteredHadiths = applyFilter(list, state.searchQuery),
                                currentChapterTitle = chapterTitle,
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
                filteredHadiths = applyFilter(state.hadiths, query)
            )
        }
    }

    fun selectHadith(hadith: Hadith?) {
        _uiState.update { it.copy(selectedHadith = hadith) }
    }

    private fun applyFilter(list: List<Hadith>, query: String): List<Hadith> {
        val cleanQuery = StringUtils.cleanSearchQuery(query)
        if (cleanQuery.isBlank()) return list
        val cleanArabicQuery = StringUtils.removeArabicDiacritics(cleanQuery)
        return list.filter { hadith ->
            hadith.englishText.contains(cleanQuery, ignoreCase = true) ||
                hadith.chapter.contains(cleanQuery, ignoreCase = true) ||
                hadith.narrator.contains(cleanQuery, ignoreCase = true) ||
                hadith.source.contains(cleanQuery, ignoreCase = true) ||
                StringUtils.removeArabicDiacritics(hadith.arabicText).contains(cleanArabicQuery, ignoreCase = true)
        }
    }
}
