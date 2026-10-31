package com.ne.muslimprayers.qiblafinder.ui.quran

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ne.muslimprayers.qiblafinder.core.common.Resource
import com.ne.muslimprayers.qiblafinder.core.utils.StringUtils
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.domain.usecase.quran.GetQuranSurahsUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.quran.GetSurahVersesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.widget.Toast
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranAudioDownloadManager
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranDownloadStatus
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranSurahUtils
import kotlinx.coroutines.Dispatchers
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
    val startingSurahName: String
)

val DEFAULT_FAVORITE_SURAHS = listOf(
    18, // Surah Al-Kahf
    32, // Surah As-Sajdah
    36, // Surah Ya-Sin
    55, // Surah Ar-Rahman
    56, // Surah Al-Waqi'ah
    67  // Surah Al-Mulk
)

data class QuranUiState(
    val surahs: List<Surah> = emptyList(),
    val searchQuery: String = "",
    val filteredSurahs: List<Surah> = emptyList(),
    val selectedSectionTab: Int = 0, // 0 = Surahs, 1 = Juz, 2 = Favorites, 3 = Bookmarks
    val juzList: List<JuzInfo> = emptyList(),
    val favoriteSurahNumbers: Set<Int> = DEFAULT_FAVORITE_SURAHS.toSet(),
    val bookmarks: List<BookmarkItem> = emptyList(),
    val selectedSurah: Surah? = null,
    val verses: List<Ayah> = emptyList(),
    val isLoadingSurahs: Boolean = false,
    val surahsError: String? = null,
    val isLoadingVerses: Boolean = false,
    val versesError: String? = null,
    val currentlyPlayingAyah: Int? = null,
    val arabicFontSize: Float = 26f,
    val showTranslation: Boolean = true,
    val showTransliteration: Boolean = false,
    val autoPlayNext: Boolean = true,
    val selectedReciter: String = "ar.alafasy",
    val showSettingsSheet: Boolean = false,
    val isFullScreenMode: Boolean = false,
    val isAutoScrollEnabled: Boolean = true,
    val useIndoPakScript: Boolean = true,
    val selectedQuranFont: String = "indopak", // "indopak", "uthmani", "pdms"
    val showUrduTranslation: Boolean = true,
    val isColoredTajweedQuran: Boolean = true,
    val isCurrentSurahDownloaded: Boolean = false,
    val downloadStatus: QuranDownloadStatus = QuranDownloadStatus()
)

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getQuranSurahsUseCase: GetQuranSurahsUseCase,
    private val getSurahVersesUseCase: GetSurahVersesUseCase,
    val quranApiService: com.ne.muslimprayers.qiblafinder.data.remote.api.QuranApiService,
    private val okHttpClient: OkHttpClient,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        QuranUiState(
            juzList = createJuzList(),
            favoriteSurahNumbers = loadFavorites(),
            bookmarks = loadBookmarks(),
            selectedReciter = prefs.getString("selected_reciter", "ar.alafasy") ?: "ar.alafasy",
            isColoredTajweedQuran = prefs.getBoolean("is_colored_quran", true)
        )
    )
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    init {
        loadSurahs()
        observeDownloadStatus()
    }

    private fun observeDownloadStatus() {
        viewModelScope.launch {
            QuranAudioDownloadManager.downloadStatus.collect { status ->
                _uiState.update { state ->
                    val isDownloaded = state.selectedSurah?.let { surah ->
                        QuranAudioDownloadManager.isSurahDownloaded(
                            context,
                            state.selectedReciter,
                            surah.number,
                            surah.totalVerses
                        )
                    } ?: false
                    state.copy(downloadStatus = status, isCurrentSurahDownloaded = isDownloaded)
                }
            }
        }
    }

    private fun loadFavorites(): Set<Int> {
        val saved = prefs.getStringSet("favorite_surahs", null)
        return saved?.mapNotNull { it.toIntOrNull() }?.toSet() ?: DEFAULT_FAVORITE_SURAHS.toSet()
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

    fun toggleBookmarkAyah(surah: Surah, ayah: Ayah) {
        val currentBookmarks = _uiState.value.bookmarks.toMutableList()
        val existingIndex = currentBookmarks.indexOfFirst {
            it.surahNumber == surah.number && it.ayahNumber == ayah.numberInSurah
        }
        if (existingIndex != -1) {
            currentBookmarks.removeAt(existingIndex)
        } else {
            currentBookmarks.add(
                BookmarkItem(
                    surahNumber = surah.number,
                    surahName = surah.name,
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
        _uiState.update {
            val nextState = !it.useIndoPakScript
            it.copy(
                useIndoPakScript = nextState,
                selectedQuranFont = if (nextState) "indopak" else "uthmani"
            )
        }
    }

    fun setQuranFont(fontKey: String) {
        _uiState.update {
            it.copy(
                selectedQuranFont = fontKey,
                useIndoPakScript = (fontKey == "indopak" || fontKey == "pdms")
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
    }

    fun loadSurahs() {
        viewModelScope.launch {
            getQuranSurahsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoadingSurahs = true, surahsError = null) }
                    }
                    is Resource.Success -> {
                        val list = result.data.orEmpty()
                        _uiState.update { state ->
                            state.copy(
                                isLoadingSurahs = false,
                                surahs = list,
                                filteredSurahs = filterSurahs(list, state.searchQuery),
                                surahsError = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingSurahs = false,
                                surahsError = result.message ?: "Failed to load Surah list"
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

    fun selectJuz(juz: JuzInfo) {
        val targetSurah = _uiState.value.surahs.find { it.number == juz.startingSurahNumber }
            ?: Surah(
                number = juz.startingSurahNumber,
                name = juz.startingSurahName,
                arabicName = "",
                englishMeaning = juz.englishName,
                totalVerses = 0,
                revelationType = ""
            )
        selectSurah(targetSurah)
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
                isLoadingVerses = true,
                versesError = null,
                verses = emptyList(),
                isCurrentSurahDownloaded = isDownloaded
            )
        }
        loadVerses(surah.number)
    }

    fun clearSelectedSurah() {
        stopAudio()
        _uiState.update {
            it.copy(
                selectedSurah = null,
                verses = emptyList(),
                currentlyPlayingAyah = null,
                isFullScreenMode = false,
                isCurrentSurahDownloaded = false
            )
        }
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

    fun downloadCurrentSurah() {
        val surah = _uiState.value.selectedSurah ?: return
        val verses = _uiState.value.verses
        if (verses.isEmpty()) return
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Downloading Surah ${surah.name} audio for offline listening...", Toast.LENGTH_SHORT).show()
            }
            val success = QuranAudioDownloadManager.downloadSurahAudio(
                context = context,
                okHttpClient = okHttpClient,
                surah = surah,
                verses = verses,
                reciterId = _uiState.value.selectedReciter
            )
            checkCurrentSurahDownloaded()
            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(context, "Surah ${surah.name} downloaded successfully! Available offline.", Toast.LENGTH_LONG).show()
                } else if (!QuranAudioDownloadManager.downloadStatus.value.isDownloading) {
                    Toast.makeText(context, "Audio download completed or interrupted.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun downloadSurah(surah: Surah) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Preparing Surah ${surah.name} audio download...", Toast.LENGTH_SHORT).show()
            }
            getSurahVersesUseCase(surah.number).collect { res ->
                if (res is Resource.Success && !res.data.isNullOrEmpty()) {
                    val success = QuranAudioDownloadManager.downloadSurahAudio(
                        context = context,
                        okHttpClient = okHttpClient,
                        surah = surah,
                        verses = res.data,
                        reciterId = _uiState.value.selectedReciter
                    )
                    checkCurrentSurahDownloaded()
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "Surah ${surah.name} audio downloaded for offline use!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    fun downloadJuz(juz: JuzInfo) {
        viewModelScope.launch {
            val surah = _uiState.value.surahs.find { it.number == juz.startingSurahNumber } ?: return@launch
            downloadSurah(surah)
        }
    }

    fun cancelAudioDownload() {
        QuranAudioDownloadManager.cancelDownload()
        Toast.makeText(context, "Audio download cancelled", Toast.LENGTH_SHORT).show()
    }

    fun deleteSurahAudio(surahNumber: Int) {
        QuranAudioDownloadManager.deleteSurahAudio(context, _uiState.value.selectedReciter, surahNumber)
        checkCurrentSurahDownloaded()
        Toast.makeText(context, "Downloaded audio removed", Toast.LENGTH_SHORT).show()
    }

    fun checkCurrentSurahDownloaded() {
        val surah = _uiState.value.selectedSurah ?: return
        val isDownloaded = QuranAudioDownloadManager.isSurahDownloaded(
            context,
            _uiState.value.selectedReciter,
            surah.number,
            surah.totalVerses
        )
        _uiState.update { it.copy(isCurrentSurahDownloaded = isDownloaded) }
    }

    fun isSurahDownloaded(surah: Surah): Boolean {
        return QuranAudioDownloadManager.isSurahDownloaded(
            context,
            _uiState.value.selectedReciter,
            surah.number,
            surah.totalVerses
        )
    }

    private fun getReciterAudioUrl(ayah: Ayah, reciterId: String): String {
        val surahNum = _uiState.value.selectedSurah?.number ?: 1
        return QuranSurahUtils.getAudioUrl(reciterId, surahNum, ayah.numberInSurah)
    }

    fun playAyahAudio(ayah: Ayah) {
        try {
            if (_uiState.value.currentlyPlayingAyah == ayah.numberInSurah) {
                stopAudio()
                return
            }

            mediaPlayer?.release()
            val reciterId = _uiState.value.selectedReciter
            val surahNum = _uiState.value.selectedSurah?.number ?: 1
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
                    _uiState.update { it.copy(currentlyPlayingAyah = ayah.numberInSurah) }
                }
                setOnCompletionListener {
                    _uiState.update { it.copy(currentlyPlayingAyah = null) }
                    if (_uiState.value.autoPlayNext) {
                        val currentList = _uiState.value.verses
                        val currentIndex = currentList.indexOfFirst { it.numberInSurah == ayah.numberInSurah }
                        if (currentIndex != -1 && currentIndex + 1 < currentList.size) {
                            playAyahAudio(currentList[currentIndex + 1])
                        }
                    }
                }
                setOnErrorListener { _, _, _ ->
                    // Fallback to default Alafasy reciter if custom Qirah fails
                    if (_uiState.value.selectedReciter != "ar.alafasy") {
                        try {
                            mediaPlayer?.reset()
                            mediaPlayer?.setDataSource(ayah.audioUrl)
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

    fun adjustFontSize(delta: Float) {
        _uiState.update {
            val newSize = (it.arabicFontSize + delta).coerceIn(20f, 44f)
            it.copy(arabicFontSize = newSize)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }

    private fun createJuzList(): List<JuzInfo> = listOf(
        JuzInfo(1, "الم (آلم)", "Alif Lam Meem", 1, "Al-Fatihah"),
        JuzInfo(2, "سَيَقُولُ", "Sayaqool", 2, "Al-Baqarah"),
        JuzInfo(3, "تِلْكَ الرُّسُلُ", "Tilkar Rusul", 2, "Al-Baqarah"),
        JuzInfo(4, "لَنْ تَنَالُوا", "Lan Tanaalu", 3, "Ali 'Imran"),
        JuzInfo(5, "وَالْمُحْصَنَاتُ", "Wal Muhsanat", 4, "An-Nisa"),
        JuzInfo(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", 4, "An-Nisa"),
        JuzInfo(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'u", 5, "Al-Ma'idah"),
        JuzInfo(8, "وَلَوْ أَنَّنَا", "Wa Law Annana", 6, "Al-An'am"),
        JuzInfo(9, "قَالَ الْمَلَأُ", "Qalal Mala'u", 7, "Al-A'raf"),
        JuzInfo(10, "وَاعْلَمُوا", "Wa'lamoo", 8, "Al-Anfal"),
        JuzInfo(11, "يَعْتَذِرُونَ", "Ya'taziroon", 9, "At-Tawbah"),
        JuzInfo(12, "وَمَا مِنْ دَابَّةٍ", "Wa Ma Min Dabbah", 11, "Hud"),
        JuzInfo(13, "وَمَا أُبَرِّئُ", "Wa Ma Ubarri'u", 12, "Yusuf"),
        JuzInfo(14, "رُبَمَا", "Rubama", 15, "Al-Hijr"),
        JuzInfo(15, "سُبْحَانَ الَّذِي", "Subhanallazi", 17, "Al-Isra"),
        JuzInfo(16, "قَالَ أَلَمْ", "Qal Alam", 18, "Al-Kahf"),
        JuzInfo(17, "اقْتَرَبَ لِلنَّاسِ", "Iqtaraba Linnaas", 21, "Al-Anbiya"),
        JuzInfo(18, "قَدْ أَفْلَحَ", "Qad Aflaha", 23, "Al-Mu'minun"),
        JuzInfo(19, "وَقَالَ الَّذِينَ", "Wa Qalal Lazeena", 25, "Al-Furqan"),
        JuzInfo(20, "أَمَّنْ خَلَقَ", "Amman Khalaq", 27, "An-Naml"),
        JuzInfo(21, "اتْلُ مَا أُوحِيَ", "Utlu Ma Oohiya", 29, "Al-'Ankabut"),
        JuzInfo(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", 33, "Al-Ahzab"),
        JuzInfo(23, "وَمَا لِيَ", "Wa Maliya", 36, "Ya-Sin"),
        JuzInfo(24, "فَمَنْ أَظْلَمُ", "Faman Azlamu", 39, "Az-Zumar"),
        JuzInfo(25, "إِلَيْهِ يُرَدُّ", "Ilayhi Yuraddu", 41, "Fussilat"),
        JuzInfo(26, "حم", "Ha-Meem", 46, "Al-Ahqaf"),
        JuzInfo(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum", 51, "Az-Zariyat"),
        JuzInfo(28, "قَدْ سَمِعَ اللَّهُ", "Qad Sami'allah", 58, "Al-Mujadila"),
        JuzInfo(29, "تَبَارَكَ الَّذِي", "Tabarakallazi", 67, "Al-Mulk"),
        JuzInfo(30, "عَمَّ يَتَسَاءَلُونَ", "'Amma Yatasa'aloon", 78, "An-Naba")
    )
}
