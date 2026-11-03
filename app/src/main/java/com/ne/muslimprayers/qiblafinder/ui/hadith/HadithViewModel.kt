package com.ne.muslimprayers.qiblafinder.ui.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.core.utils.StringUtils
import com.ne.muslimprayers.qiblafinder.data.local.HadithDatabaseHelper
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
import com.ne.muslimprayers.qiblafinder.domain.usecase.hadith.GetHadithCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HadithCollectionTab(
    val title: String,
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
    val currentSection: Int = 1,
    val maxSections: Int = 97,
    val currentChapterName: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val getHadithCollectionUseCase: GetHadithCollectionUseCase,
    private val hadithDbHelper: HadithDatabaseHelper
) : ViewModel() {

    // The Six Canonical Hadith Books (Al-Kutub al-Sittah)
    val collections = listOf(
        HadithCollectionTab("Sahih Bukhari", "eng-bukhari", 97),
        HadithCollectionTab("Sahih Muslim", "eng-muslim", 56),
        HadithCollectionTab("Abu Dawud", "eng-abudawud", 43),
        HadithCollectionTab("Jami' at-Tirmidhi", "eng-tirmidhi", 49),
        HadithCollectionTab("Sunan an-Nasa'i", "eng-nasai", 51),
        HadithCollectionTab("Sunan Ibn Majah", "eng-ibnmajah", 37)
    )

    private val _uiState = MutableStateFlow(
        HadithUiState(
            maxSections = collections[0].maxSections
        )
    )
    val uiState: StateFlow<HadithUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val title = hadithDbHelper.getSectionTitle(collections[0].edition, 1) ?: "Revelation"
            _uiState.update { it.copy(currentChapterName = "${collections[0].title} • $title") }
        }
        loadHadiths(collections[0].edition, 1)
    }

    fun selectCollection(index: Int) {
        if (index in collections.indices) {
            val item = collections[index]
            viewModelScope.launch {
                val chapterName = hadithDbHelper.getSectionTitle(item.edition, 1) ?: "Chapter 1"
                _uiState.update {
                    it.copy(
                        selectedCollectionIndex = index,
                        currentSection = 1,
                        maxSections = item.maxSections,
                        currentChapterName = "${item.title} • $chapterName"
                    )
                }
                loadHadiths(item.edition, 1)
            }
        }
    }

    fun nextSection() {
        val next = _uiState.value.currentSection + 1
        if (next <= _uiState.value.maxSections) {
            val item = collections[_uiState.value.selectedCollectionIndex]
            viewModelScope.launch {
                val chapterName = hadithDbHelper.getSectionTitle(item.edition, next) ?: "Chapter $next"
                _uiState.update {
                    it.copy(
                        currentSection = next,
                        currentChapterName = "${item.title} • $chapterName"
                    )
                }
                loadHadiths(item.edition, next)
            }
        }
    }

    fun previousSection() {
        val prev = _uiState.value.currentSection - 1
        if (prev >= 1) {
            val item = collections[_uiState.value.selectedCollectionIndex]
            viewModelScope.launch {
                val chapterName = hadithDbHelper.getSectionTitle(item.edition, prev) ?: "Chapter $prev"
                _uiState.update {
                    it.copy(
                        currentSection = prev,
                        currentChapterName = "${item.title} • $chapterName"
                    )
                }
                loadHadiths(item.edition, prev)
            }
        }
    }

    fun goToSection(section: Int) {
        val target = section.coerceIn(1, _uiState.value.maxSections)
        val item = collections[_uiState.value.selectedCollectionIndex]
        viewModelScope.launch {
            val chapterName = hadithDbHelper.getSectionTitle(item.edition, target) ?: "Chapter $target"
            _uiState.update {
                it.copy(
                    currentSection = target,
                    currentChapterName = "${item.title} • $chapterName"
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
                        val chapterTitle = hadithDbHelper.getSectionTitle(edition, section)
                        val collectionTitle = collections.find { it.edition == edition }?.title ?: "Hadith"
                        val chapterName = if (!chapterTitle.isNullOrBlank()) {
                            "$collectionTitle • $chapterTitle"
                        } else {
                            list.firstOrNull()?.chapter ?: "$collectionTitle • Chapter $section"
                        }
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                hadiths = list,
                                filteredHadiths = applyFilter(list, state.searchQuery),
                                currentChapterName = chapterName,
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
