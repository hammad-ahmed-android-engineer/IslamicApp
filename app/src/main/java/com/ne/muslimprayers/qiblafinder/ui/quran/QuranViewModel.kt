package com.ne.muslimprayers.qiblafinder.ui.quran

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager
import com.ne.muslimprayers.qiblafinder.core.utils.StringUtils
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.domain.usecase.quran.GetJuzVersesUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.quran.GetQuranSurahsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.quran.GetSurahVersesUseCase
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranAudioDownloadManager
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranDownloadStatus
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranStaticData
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranSurahUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import javax.inject.Inject

data class BookmarkItem(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val arabicSnippet: String,
    val englishSnippet: String
)

data class JuzInfo(
    val number: Int,
    val arabicName: String,
    val englishName: String,
    val startingSurahNumber: Int,
    val startingSurahName: String,
    val totalVerses: Int = 148,
    val startingAyahNumber: Int? = null
) {
    val startsNewSurah: Boolean? get() = startingAyahNumber?.let { it == 1 }
}

val DEFAULT_FAVORITE_SURAHS = listOf(
    18, // Surah Al-Kahf
    32, // Surah As-Sajdah
    36, // Surah Ya-Sin
    55, // Surah Ar-Rahman
    56, // Surah Al-Waqi'ah
    67  // Surah Al-Mulk
)

