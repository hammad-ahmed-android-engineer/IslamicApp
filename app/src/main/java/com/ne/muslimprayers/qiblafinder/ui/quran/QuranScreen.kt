package com.ne.muslimprayers.qiblafinder.ui.quran

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicSearchBar
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import com.ne.muslimprayers.qiblafinder.ui.quran.components.JuzListItemCard
import com.ne.muslimprayers.qiblafinder.ui.quran.components.QuranSettingsSheetContent
import com.ne.muslimprayers.qiblafinder.ui.quran.components.SelectReciterDownloadDialog
import com.ne.muslimprayers.qiblafinder.ui.quran.components.SurahDetailReader
import com.ne.muslimprayers.qiblafinder.ui.quran.components.SurahListItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    viewModel: QuranViewModel = hiltViewModel(),
    onFullScreenChanged: (Boolean) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isEffectiveFullScreen = state.selectedSurah != null && state.isFullScreenMode
    LaunchedEffect(isEffectiveFullScreen) {
        onFullScreenChanged(isEffectiveFullScreen)
    }
    DisposableEffect(Unit) {
        onDispose {
            onFullScreenChanged(false)
        }
    }

    // Qari Selection Before Downloading Dialog
    if (state.showReciterDownloadDialog) {
        SelectReciterDownloadDialog(
            targetTitle = state.pendingDownloadTitle,
            initialReciterId = state.selectedReciter,
            onDismiss = viewModel::dismissReciterDownloadDialog,
            onConfirmDownload = viewModel::confirmDownloadWithReciter
        )
    }

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
                onDownloadSurah = { viewModel.requestDownloadCurrent() },
                onDeleteSurahAudio = { state.selectedSurah?.let { viewModel.deleteSurahAudio(it.number) } },
                onClearAllAudio = { viewModel.refreshDownloadedSets() },
                onToggleAutoPlay = viewModel::toggleAutoPlay,
                onToggleAutoScroll = viewModel::toggleAutoScroll,
                onSetReciter = viewModel::setReciter,
                onAdjustFontSize = viewModel::adjustFontSize,
                onSetArabicFontSize = viewModel::setArabicFontSize,
                onSetTranslationFontSize = viewModel::setTranslationFontSize,
                onDismiss = { viewModel.toggleSettingsSheet(false) }
            )
        }
    }

    var selectedTafseerAyah by remember { mutableStateOf<Ayah?>(null) }
    val currentTafseerAyah = selectedTafseerAyah

    // Ayah Tafseer Dialog
    if (currentTafseerAyah != null && state.selectedSurah != null) {
        val effectiveSurahNum = if (currentTafseerAyah.surahNumber > 0) {
            currentTafseerAyah.surahNumber
        } else {
            state.selectedSurah!!.number
        }
        val effectiveSurahName = state.surahs.find { it.number == effectiveSurahNum }?.name
            ?: state.selectedSurah!!.name

        com.ne.muslimprayers.qiblafinder.ui.quran.components.AyahTafseerDialog(
            surahNumber = effectiveSurahNum,
            surahName = effectiveSurahName,
            ayah = currentTafseerAyah,
            quranApiService = viewModel.quranApiService,
            onDismiss = { selectedTafseerAyah = null }
        )
    }

    if (state.selectedSurah != null) {
        BackHandler {
            viewModel.clearSelectedSurah()
        }

        val bookmarkedAyahKeys = remember(state.bookmarks) {
            state.bookmarks.map { "${it.surahNumber}_${it.ayahNumber}" }.toSet()
        }

        SurahDetailReader(
            surah = state.selectedSurah!!,
            verses = state.verses,
            allSurahs = state.surahs,
            isJuzMode = state.selectedJuz != null,
            juzNumber = state.selectedJuz?.number,
            selectedJuz = state.selectedJuz,
            isLoading = state.isLoadingVerses,
            errorMessage = state.versesError,
            currentlyPlayingAyah = state.currentlyPlayingAyah,
            arabicFontSize = state.arabicFontSize,
            translationFontSize = state.translationFontSize,
            showTranslation = state.showTranslation,
            showTransliteration = state.showTransliteration,
            showUrduTranslation = state.showUrduTranslation,
            selectedQuranFont = state.selectedQuranFont,
            useIndoPakScript = state.useIndoPakScript,
            isColoredQuran = state.isColoredTajweedQuran,
            isDownloaded = state.isCurrentSurahDownloaded,
            downloadStatus = state.downloadStatus,
            bookmarkedAyahKeys = bookmarkedAyahKeys,
            isLandscape = isLandscape,
            isFullScreen = state.isFullScreenMode,
            isAutoScrollEnabled = state.isAutoScrollEnabled,
            onToggleFullScreen = viewModel::toggleFullScreen,
            onToggleAutoScroll = viewModel::toggleAutoScroll,
            onToggleColoredQuran = { viewModel.toggleColoredTajweedQuran(!state.isColoredTajweedQuran) },
            onDownloadSurah = { viewModel.requestDownloadCurrent() },
            onDeleteSurahAudio = { state.selectedSurah?.let { viewModel.deleteSurahAudio(it.number) } },
            onBackClick = { viewModel.clearSelectedSurah() },
            onPlayAyah = { viewModel.playAyahAudio(it) },
            onAdjustFontSize = { viewModel.adjustFontSize(it) },
            onOpenSettings = { viewModel.toggleSettingsSheet(true) },
            onBookmarkAyah = { ayah ->
                val targetSurahNum = if (ayah.surahNumber > 0) ayah.surahNumber else state.selectedSurah!!.number
                val targetSurah = state.surahs.find { it.number == targetSurahNum } ?: state.selectedSurah!!
                viewModel.toggleBookmarkAyah(targetSurah, ayah)
            },
            onRetry = viewModel::retryLoadVerses,
            onOpenTafseer = { ayah -> selectedTafseerAyah = ayah },
            onToggleTranslation = viewModel::toggleTranslation,
            onCopyAyah = { ayah ->
                val fontConfig = ArabicFontManager.getTypographyConfig(state.selectedQuranFont)
                val isIndoPakEffective = state.useIndoPakScript || fontConfig.prefersIndoPakScript
                val rawArabic = if (isIndoPakEffective && ayah.indopakText.isNotBlank()) ayah.indopakText else ayah.arabicText
                val ayahSurahNum = if (ayah.surahNumber > 0) ayah.surahNumber else state.selectedSurah!!.number
                val ayahSurahName = state.surahs.find { it.number == ayahSurahNum }?.name ?: state.selectedSurah!!.name
                val formattedArabic = com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter.getPlainTextWithEndSign(
                    rawArabic,
                    ayah.numberInSurah,
                    ayahSurahNum
                )
                val urduPart = if (state.showUrduTranslation && ayah.urduText.isNotBlank()) "\n\n${ayah.urduText}" else ""
                val engPart = if (state.showTranslation && ayah.englishText.isNotBlank()) "\n\n${ayah.englishText}" else ""
                val text = "$formattedArabic$urduPart$engPart\n‹ $ayahSurahNum.${ayah.numberInSurah} › Surah $ayahSurahName"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(
                    context,
                    "Ayah",
                    text,
                    context.getString(R.string.ayah_copied)
                )
            },
            onShareAyah = { ayah ->
                val fontConfig = ArabicFontManager.getTypographyConfig(state.selectedQuranFont)
                val isIndoPakEffective = state.useIndoPakScript || fontConfig.prefersIndoPakScript
                val rawArabic = if (isIndoPakEffective && ayah.indopakText.isNotBlank()) ayah.indopakText else ayah.arabicText
                val ayahSurahNum = if (ayah.surahNumber > 0) ayah.surahNumber else state.selectedSurah!!.number
                val ayahSurahName = state.surahs.find { it.number == ayahSurahNum }?.name ?: state.selectedSurah!!.name
                val formattedArabic = com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter.getPlainTextWithEndSign(
                    rawArabic,
                    ayah.numberInSurah,
                    ayahSurahNum
                )
                val urduPart = if (state.showUrduTranslation && ayah.urduText.isNotBlank()) "\n\n${ayah.urduText}" else ""
                val engPart = if (state.showTranslation && ayah.englishText.isNotBlank()) "\n\n${ayah.englishText}" else ""
                val text = "$formattedArabic$urduPart$engPart\n\n— Holy Quran ‹ $ayahSurahNum.${ayah.numberInSurah} › (Surah $ayahSurahName)"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(
                    context,
                    text,
                    context.getString(R.string.share_ayah_via)
                )
            }
        )
    } else {
        Scaffold(
            topBar = {
                IslamicTopAppBar(
                    title = stringResource(R.string.quran_title),
                    actions = {
                        IconButton(onClick = { viewModel.toggleSettingsSheet(true) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.quran_settings),
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconMedium)
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
                    .padding(horizontal = AppDimensions.paddingMedium)
            ) {
                // Section Tabs: Surahs, Juz, Favorites, Bookmarks
                ScrollableTabRow(
                    selectedTabIndex = state.selectedSectionTab,
                    containerColor = WarmParchment,
                    contentColor = MedinaSage,
                    edgePadding = AppDimensions.paddingZero,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedSectionTab]),
                            color = MedinaSage,
                            height = AppDimensions.borderWidthThick
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = state.selectedSectionTab == 0,
                        onClick = { viewModel.selectSectionTab(0) },
                        modifier = Modifier.height(AppDimensions.buttonSmallHeight),
                        text = {
                            AppText.BodyMedium(
                                text = stringResource(R.string.quran_tab_surahs, state.surahs.size),
                                fontWeight = if (state.selectedSectionTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 0) MedinaSage else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedSectionTab == 1,
                        onClick = { viewModel.selectSectionTab(1) },
                        modifier = Modifier.height(AppDimensions.buttonSmallHeight),
                        text = {
                            AppText.BodyMedium(
                                text = stringResource(R.string.quran_tab_juz),
                                fontWeight = if (state.selectedSectionTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 1) MedinaSage else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedSectionTab == 2,
                        onClick = { viewModel.selectSectionTab(2) },
                        modifier = Modifier.height(AppDimensions.buttonSmallHeight),
                        text = {
                            AppText.BodyMedium(
                                text = stringResource(R.string.quran_tab_favorites, state.favoriteSurahNumbers.size),
                                fontWeight = if (state.selectedSectionTab == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 2) MedinaSage else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedSectionTab == 3,
                        onClick = { viewModel.selectSectionTab(3) },
                        modifier = Modifier.height(AppDimensions.buttonSmallHeight),
                        text = {
                            AppText.BodyMedium(
                                text = stringResource(R.string.quran_tab_bookmarks, state.bookmarks.size),
                                fontWeight = if (state.selectedSectionTab == 3) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedSectionTab == 3) MedinaSage else TextSecondary
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                when (state.selectedSectionTab) {
                    0 -> {
                        // All Surahs Tab with Compact Search Bar
                        IslamicSearchBar(
                            query = state.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            placeholder = stringResource(R.string.quran_search_placeholder),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                        if (state.isLoadingSurahs && state.surahs.isEmpty()) {
                            LoadingState(message = stringResource(R.string.quran_loading_surahs))
                        } else if (state.surahsError != null && state.surahs.isEmpty()) {
                            ErrorState(
                                message = state.surahsError ?: stringResource(R.string.quran_failed_load_surahs),
                                onRetry = viewModel::loadSurahs
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.filteredSurahs, key = { it.number }) { surah ->
                                    val isSurahDownloaded = state.downloadedSurahNumbers.contains(surah.number)
                                    val isFav = state.favoriteSurahNumbers.contains(surah.number)
                                    SurahListItemCard(
                                        surah = surah,
                                        isFavorite = isFav,
                                        onFavoriteClick = { viewModel.toggleFavorite(surah.number) },
                                        isDownloaded = isSurahDownloaded,
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.requestDownloadSurah(surah) },
                                        onClick = { viewModel.selectSurah(surah) }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Juz Tab
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                        ) {
                            items(state.juzList, key = { it.number }) { juz ->
                                val isJuzDownloaded = state.downloadedJuzNumbers.contains(juz.number)
                                JuzListItemCard(
                                    juz = juz,
                                    isDownloaded = isJuzDownloaded,
                                    isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "juz_${juz.number}",
                                    onDownloadClick = { viewModel.requestDownloadJuz(juz) },
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
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(AppDimensions.paddingExtraLarge),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = DesertOchre,
                                        modifier = Modifier.size(AppDimensions.iconHuge)
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                                    AppText.BodyMedium(
                                        text = stringResource(R.string.quran_no_favorites_title),
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                                    AppText.Caption(
                                        text = stringResource(R.string.quran_no_favorites_desc),
                                        color = TextSecondary
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(favoriteSurahs, key = { it.number }) { surah ->
                                    val isSurahDownloaded = state.downloadedSurahNumbers.contains(surah.number)
                                    SurahListItemCard(
                                        surah = surah,
                                        isFavorite = true,
                                        onFavoriteClick = { viewModel.toggleFavorite(surah.number) },
                                        isDownloaded = isSurahDownloaded,
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.requestDownloadSurah(surah) },
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
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(AppDimensions.paddingExtraLarge),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = MedinaSage,
                                        modifier = Modifier.size(AppDimensions.iconHuge)
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                                    AppText.BodyMedium(
                                        text = stringResource(R.string.quran_no_bookmarks_title),
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                                    AppText.Caption(
                                        text = stringResource(R.string.quran_no_bookmarks_desc),
                                        color = TextSecondary
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.bookmarks, key = { "${it.surahNumber}_${it.ayahNumber}" }) { bm ->
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
