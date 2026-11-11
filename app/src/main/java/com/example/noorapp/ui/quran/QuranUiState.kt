package com.example.noorapp.ui.quran

import androidx.annotation.StringRes
import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.model.BookmarkItem
import com.example.noorapp.domain.model.DEFAULT_FAVORITE_SURAHS
import com.example.noorapp.domain.model.QuranDownloadStatus
import com.example.noorapp.domain.model.QuranStaticData
import com.example.noorapp.domain.model.Surah

sealed interface QuranUiEvent {
    data class ShowToast(
        @StringRes val messageRes: Int,
        val formatArgs: List<Any> = emptyList()
    ) : QuranUiEvent
}

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

data class TafseerOption(
    val id: Int,
    val name: String,
    val author: String,
    val language: String, // "ur", "en", "ar", "bn"
    val languageLabel: String
)

val AVAILABLE_TAFSEERS = listOf(
    TafseerOption(159, "Bayan ul Quran", "Dr. Israr Ahmad", "ur", "اردو"),
    TafseerOption(160, "Tafsir Ibn Kathir (Urdu)", "Hafiz Ibn Kathir", "ur", "اردو"),
    TafseerOption(169, "Ibn Kathir (English)", "Hafiz Ibn Kathir", "en", "English"),
    TafseerOption(16, "Tafsir Muyassar (Arabic)", "King Fahd Complex", "ar", "العربية"),
    TafseerOption(165, "Tafsir Ahsanul Bayaan (Bengali)", "Salahuddin Yusuf", "bn", "বাংলা")
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
    val showWaqfGuideDialog: Boolean = false,
    val isFullScreenMode: Boolean = false,
    val isAutoScrollEnabled: Boolean = true,
    val isContinuousScrolling: Boolean = false,
    val scrollSpeed: Float = 1.0f,
    val useIndoPakScript: Boolean = false,
    val selectedQuranFont: String = "muhammadi",
    val showUrduTranslation: Boolean = true,
    val isColoredTajweedQuran: Boolean = true,
    val isCurrentSurahDownloaded: Boolean = false,
    val downloadedSurahNumbers: Set<Int> = emptySet(),
    val downloadedJuzNumbers: Set<Int> = emptySet(),
    val totalAudioStorageMb: Double = 0.0,
    val downloadStatus: QuranDownloadStatus = QuranDownloadStatus(),
    val showReciterDownloadDialog: Boolean = false,
    val pendingDownloadSurah: Surah? = null,
    val pendingDownloadJuz: JuzInfo? = null,
    val pendingDownloadReciterId: String = "ar.alafasy",
    val selectedTafseerAyah: Ayah? = null,
    val selectedTafseerId: Int = 159,
    val tafseerContent: String = "",
    val isLoadingTafseer: Boolean = false,
    val tafseerError: String? = null
)