data class QuranUiState(
    val surahs: List<Surah> = QuranStaticData.ALL_SURAHS,
    val searchQuery: String = "",
    val filteredSurahs: List<Surah> = QuranStaticData.ALL_SURAHS,
    val selectedSectionTab: Int = 0, // 0 = Surahs, 1 = Juz, 2 = Favorites, 3 = Bookmarks
    val juzList: List<JuzInfo> = emptyList(),
    val favoriteSurahNumbers: Set<Int> = DEFAULT_FAVORITE_SURAHS.toSet(),
    val bookmarks: List<BookmarkItem> = emptyList(),
    val selectedSurah: Surah? = null,
    val selectedJuz: JuzInfo? = null,
    val verses: List<Ayah> = emptyList(),
    val isLoadingSurahs: Boolean = false,
    val surahsError: String? = null,
    val isLoadingVerses: Boolean = false,
    val versesError: String? = null,
    val currentlyPlayingAyah: Int? = null,
    val arabicFontSize: Float = 26f,
    val translationFontSize: Float = 15f,
    val showTranslation: Boolean = true,
    val showTransliteration: Boolean = false,
    val autoPlayNext: Boolean = true,
    val selectedReciter: String = "ar.alafasy",
    val showSettingsSheet: Boolean = false,
    val isFullScreenMode: Boolean = false,
    val isAutoScrollEnabled: Boolean = true,
    val useIndoPakScript: Boolean = false,
    val selectedQuranFont: String = "muhammadi",
    val showUrduTranslation: Boolean = true,
    val isColoredTajweedQuran: Boolean = true,
    val isCurrentSurahDownloaded: Boolean = false,
    val downloadedSurahNumbers: Set<Int> = emptySet(),
    val downloadedJuzNumbers: Set<Int> = emptySet(),
    val downloadStatus: QuranDownloadStatus = QuranDownloadStatus(),
    val showReciterDownloadDialog: Boolean = false,
    val pendingDownloadSurah: Surah? = null,
    val pendingDownloadJuz: JuzInfo? = null,
    val pendingDownloadTitle: String = ""
)

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getQuranSurahsUseCase: GetQuranSurahsUseCase,
    private val getSurahVersesUseCase: GetSurahVersesUseCase,
    private val getJuzVersesUseCase: GetJuzVersesUseCase,
    val quranApiService: com.ne.muslimprayers.qiblafinder.data.remote.api.QuranApiService,
    private val okHttpClient: OkHttpClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)

    init {
        ArabicFontManager.init(context)
    }

    private val initialFontKey = ArabicFontManager.currentFontKey.value
    private val initialFontConfig = ArabicFontManager.getTypographyConfig(initialFontKey)
    private val initialReciter = (prefs.getString("selected_reciter", "ar.alafasy") ?: "ar.alafasy").let {
        if (it == "ar.warsh") "ar.husary" else it
    }

    private val _uiState = MutableStateFlow(
        QuranUiState(
            surahs = QuranStaticData.ALL_SURAHS,
            filteredSurahs = QuranStaticData.ALL_SURAHS,
            juzList = createJuzList(),
            favoriteSurahNumbers = loadFavorites(),
            bookmarks = loadBookmarks(),
            arabicFontSize = prefs.getFloat("arabic_font_size", 26f).coerceIn(18f, 44f),
            translationFontSize = prefs.getFloat("translation_font_size", 15f).coerceIn(12f, 26f),
            selectedReciter = initialReciter,
            selectedQuranFont = initialFontKey,
            useIndoPakScript = initialFontConfig.prefersIndoPakScript,
            isColoredTajweedQuran = prefs.getBoolean("is_colored_quran", true)
        )
    )
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    init {
        loadSurahs()
        loadJuzStartBoundaries()
        observeDownloadStatus()
        observeFontChanges()
        refreshDownloadedSets()
    }

    private fun loadJuzStartBoundaries() {
        viewModelScope.launch {
            val boundaries = getJuzVersesUseCase.getJuzStartBoundaries()
            if (boundaries.isEmpty()) return@launch
            _uiState.update { state ->
                val updatedJuzList = state.juzList.map { juz ->
                    val boundary = boundaries[juz.number] ?: return@map juz
                    val startingSurahName = state.surahs.find { it.number == boundary.startingSurahNumber }?.name
                        ?: juz.startingSurahName
                    juz.copy(
                        startingSurahNumber = boundary.startingSurahNumber,
                        startingSurahName = startingSurahName,
                        totalVerses = if (boundary.totalVerses > 0) boundary.totalVerses else juz.totalVerses,
                        startingAyahNumber = boundary.startingAyahNumber
                    )
                }
                val updatedSelectedJuz = state.selectedJuz?.let { current ->
                    updatedJuzList.find { it.number == current.number } ?: current
                }
                state.copy(
                    juzList = updatedJuzList,
                    selectedJuz = updatedSelectedJuz
                )
            }
        }
    }

    private fun observeFontChanges() {
        viewModelScope.launch {
            ArabicFontManager.currentFontKey.collect { key ->
                val config = ArabicFontManager.getTypographyConfig(key)
                _uiState.update {
                    it.copy(
                        selectedQuranFont = key,
                        useIndoPakScript = config.prefersIndoPakScript
                    )
                }
            }
        }
    }

    private fun observeDownloadStatus() {
        viewModelScope.launch {
            QuranAudioDownloadManager.downloadStatus.collect { status ->
                _uiState.update { state ->
                    val isDownloaded = computeCurrentSelectionDownloaded(state)
                    state.copy(downloadStatus = status, isCurrentSurahDownloaded = isDownloaded)
                }
                if (!status.isDownloading) {
                    refreshDownloadedSets()
                }
            }
        }
    }

    private fun computeCurrentSelectionDownloaded(state: QuranUiState): Boolean {
        val currentJuz = state.selectedJuz
        if (currentJuz != null) {
            return QuranAudioDownloadManager.isJuzDownloaded(context, state.selectedReciter, currentJuz.number)
        }
        val surah = state.selectedSurah ?: return false
        return QuranAudioDownloadManager.isSurahDownloaded(
            context,
            state.selectedReciter,
            surah.number,
            surah.totalVerses
        )
    }

    fun refreshDownloadedSets() {
        viewModelScope.launch(Dispatchers.IO) {
            val reciterId = _uiState.value.selectedReciter
            val surahs = _uiState.value.surahs
            val juzs = _uiState.value.juzList

            val downloadedSurahs = surahs
                .filter { QuranAudioDownloadManager.isSurahDownloaded(context, reciterId, it.number, it.totalVerses) }
                .map { it.number }
                .toSet()

            val downloadedJuzs = juzs
                .filter { QuranAudioDownloadManager.isJuzDownloaded(context, reciterId, it.number) }
                .map { it.number }
                .toSet()

            _uiState.update { state ->
                val isCurrentDone = if (state.selectedJuz != null) {
                    downloadedJuzs.contains(state.selectedJuz.number)
                } else if (state.selectedSurah != null) {
                    downloadedSurahs.contains(state.selectedSurah.number)
                } else {
                    false
                }
                state.copy(
                    downloadedSurahNumbers = downloadedSurahs,
                    downloadedJuzNumbers = downloadedJuzs,
                    isCurrentSurahDownloaded = isCurrentDone
                )
            }
        }
    }

    private fun loadFavorites(): Set<Int> {
        val saved = prefs.getStringSet("favorite_surahs", null)?.mapNotNull { it.toIntOrNull() }?.toSet()
        return if (saved.isNullOrEmpty()) DEFAULT_FAVORITE_SURAHS.toSet() else saved
    }

    private fun saveFavorites(favs: Set<Int>) {
        prefs.edit().putStringSet("favorite_surahs", favs.map { it.toString() }.toSet()).apply()
    }

    private fun loadBookmarks(): List<BookmarkItem> {
        val count = prefs.getInt("bookmark_count", 0)
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

    private fun saveBookmarks(list: List<BookmarkItem>) {
        val editor = prefs.edit()
        editor.putInt("bookmark_count", list.size)
        list.forEachIndexed { i, bm ->
            editor.putInt("bm_${i}_surah", bm.surahNumber)
            editor.putString("bm_${i}_name", bm.surahName)
            editor.putInt("bm_${i}_ayah", bm.ayahNumber)
            editor.putString("bm_${i}_ar", bm.arabicSnippet)
            editor.putString("bm_${i}_en", bm.englishSnippet)
        }
        editor.apply()
    }

    fun selectSectionTab(index: Int) {
        _uiState.update { it.copy(selectedSectionTab = index) }
    }

    fun toggleFavorite(surahNumber: Int) {
        val currentFavs = _uiState.value.favoriteSurahNumbers.toMutableSet()
        if (currentFavs.contains(surahNumber)) {
            currentFavs.remove(surahNumber)
        } else {
            currentFavs.add(surahNumber)
        }
        saveFavorites(currentFavs)
        _uiState.update { it.copy(favoriteSurahNumbers = currentFavs) }
    }

    fun toggleBookmarkAyah(fallbackSurah: Surah, ayah: Ayah) {
        val effectiveSurahNum = if (ayah.surahNumber > 0) ayah.surahNumber else fallbackSurah.number
        val effectiveSurahName = _uiState.value.surahs.find { it.number == effectiveSurahNum }?.name ?: fallbackSurah.name

        val currentBookmarks = _uiState.value.bookmarks.toMutableList()
        val existingIndex = currentBookmarks.indexOfFirst {
            it.surahNumber == effectiveSurahNum && it.ayahNumber == ayah.numberInSurah
        }
        if (existingIndex != -1) {
            currentBookmarks.removeAt(existingIndex)
        } else {
            currentBookmarks.add(
                BookmarkItem(
                    surahNumber = effectiveSurahNum,
                    surahName = effectiveSurahName,
                    ayahNumber = ayah.numberInSurah,
                    arabicSnippet = ayah.arabicText.take(45),
                    englishSnippet = ayah.englishText.take(55)
                )
            )
        }
        saveBookmarks(currentBookmarks)
        _uiState.update { it.copy(bookmarks = currentBookmarks) }
    }

    fun removeBookmark(item: BookmarkItem) {
        val currentBookmarks = _uiState.value.bookmarks.toMutableList()
        currentBookmarks.removeAll { it.surahNumber == item.surahNumber && it.ayahNumber == item.ayahNumber }
        saveBookmarks(currentBookmarks)
        _uiState.update { it.copy(bookmarks = currentBookmarks) }
    }

    fun toggleFullScreen() {
        _uiState.update { it.copy(isFullScreenMode = !it.isFullScreenMode) }
    }

    fun toggleAutoScroll() {
        _uiState.update { it.copy(isAutoScrollEnabled = !it.isAutoScrollEnabled) }
    }

    fun toggleSettingsSheet(show: Boolean) {
        _uiState.update { it.copy(showSettingsSheet = show) }
    }

    fun toggleTranslation() {
        _uiState.update { it.copy(showTranslation = !it.showTranslation) }
    }

    fun toggleTransliteration() {
        _uiState.update { it.copy(showTransliteration = !it.showTransliteration) }
    }

    fun toggleIndoPakScript() {
        val nextState = !_uiState.value.useIndoPakScript
        val targetFont = if (nextState) "pdms_saleem" else "hafs"
        setQuranFont(targetFont)
    }

    fun setQuranFont(fontKey: String) {
        val normalized = ArabicFontManager.normalizeKey(fontKey)
        ArabicFontManager.setFont(context, normalized)
        val config = ArabicFontManager.getTypographyConfig(normalized)
        _uiState.update {
            it.copy(
                selectedQuranFont = normalized,
                useIndoPakScript = config.prefersIndoPakScript
            )
        }
    }

    fun toggleUrduTranslation() {
        _uiState.update { it.copy(showUrduTranslation = !it.showUrduTranslation) }
    }

    fun toggleAutoPlay() {
        _uiState.update { it.copy(autoPlayNext = !it.autoPlayNext) }
    }

    fun setReciter(reciterId: String) {
        prefs.edit().putString("selected_reciter", reciterId).apply()
        _uiState.update { it.copy(selectedReciter = reciterId) }
        refreshDownloadedSets()
    }

    fun loadSurahs() {
        viewModelScope.launch {
            getQuranSurahsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoadingSurahs = false, surahsError = null) }
                    }
                    is Resource.Success -> {
                        val list = result.data.orEmpty().ifEmpty { QuranStaticData.ALL_SURAHS }
                        _uiState.update { state ->
                            state.copy(
                                isLoadingSurahs = false,
                                surahs = list,
                                filteredSurahs = filterSurahs(list, state.searchQuery),
                                surahsError = null
                            )
                        }
                        refreshDownloadedSets()
                    }
                    is Resource.Error -> {
                        _uiState.update { state ->
                            val fallback = state.surahs.ifEmpty { QuranStaticData.ALL_SURAHS }
                            state.copy(
                                isLoadingSurahs = false,
                                surahs = fallback,
                                filteredSurahs = filterSurahs(fallback, state.searchQuery),
                                surahsError = null
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
                filteredSurahs = filterSurahs(state.surahs, query)
            )
        }
    }

    private fun filterSurahs(surahs: List<Surah>, rawQuery: String): List<Surah> {
        val cleanQuery = StringUtils.cleanSearchQuery(rawQuery)
        if (cleanQuery.isBlank()) return surahs
        val cleanArabicQuery = StringUtils.removeArabicDiacritics(cleanQuery)
        return surahs.filter { surah ->
            surah.name.contains(cleanQuery, ignoreCase = true) ||
                    surah.englishMeaning.contains(cleanQuery, ignoreCase = true) ||
                    surah.number.toString().contains(cleanQuery) ||
                    StringUtils.removeArabicDiacritics(surah.arabicName).contains(cleanArabicQuery, ignoreCase = true)
        }
    }

    /**
     * Resolves the detail header metadata for a Juz based on whether its first Ayah
     * starts a new Surah (Ayah 1 -> display the Surah name) or continues a Surah from the
     * previous Juz (Ayah > 1 -> display the Juz name).
     */
    private fun resolveJuzTitleSurah(
        juz: JuzInfo,
        surahs: List<Surah>,
        firstAyah: Ayah?,
        verseCount: Int
    ): Surah {
        val startingSurahNum = firstAyah?.surahNumber?.takeIf { it > 0 } ?: juz.startingSurahNumber
        val startingSurah = surahs.find { it.number == startingSurahNum }
            ?: QuranStaticData.ALL_SURAHS.find { it.number == startingSurahNum }
        val resolvedSurahName = startingSurah?.name ?: juz.startingSurahName
        val startsNewSurah = (firstAyah?.numberInSurah ?: juz.startingAyahNumber) == 1

        return if (startsNewSurah) {
            Surah(
                number = startingSurahNum,
                name = resolvedSurahName,
                arabicName = startingSurah?.arabicName ?: juz.arabicName,
                englishMeaning = "Juz ${juz.number} (${juz.englishName})",
                totalVerses = verseCount,
                revelationType = startingSurah?.revelationType ?: "Juz ${juz.number}"
            )
        } else {
            Surah(
                number = startingSurahNum,
                name = "Juz ${juz.number} (${juz.englishName})",
                arabicName = juz.arabicName,
                englishMeaning = "Continues Surah $resolvedSurahName",
                totalVerses = verseCount,
                revelationType = "Juz ${juz.number}"
            )
        }
    }

    /**
     * Opens the entire Juz for reading offline.
     */
    fun selectJuz(juz: JuzInfo) {
        val surahs = _uiState.value.surahs
        val juzTitleSurah = resolveJuzTitleSurah(
            juz = juz,
            surahs = surahs,
            firstAyah = null,
            verseCount = 0
        )
        val isDownloaded = QuranAudioDownloadManager.isJuzDownloaded(
            context,
            _uiState.value.selectedReciter,
            juz.number
        )
        _uiState.update {
            it.copy(
                selectedSurah = juzTitleSurah,
                selectedJuz = juz,
                isLoadingVerses = true,
                versesError = null,
                verses = emptyList(),
                isCurrentSurahDownloaded = isDownloaded
            )
        }
        loadJuzVerses(juz.number)
    }

    private fun loadJuzVerses(juzNumber: Int) {
        viewModelScope.launch {
            getJuzVersesUseCase(juzNumber).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoadingVerses = true) }
                    }
                    is Resource.Success -> {
                        val list = result.data.orEmpty()
                        val firstAyah = list.firstOrNull()
                        val isDownloaded = QuranAudioDownloadManager.isJuzDownloaded(
                            context,
                            _uiState.value.selectedReciter,
                            juzNumber
                        )
                        _uiState.update { state ->
                            val activeJuz = state.selectedJuz?.takeIf { it.number == juzNumber }
                            val updatedJuz = if (activeJuz != null && firstAyah != null) {
                                activeJuz.copy(
                                    startingSurahNumber = firstAyah.surahNumber.takeIf { it > 0 }
                                        ?: activeJuz.startingSurahNumber,
                                    startingSurahName = state.surahs.find { it.number == firstAyah.surahNumber }?.name
                                        ?: activeJuz.startingSurahName,
                                    startingAyahNumber = firstAyah.numberInSurah
                                )
                            } else {
                                activeJuz
                            }
                            val updatedSurah = updatedJuz?.let {
                                resolveJuzTitleSurah(
                                    juz = it,
                                    surahs = state.surahs,
                                    firstAyah = firstAyah,
                                    verseCount = list.size
                                )
                            } ?: state.selectedSurah?.copy(totalVerses = list.size)

                            state.copy(
                                isLoadingVerses = false,
                                verses = list,
                                selectedJuz = updatedJuz ?: state.selectedJuz,
                                selectedSurah = updatedSurah,
                                versesError = null,
                                isCurrentSurahDownloaded = isDownloaded
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingVerses = false,
                                versesError = result.message ?: "Failed to load Juz verses"
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectSurah(surah: Surah) {
        val isDownloaded = QuranAudioDownloadManager.isSurahDownloaded(
            context,
            _uiState.value.selectedReciter,
            surah.number,
            surah.totalVerses
        )
        _uiState.update {
            it.copy(
                selectedSurah = surah,
                selectedJuz = null,
                isLoadingVerses = true,
                versesError = null,
                verses = emptyList(),
                isCurrentSurahDownloaded = isDownloaded
            )
        }
        loadVerses(surah.number)
    }

    fun retryLoadVerses() {
        val currentJuz = _uiState.value.selectedJuz
        val currentSurah = _uiState.value.selectedSurah
        if (currentJuz != null) {
            selectJuz(currentJuz)
        } else if (currentSurah != null) {
            selectSurah(currentSurah)
        }
    }

    fun clearSelectedSurah() {
        stopAudio()
        _uiState.update {
            it.copy(
                selectedSurah = null,
                selectedJuz = null,
                verses = emptyList(),
                currentlyPlayingAyah = null,
                isFullScreenMode = false,
                isCurrentSurahDownloaded = false
            )
        }
        refreshDownloadedSets()
    }

    private fun loadVerses(surahNumber: Int) {
        viewModelScope.launch {
            getSurahVersesUseCase(surahNumber).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoadingVerses = true) }
                    }
                    is Resource.Success -> {
                        val list = result.data.orEmpty()
                        val isDownloaded = _uiState.value.selectedSurah?.let { surah ->
                            QuranAudioDownloadManager.isSurahDownloaded(
                                context,
                                _uiState.value.selectedReciter,
                                surah.number,
                                if (surah.totalVerses > 0) surah.totalVerses else list.size
                            )
                        } ?: false
                        _uiState.update {
                            it.copy(
                                isLoadingVerses = false,
                                verses = list,
                                versesError = null,
                                isCurrentSurahDownloaded = isDownloaded
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingVerses = false,
                                versesError = result.message ?: "Failed to load verses"
                            )
                        }
                    }
                }
            }
        }
    }

    fun toggleColoredTajweedQuran(isColored: Boolean) {
        prefs.edit().putBoolean("is_colored_quran", isColored).apply()
        _uiState.update { it.copy(isColoredTajweedQuran = isColored) }
    }

    /**
     * Prompts the user with a Qari voice selection dialog before downloading the currently open Surah or Juz.
     */
    fun requestDownloadCurrent() {
        val currentJuz = _uiState.value.selectedJuz
        val currentSurah = _uiState.value.selectedSurah
        if (currentJuz != null) {
            requestDownloadJuz(currentJuz)
        } else if (currentSurah != null) {
            requestDownloadSurah(currentSurah)
        }
    }

    /**
     * Prompts the user with a Qari voice selection dialog before downloading a Surah.
     */
    fun requestDownloadSurah(surah: Surah) {
        _uiState.update {
            it.copy(
                showReciterDownloadDialog = true,
                pendingDownloadSurah = surah,
                pendingDownloadJuz = null,
                pendingDownloadTitle = "Surah ${surah.name}"
            )
        }
    }

    /**
     * Prompts the user with a Qari voice selection dialog before downloading a Juz / Para.
     */
    fun requestDownloadJuz(juz: JuzInfo) {
        _uiState.update {
            it.copy(
                showReciterDownloadDialog = true,
                pendingDownloadSurah = null,
                pendingDownloadJuz = juz,
                pendingDownloadTitle = "Para / Juz ${juz.number} (${juz.englishName})"
            )
        }
    }

    fun dismissReciterDownloadDialog() {
        _uiState.update {
            it.copy(
                showReciterDownloadDialog = false,
                pendingDownloadSurah = null,
                pendingDownloadJuz = null,
                pendingDownloadTitle = ""
            )
        }
    }

    fun confirmDownloadWithReciter(reciterId: String) {
        val targetSurah = _uiState.value.pendingDownloadSurah
        val targetJuz = _uiState.value.pendingDownloadJuz

        setReciter(reciterId)
        dismissReciterDownloadDialog()

        if (targetJuz != null) {
            startJuzDownload(targetJuz, reciterId)
        } else if (targetSurah != null) {
            startSurahDownload(targetSurah, reciterId)
        }
    }

    private fun startSurahDownload(surah: Surah, reciterId: String) {
        viewModelScope.launch {
            val reciterName = AVAILABLE_RECITERS.find { it.id == reciterId }?.name ?: reciterId
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Downloading Surah ${surah.name} ($reciterName)...",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // If this Surah is already open and loaded, use its verses directly
            val existingVerses = if (_uiState.value.selectedJuz == null &&
                _uiState.value.selectedSurah?.number == surah.number &&
                _uiState.value.verses.isNotEmpty()
            ) {
                _uiState.value.verses
            } else {
                null
            }

            if (existingVerses != null) {
                val success = QuranAudioDownloadManager.downloadSurahAudio(
                    context = context,
                    okHttpClient = okHttpClient,
                    surah = surah,
                    verses = existingVerses,
                    reciterId = reciterId
                )
                refreshDownloadedSets()
                withContext(Dispatchers.Main) {
                    if (success) {
                        Toast.makeText(
                            context,
                            "Surah ${surah.name} downloaded in $reciterName's voice!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                getSurahVersesUseCase(surah.number).collect { res ->
                    if (res is Resource.Success && !res.data.isNullOrEmpty()) {
                        val success = QuranAudioDownloadManager.downloadSurahAudio(
                            context = context,
                            okHttpClient = okHttpClient,
                            surah = surah,
                            verses = res.data,
                            reciterId = reciterId
                        )
                        refreshDownloadedSets()
                        withContext(Dispatchers.Main) {
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Surah ${surah.name} downloaded in $reciterName's voice!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startJuzDownload(juz: JuzInfo, reciterId: String) {
        viewModelScope.launch {
            val reciterName = AVAILABLE_RECITERS.find { it.id == reciterId }?.name ?: reciterId
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Downloading Juz ${juz.number} ($reciterName)...",
                    Toast.LENGTH_SHORT
                ).show()
            }

            getJuzVersesUseCase(juz.number).collect { res ->
                if (res is Resource.Success && !res.data.isNullOrEmpty()) {
                    val success = QuranAudioDownloadManager.downloadJuzAudio(
                        context = context,
                        okHttpClient = okHttpClient,
                        juzNumber = juz.number,
                        juzTitle = juz.englishName,
                        verses = res.data,
                        reciterId = reciterId
                    )
                    refreshDownloadedSets()
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(
                                context,
                                "Juz ${juz.number} (${juz.englishName}) downloaded in $reciterName's voice!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    fun downloadCurrentSurah() {
        requestDownloadCurrent()
    }

    fun downloadSurah(surah: Surah) {
        requestDownloadSurah(surah)
    }

    fun downloadJuz(juz: JuzInfo) {
        requestDownloadJuz(juz)
    }

    fun cancelAudioDownload() {
        QuranAudioDownloadManager.cancelDownload()
        Toast.makeText(context, "Audio download cancelled", Toast.LENGTH_SHORT).show()
    }

    fun deleteSurahAudio(surahNumber: Int) {
        QuranAudioDownloadManager.deleteSurahAudio(context, _uiState.value.selectedReciter, surahNumber)
        refreshDownloadedSets()
        Toast.makeText(context, "Downloaded audio removed", Toast.LENGTH_SHORT).show()
    }

    fun checkCurrentSurahDownloaded() {
        refreshDownloadedSets()
    }

    fun refreshDownloadedStates() {
        refreshDownloadedSets()
    }

    fun isSurahDownloaded(surah: Surah): Boolean {
        return _uiState.value.downloadedSurahNumbers.contains(surah.number) ||
                QuranAudioDownloadManager.isSurahDownloaded(
                    context,
                    _uiState.value.selectedReciter,
                    surah.number,
                    surah.totalVerses
                )
    }

    fun isJuzDownloaded(juz: JuzInfo): Boolean {
        return _uiState.value.downloadedJuzNumbers.contains(juz.number) ||
                QuranAudioDownloadManager.isJuzDownloaded(
                    context,
                    _uiState.value.selectedReciter,
                    juz.number
                )
    }

    private fun getReciterAudioUrl(ayah: Ayah, reciterId: String): String {
        val surahNum = if (ayah.surahNumber > 0) ayah.surahNumber else (_uiState.value.selectedSurah?.number ?: 1)
        return QuranSurahUtils.getAudioUrl(reciterId, surahNum, ayah.numberInSurah)
    }

    fun playAyahAudio(ayah: Ayah) {
        try {
            val playId = if (ayah.globalAyahNumber > 0) ayah.globalAyahNumber else ayah.numberInSurah
            if (_uiState.value.currentlyPlayingAyah == playId) {
                stopAudio()
                return
            }

            mediaPlayer?.release()
            val reciterId = _uiState.value.selectedReciter
            val surahNum = if (ayah.surahNumber > 0) ayah.surahNumber else (_uiState.value.selectedSurah?.number ?: 1)
            val localFile = QuranAudioDownloadManager.getLocalAyahFile(context, reciterId, surahNum, ayah.numberInSurah)

            mediaPlayer = MediaPlayer().apply {
                if (localFile.exists() && localFile.length() > 512) {
                    setDataSource(localFile.absolutePath)
                } else {
                    val audioUrl = getReciterAudioUrl(ayah, reciterId)
                    setDataSource(audioUrl)
                }
                setOnPreparedListener {
                    start()
                    _uiState.update { it.copy(currentlyPlayingAyah = playId) }
                }
                setOnCompletionListener {
                    _uiState.update { it.copy(currentlyPlayingAyah = null) }
                    if (_uiState.value.autoPlayNext) {
                        val currentList = _uiState.value.verses
                        val currentIndex = currentList.indexOfFirst {
                            (if (it.globalAyahNumber > 0) it.globalAyahNumber else it.numberInSurah) == playId
                        }
                        if (currentIndex != -1 && currentIndex + 1 < currentList.size) {
                            playAyahAudio(currentList[currentIndex + 1])
                        }
                    }
                }
                setOnErrorListener { _, _, _ ->
                    if (_uiState.value.selectedReciter != "ar.alafasy") {
                        try {
                            mediaPlayer?.reset()
                            mediaPlayer?.setDataSource(QuranSurahUtils.getAudioUrl("ar.alafasy", surahNum, ayah.numberInSurah))
                            mediaPlayer?.prepareAsync()
                        } catch (ex: Exception) {
                            _uiState.update { it.copy(currentlyPlayingAyah = null) }
                        }
                    } else {
                        _uiState.update { it.copy(currentlyPlayingAyah = null) }
                    }
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(currentlyPlayingAyah = null) }
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            // ignore
        }
        _uiState.update { it.copy(currentlyPlayingAyah = null) }
    }

    fun setArabicFontSize(size: Float) {
        val clamped = size.coerceIn(18f, 44f)
        prefs.edit().putFloat("arabic_font_size", clamped).apply()
        _uiState.update { it.copy(arabicFontSize = clamped) }
    }

    fun setTranslationFontSize(size: Float) {
        val clamped = size.coerceIn(12f, 26f)
        prefs.edit().putFloat("translation_font_size", clamped).apply()
        _uiState.update { it.copy(translationFontSize = clamped) }
    }

    fun adjustFontSize(delta: Float) {
        setArabicFontSize(_uiState.value.arabicFontSize + delta)
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }

    private fun createJuzList(): List<JuzInfo> = listOf(
        JuzInfo(1, "آلم", "Alif Laam Meem", 1, "Al-Faatiha", 148),
        JuzInfo(2, "سَيَقُولُ", "Sayaqool", 2, "Al-Baqara", 111),
        JuzInfo(3, "تِلْكَ الرُّسُلُ", "Tilkar Rusul", 2, "Al-Baqara", 126),
        JuzInfo(4, "لَنْ تَنَالُوا", "Lan Tanaalu", 3, "Aal-i-Imraan", 131),
        JuzInfo(5, "وَالْمُحْصَنَاتُ", "Wal Muhsanat", 4, "An-Nisaa", 124),
        JuzInfo(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", 4, "An-Nisaa", 110),
        JuzInfo(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'u", 5, "Al-Maaida", 149),
        JuzInfo(8, "وَلَوْ أَنَّنَا", "Wa Law Annana", 6, "Al-An'aam", 142),
        JuzInfo(9, "قَالَ الْمَلَأُ", "Qalal Mala'u", 7, "Al-A'raaf", 159),
        JuzInfo(10, "وَاعْلَمُوا", "Wa'lamoo", 8, "Al-Anfaal", 127),
        JuzInfo(11, "يَعْتَذِرُونَ", "Ya'taziroon", 9, "At-Tawba", 151),
        JuzInfo(12, "وَمَا مِنْ دَابَّةٍ", "Wa Ma Min Dabbah", 11, "Hud", 170),
        JuzInfo(13, "وَمَا أُبَرِّئُ", "Wa Ma Ubarri'u", 12, "Yusuf", 154),
        JuzInfo(14, "رُبَمَا", "Rubama", 15, "Al-Hijr", 227),
        JuzInfo(15, "سُبْحَانَ الَّذِي", "Subhanallazi", 17, "Al-Israa", 185),
        JuzInfo(16, "قَالَ أَلَمْ", "Qal Alam", 18, "Al-Kahf", 269),
        JuzInfo(17, "اقْتَرَبَ لِلنَّاسِ", "Iqtaraba Linnaas", 21, "Al-Anbiyaa", 190),
        JuzInfo(18, "قَدْ أَفْلَحَ", "Qad Aflaha", 23, "Al-Muminoon", 202),
        JuzInfo(19, "وَقَالَ الَّذِينَ", "Wa Qalal Lazeena", 25, "Al-Furqaan", 339),
        JuzInfo(20, "أَمَّنْ خَلَقَ", "Amman Khalaq", 27, "An-Naml", 171),
        JuzInfo(21, "اتْلُ مَا أُوحِيَ", "Utlu Ma Oohiya", 29, "Al-Ankaboot", 178),
        JuzInfo(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", 33, "Al-Ahzaab", 169),
        JuzInfo(23, "وَمَا لِيَ", "Wa Maliya", 36, "Yaseen", 357),
        JuzInfo(24, "فَمَنْ أَظْلَمُ", "Faman Azlamu", 39, "Az-Zumar", 175),
        JuzInfo(25, "إِلَيْهِ يُرَدُّ", "Ilayhi Yuraddu", 41, "Fussilat", 246),
        JuzInfo(26, "حم", "Ha-Meem", 46, "Al-Ahqaf", 195),
        JuzInfo(27, "قَالَ فَمَا خطْبُكُمْ", "Qala Fama Khatbukum", 51, "Az-Zariyat", 399),
        JuzInfo(28, "قَدْ سَمِعَ اللَّهُ", "Qad Sami'allah", 58, "Al-Mujadila", 137),
        JuzInfo(29, "تَبَارَكَ الَّذِي", "Tabarakallazi", 67, "Al-Mulk", 431),
        JuzInfo(30, "عَمَّ يَتَسَاءَلُونَ", "'Amma Yatasa'aloon", 78, "An-Naba", 564)
    )
}
