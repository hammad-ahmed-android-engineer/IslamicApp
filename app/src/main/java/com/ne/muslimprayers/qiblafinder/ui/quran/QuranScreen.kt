package com.ne.muslimprayers.qiblafinder.ui.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicSearchBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import com.ne.muslimprayers.qiblafinder.ui.quran.components.JuzListItemCard
import com.ne.muslimprayers.qiblafinder.ui.quran.components.QuranSettingsSheetContent
import com.ne.muslimprayers.qiblafinder.ui.quran.components.SurahDetailReader
import com.ne.muslimprayers.qiblafinder.ui.quran.components.SurahListItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: QuranViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Settings Bottom Sheet
    if (state.showSettingsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.toggleSettingsSheet(false) },
            sheetState = sheetState,
            containerColor = WarmParchment
        ) {
            QuranSettingsSheetContent(
                state = state,
                onToggleTranslation = viewModel::toggleTranslation,
                onToggleTransliteration = viewModel::toggleTransliteration,
                onToggleIndoPakScript = viewModel::toggleIndoPakScript,
                onSetQuranFont = viewModel::setQuranFont,
                onToggleUrduTranslation = viewModel::toggleUrduTranslation,
                onToggleColoredTajweedQuran = viewModel::toggleColoredTajweedQuran,
                onDownloadSurah = { viewModel.downloadCurrentSurah() },
                onDeleteSurahAudio = { state.selectedSurah?.let { viewModel.deleteSurahAudio(it.number) } },
                onClearAllAudio = { state.selectedSurah?.let { viewModel.checkCurrentSurahDownloaded() } },
                onToggleAutoPlay = viewModel::toggleAutoPlay,
                onToggleAutoScroll = viewModel::toggleAutoScroll,
                onSetReciter = viewModel::setReciter,
                onAdjustFontSize = viewModel::adjustFontSize,
                onDismiss = { viewModel.toggleSettingsSheet(false) }
            )
        }
    }

    var selectedTafseerAyah by remember { mutableStateOf<Ayah?>(null) }
    val currentTafseerAyah = selectedTafseerAyah

    // Ayah Tafseer Dialog
    if (currentTafseerAyah != null && state.selectedSurah != null) {
        com.ne.muslimprayers.qiblafinder.ui.quran.components.AyahTafseerDialog(
            surahNumber = state.selectedSurah!!.number,
            surahName = state.selectedSurah!!.name,
            ayah = currentTafseerAyah,
            quranApiService = viewModel.quranApiService,
            onDismiss = { selectedTafseerAyah = null }
        )
    }

    if (state.selectedSurah != null) {
        BackHandler {
            viewModel.clearSelectedSurah()
        }

        val bookmarkedAyahSet = remember(state.bookmarks, state.selectedSurah) {
            val sNum = state.selectedSurah!!.number
            state.bookmarks.filter { it.surahNumber == sNum }.map { it.ayahNumber }.toSet()
        }

        SurahDetailReader(
            surah = state.selectedSurah!!,
            verses = state.verses,
            isLoading = state.isLoadingVerses,
            errorMessage = state.versesError,
            currentlyPlayingAyah = state.currentlyPlayingAyah,
            arabicFontSize = state.arabicFontSize,
            showTranslation = state.showTranslation,
            showTransliteration = state.showTransliteration,
            showUrduTranslation = state.showUrduTranslation,
            selectedQuranFont = state.selectedQuranFont,
            useIndoPakScript = state.useIndoPakScript,
            isColoredQuran = state.isColoredTajweedQuran,
            isDownloaded = state.isCurrentSurahDownloaded,
            downloadStatus = state.downloadStatus,
            bookmarkedAyahNumbers = bookmarkedAyahSet,
            isLandscape = isLandscape,
            isFullScreen = state.isFullScreenMode,
            isAutoScrollEnabled = state.isAutoScrollEnabled,
            onToggleFullScreen = viewModel::toggleFullScreen,
            onToggleAutoScroll = viewModel::toggleAutoScroll,
            onToggleColoredQuran = { viewModel.toggleColoredTajweedQuran(!state.isColoredTajweedQuran) },
            onDownloadSurah = { viewModel.downloadCurrentSurah() },
            onDeleteSurahAudio = { state.selectedSurah?.let { viewModel.deleteSurahAudio(it.number) } },
            onBackClick = { viewModel.clearSelectedSurah() },
            onPlayAyah = { viewModel.playAyahAudio(it) },
            onAdjustFontSize = { viewModel.adjustFontSize(it) },
            onOpenSettings = { viewModel.toggleSettingsSheet(true) },
            onBookmarkAyah = { ayah ->
                viewModel.toggleBookmarkAyah(state.selectedSurah!!, ayah)
            },
            onRetry = viewModel::retryLoadVerses,
            onOpenTafseer = { ayah -> selectedTafseerAyah = ayah },
            onCopyAyah = { ayah ->
                val isIndoPakEffective = state.useIndoPakScript || state.selectedQuranFont == "indopak" || state.selectedQuranFont == "pdms"
                val rawArabic = if (isIndoPakEffective && ayah.indopakText.isNotBlank()) ayah.indopakText else ayah.arabicText
                val formattedArabic = com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter.getPlainTextWithEndSign(
                    rawArabic,
                    ayah.numberInSurah,
                    state.selectedSurah!!.number
                )
                val urduPart = if (state.showUrduTranslation && ayah.urduText.isNotBlank()) "\n\n${ayah.urduText}" else ""
                val engPart = if (state.showTranslation && ayah.englishText.isNotBlank()) "\n\n${ayah.englishText}" else ""
                val text = "$formattedArabic$urduPart$engPart\n‹ ${state.selectedSurah!!.number}.${ayah.numberInSurah} › Surah ${state.selectedSurah!!.name}"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(context, "Ayah", text, "Ayah copied to clipboard")
            },
            onShareAyah = { ayah ->
                val isIndoPakEffective = state.useIndoPakScript || state.selectedQuranFont == "indopak" || state.selectedQuranFont == "pdms"
                val rawArabic = if (isIndoPakEffective && ayah.indopakText.isNotBlank()) ayah.indopakText else ayah.arabicText
                val formattedArabic = com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter.getPlainTextWithEndSign(
                    rawArabic,
                    ayah.numberInSurah,
                    state.selectedSurah!!.number
                )
                val urduPart = if (state.showUrduTranslation && ayah.urduText.isNotBlank()) "\n\n${ayah.urduText}" else ""
                val engPart = if (state.showTranslation && ayah.englishText.isNotBlank()) "\n\n${ayah.englishText}" else ""
                val text = "$formattedArabic$urduPart$engPart\n\n— Holy Quran ‹ ${state.selectedSurah!!.number}.${ayah.numberInSurah} › (Surah ${state.selectedSurah!!.name})"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(context, text, "Share Ayah via")
            }
        )
    } else {
        Scaffold(
            topBar = {
                IslamicTopAppBar(
                    title = "Holy Quran (القرآن الكريم)",
                    actions = {
                        IconButton(onClick = { viewModel.toggleSettingsSheet(true) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Quran Settings",
                                tint = MedinaSage
                            )
                        }
                    }
                )
            },
            containerColor = WarmParchment,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.sdp)
            ) {
                // Section Tabs: Surahs, Juz, Favorites, Bookmarks
                ScrollableTabRow(
                    selectedTabIndex = state.selectedSectionTab,
                    containerColor = WarmParchment,
                    contentColor = MedinaSage,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedSectionTab]),
                            color = MedinaSage,
                            height = 2.5.sdp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = state.selectedSectionTab == 0,
                        onClick = { viewModel.selectSectionTab(0) },
                        modifier = Modifier.height(36.sdp),
                        text = {
                            Text(
                                text = "Surahs (${state.surahs.size})",
                                fontSize = 13.sp,
                                fontWeight = if (state.selectedSectionTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 0) MedinaSage else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedSectionTab == 1,
                        onClick = { viewModel.selectSectionTab(1) },
                        modifier = Modifier.height(36.sdp),
                        text = {
                            Text(
                                text = "Juz (30)",
                                fontSize = 13.sp,
                                fontWeight = if (state.selectedSectionTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 1) MedinaSage else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedSectionTab == 2,
                        onClick = { viewModel.selectSectionTab(2) },
                        modifier = Modifier.height(36.sdp),
                        text = {
                            Text(
                                text = "Favorites (${state.favoriteSurahNumbers.size})",
                                fontSize = 13.sp,
                                fontWeight = if (state.selectedSectionTab == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 2) MedinaSage else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedSectionTab == 3,
                        onClick = { viewModel.selectSectionTab(3) },
                        modifier = Modifier.height(36.sdp),
                        text = {
                            Text(
                                text = "Bookmarks (${state.bookmarks.size})",
                                fontSize = 13.sp,
                                fontWeight = if (state.selectedSectionTab == 3) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 3) MedinaSage else TextSecondary
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.sdp))

                when (state.selectedSectionTab) {
                    0 -> {
                        // All Surahs Tab with Compact Search Bar
                        IslamicSearchBar(
                            query = state.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            placeholder = "Search Surah by name, translation, or number...",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.sdp))

                        if (state.isLoadingSurahs && state.surahs.isEmpty()) {
                            LoadingState(message = "Loading Holy Quran Surahs...")
                        } else if (state.surahsError != null && state.surahs.isEmpty()) {
                            ErrorState(message = state.surahsError ?: "Failed to load Surahs", onRetry = viewModel::loadSurahs)
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 32.sdp),
                                verticalArrangement = Arrangement.spacedBy(10.sdp)
                            ) {
                                items(state.filteredSurahs) { surah ->
                                    SurahListItemCard(
                                        surah = surah,
                                        isDownloaded = viewModel.isSurahDownloaded(surah),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.downloadSurah(surah) },
                                        onClick = { viewModel.selectSurah(surah) }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Juz Tab
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 32.sdp),
                            verticalArrangement = Arrangement.spacedBy(10.sdp)
                        ) {
                            items(state.juzList) { juz ->
                                JuzListItemCard(
                                    juz = juz,
                                    isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${juz.startingSurahNumber}",
                                    onDownloadClick = { viewModel.downloadJuz(juz) },
                                    onClick = { viewModel.selectJuz(juz) }
                                )
                            }
                        }
                    }

                    2 -> {
                        // Favorites Surah Tab
                        val favoriteSurahs = state.surahs.filter { state.favoriteSurahNumbers.contains(it.number) }
                        if (favoriteSurahs.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(32.sdp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = DesertOchre,
                                        modifier = Modifier.size(48.sdp)
                                    )
                                    Spacer(modifier = Modifier.height(12.sdp))
                                    AppText.BodyMedium(
                                        text = "No Favorite Surahs Saved",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.sdp))
                                    AppText.Caption(
                                        text = "Tap the heart on any Surah to save it here",
                                        color = TextSecondary
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 32.sdp),
                                verticalArrangement = Arrangement.spacedBy(10.sdp)
                            ) {
                                items(favoriteSurahs) { surah ->
                                    SurahListItemCard(
                                        surah = surah,
                                        isDownloaded = viewModel.isSurahDownloaded(surah),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.downloadSurah(surah) },
                                        onClick = { viewModel.selectSurah(surah) }
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // Bookmarks Tab
                        if (state.bookmarks.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(32.sdp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = MedinaSage,
                                        modifier = Modifier.size(48.sdp)
                                    )
                                    Spacer(modifier = Modifier.height(12.sdp))
                                    AppText.BodyMedium(
                                        text = "No Ayah Bookmarks Yet",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.sdp))
                                    AppText.Caption(
                                        text = "Tap the bookmark icon on any Ayah card to resume reading later",
                                        color = TextSecondary
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 32.sdp),
                                verticalArrangement = Arrangement.spacedBy(10.sdp)
                            ) {
                                items(state.bookmarks) { bm ->
                                    com.ne.muslimprayers.qiblafinder.ui.quran.components.QuranBookmarkCard(
                                        bookmark = bm,
                                        onClick = {
                                            val targetSurah = state.surahs.find { it.number == bm.surahNumber }
                                                ?: Surah(
                                                    number = bm.surahNumber,
                                                    name = bm.surahName,
                                                    arabicName = "",
                                                    englishMeaning = "",
                                                    totalVerses = 0,
                                                    revelationType = ""
                                                )
                                            viewModel.selectSurah(targetSurah)
                                        },
                                        onDelete = { viewModel.removeBookmark(bm) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
