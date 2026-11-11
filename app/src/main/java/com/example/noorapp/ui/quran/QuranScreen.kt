package com.example.noorapp.ui.quran

import android.content.res.Configuration
import android.widget.Toast
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppBottomSheet
import com.example.noorapp.core.designsystem.AppDialog
import com.example.noorapp.core.designsystem.AppErrorState
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppLoadingState
import com.example.noorapp.core.designsystem.AppScrollableTabRow
import com.example.noorapp.core.designsystem.AppSearchBar
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppTopAppBar
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.AppUtils
import com.example.noorapp.domain.model.Surah
import com.example.noorapp.ui.quran.components.AyahTafseerContent
import com.example.noorapp.ui.quran.components.JuzListItemCard
import com.example.noorapp.ui.quran.components.QuranBookmarkCard
import com.example.noorapp.ui.quran.components.QuranSettingsSheetContent
import com.example.noorapp.ui.quran.components.SelectReciterDownloadContent
import com.example.noorapp.ui.quran.components.SurahDetailReader
import com.example.noorapp.ui.quran.components.SurahListItemCard
import com.example.noorapp.ui.quran.components.TajweedWaqfGuideContent
import com.example.noorapp.ui.quran.util.QuranTextFormatter

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
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is QuranUiEvent.ShowToast -> {
                    val message = if (event.formatArgs.isEmpty()) {
                        context.getString(event.messageRes)
                    } else {
                        context.getString(event.messageRes, *event.formatArgs.toTypedArray())
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            onFullScreenChanged(false)
        }
    }

    // Qari Selection Before Downloading Dialog (hosted directly via common AppDialog)
    if (state.showReciterDownloadDialog) {
        val targetTitle = when {
            state.pendingDownloadJuz != null -> {
                val j = state.pendingDownloadJuz!!
                stringResource(R.string.quran_para_juz_title_format, j.number, j.englishName)
            }
            state.pendingDownloadSurah != null -> {
                stringResource(R.string.quran_surah_name_format, state.pendingDownloadSurah!!.name)
            }
            else -> ""
        }
        AppDialog(
            onDismissRequest = viewModel::dismissReciterDownloadDialog,
            title = stringResource(R.string.quran_select_qari_download_title),
            subtitle = stringResource(R.string.quran_select_qari_download_subtitle, targetTitle),
            headerIcon = Icons.Default.RecordVoiceOver,
            dismissButtonText = stringResource(R.string.cancel),
            onDismissClick = viewModel::dismissReciterDownloadDialog,
            confirmButtonText = stringResource(R.string.quran_download_start),
            confirmButtonIcon = Icons.Default.Download,
            confirmButtonWeight = 1.3f,
            onConfirmClick = { viewModel.confirmDownloadWithReciter(state.pendingDownloadReciterId) },
            backgroundColor = PureAlabaster,
            cornerRadius = AppDimensions.cornerExtraLarge,
            maxContentHeight = if (isLandscape) AppDimensions.dialogContentMaxHeightCompact else AppDimensions.dialogContentMaxHeightExpanded,
            scrollable = true
        ) {
            SelectReciterDownloadContent(
                selectedReciterId = state.pendingDownloadReciterId,
                onSelectReciter = viewModel::selectPendingDownloadReciter
            )
        }
    }

    // Settings Bottom Sheet (hosted directly via common AppBottomSheet)
    if (state.showSettingsSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        AppBottomSheet(
            onDismissRequest = { viewModel.toggleSettingsSheet(false) },
            title = stringResource(R.string.quran_preferences_title),
            subtitle = stringResource(R.string.quran_preferences_subtitle),
            showCloseButton = true,
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
                onOpenWaqfGuide = { viewModel.toggleWaqfGuideDialog(true) },
                onDownloadSurah = { viewModel.requestDownloadCurrent() },
                onDeleteSurahAudio = { state.selectedSurah?.let { viewModel.deleteSurahAudio(it.number) } },
                onClearAllAudio = viewModel::clearAllAudio,
                onToggleAutoPlay = viewModel::toggleAutoPlay,
                onToggleAutoScroll = viewModel::toggleAutoScroll,
                onSetReciter = viewModel::setReciter,
                onAdjustFontSize = viewModel::adjustFontSize,
                onSetArabicFontSize = viewModel::setArabicFontSize,
                onSetTranslationFontSize = viewModel::setTranslationFontSize
            )
        }
    }

    // Tajweed & Waqf Pause Marks Guide Dialog (hosted directly via common AppDialog)
    if (state.showWaqfGuideDialog) {
        AppDialog(
            onDismissRequest = { viewModel.toggleWaqfGuideDialog(false) },
            title = stringResource(R.string.quran_waqf_guide_title),
            subtitle = stringResource(R.string.quran_waqf_guide_arabic_subtitle),
            headerIcon = Icons.AutoMirrored.Filled.MenuBook,
            description = stringResource(R.string.quran_waqf_guide_description),
            showCloseButton = true,
            confirmButtonText = stringResource(R.string.quran_understood_btn),
            onConfirmClick = { viewModel.toggleWaqfGuideDialog(false) },
            backgroundColor = WarmParchment,
            borderColor = DividerColor,
            cornerRadius = AppDimensions.cornerDialog,
            tonalElevation = AppDimensions.elevationHigh,
            usePlatformDefaultWidth = false,
            modifier = Modifier.fillMaxWidth(if (isLandscape) 0.85f else 0.94f)
        ) {
            TajweedWaqfGuideContent(isLandscape = isLandscape)
        }
    }

    // Ayah Tafseer Dialog (state preserved in ViewModel across rotation)
    val currentTafseerAyah = state.selectedTafseerAyah
    if (currentTafseerAyah != null && state.selectedSurah != null) {
        val effectiveSurahNum = if (currentTafseerAyah.surahNumber > 0) {
            currentTafseerAyah.surahNumber
        } else {
            state.selectedSurah!!.number
        }
        val effectiveSurahName = state.surahs.find { it.number == effectiveSurahNum }?.name
            ?: state.selectedSurah!!.name

        AppDialog(
            onDismissRequest = viewModel::dismissTafseer,
            title = stringResource(R.string.quran_ayah_tafseer_title),
            titleBadge = stringResource(
                R.string.quran_ayah_badge_format,
                effectiveSurahNum,
                currentTafseerAyah.numberInSurah
            ),
            subtitle = stringResource(R.string.quran_surah_name_format, effectiveSurahName),
            headerIcon = Icons.AutoMirrored.Filled.MenuBook,
            showCloseButton = true,
            confirmButtonText = stringResource(R.string.quran_close_tafseer_btn),
            onConfirmClick = viewModel::dismissTafseer,
            backgroundColor = WarmParchment,
            borderColor = DividerColor,
            cornerRadius = AppDimensions.cornerDialog,
            tonalElevation = AppDimensions.elevationHigh,
            usePlatformDefaultWidth = false,
            modifier = Modifier.fillMaxWidth(if (isLandscape) 0.82f else 0.94f)
        ) {
            AyahTafseerContent(
                selectedTafseerId = state.selectedTafseerId,
                isLoading = state.isLoadingTafseer,
                tafseerContent = state.tafseerContent,
                errorMessage = state.tafseerError,
                isLandscape = isLandscape,
                onSelectTafseer = viewModel::selectTafseerSource,
                onRetry = viewModel::retryTafseer
            )
        }
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
            isContinuousScrolling = state.isContinuousScrolling,
            scrollSpeed = state.scrollSpeed,
            onToggleContinuousScrolling = viewModel::toggleContinuousScrolling,
            onSetScrollSpeed = viewModel::setScrollSpeed,
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
            onOpenTafseer = viewModel::openTafseer,
            onToggleTranslation = viewModel::toggleTranslation,
            onCopyAyah = { ayah ->
                val fontConfig = ArabicFontManager.getTypographyConfig(state.selectedQuranFont)
                val isIndoPakEffective = state.useIndoPakScript || fontConfig.prefersIndoPakScript
                val rawArabic = if (isIndoPakEffective && ayah.indopakText.isNotBlank()) ayah.indopakText else ayah.arabicText
                val ayahSurahNum = if (ayah.surahNumber > 0) ayah.surahNumber else state.selectedSurah!!.number
                val ayahSurahName = state.surahs.find { it.number == ayahSurahNum }?.name ?: state.selectedSurah!!.name
                val formattedArabic = QuranTextFormatter.getPlainTextWithEndSign(
                    rawArabic,
                    ayah.numberInSurah,
                    ayahSurahNum
                )
                val urduPart = if (state.showUrduTranslation && ayah.urduText.isNotBlank()) "\n\n${ayah.urduText}" else ""
                val engPart = if (state.showTranslation && ayah.englishText.isNotBlank()) "\n\n${ayah.englishText}" else ""
                val text = context.getString(
                    R.string.quran_copy_ayah_format,
                    "$formattedArabic$urduPart$engPart",
                    ayahSurahNum,
                    ayah.numberInSurah,
                    ayahSurahName
                )
                AppUtils.copyToClipboard(
                    context,
                    context.getString(R.string.quran_ayah_clipboard_label),
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
                val formattedArabic = QuranTextFormatter.getPlainTextWithEndSign(
                    rawArabic,
                    ayah.numberInSurah,
                    ayahSurahNum
                )
                val urduPart = if (state.showUrduTranslation && ayah.urduText.isNotBlank()) "\n\n${ayah.urduText}" else ""
                val engPart = if (state.showTranslation && ayah.englishText.isNotBlank()) "\n\n${ayah.englishText}" else ""
                val text = context.getString(
                    R.string.quran_share_ayah_format,
                    "$formattedArabic$urduPart$engPart",
                    ayahSurahNum,
                    ayah.numberInSurah,
                    ayahSurahName
                )
                AppUtils.shareText(
                    context,
                    text,
                    context.getString(R.string.share_ayah_via)
                )
            }
        )
    } else {
        Scaffold(
            topBar = {
                AppTopAppBar(
                    title = stringResource(R.string.quran_title),
                    actions = {
                        AppIconButton(onClick = { viewModel.toggleSettingsSheet(true) }) {
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
                AppScrollableTabRow(
                    selectedTabIndex = state.selectedSectionTab,
                    containerColor = WarmParchment,
                    contentColor = MedinaSage,
                    edgePadding = AppDimensions.paddingZero
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
                        AppSearchBar(
                            query = state.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            placeholder = stringResource(R.string.quran_search_placeholder),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                        if (state.isLoadingSurahs && state.surahs.isEmpty()) {
                            AppLoadingState(message = stringResource(R.string.quran_loading_surahs))
                        } else if (state.surahsError != null && state.surahs.isEmpty()) {
                            AppErrorState(
                                message = state.surahsError ?: stringResource(R.string.quran_failed_load_surahs),
                                onRetry = viewModel::loadSurahs
                            )
                        } else if (isLandscape) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.filteredSurahs, key = { it.number }) { surah ->
                                    SurahListItemCard(
                                        surah = surah,
                                        isFavorite = state.favoriteSurahNumbers.contains(surah.number),
                                        onFavoriteClick = { viewModel.toggleFavorite(surah.number) },
                                        isDownloaded = state.downloadedSurahNumbers.contains(surah.number),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.requestDownloadSurah(surah) },
                                        onClick = { viewModel.selectSurah(surah) }
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.filteredSurahs, key = { it.number }) { surah ->
                                    SurahListItemCard(
                                        surah = surah,
                                        isFavorite = state.favoriteSurahNumbers.contains(surah.number),
                                        onFavoriteClick = { viewModel.toggleFavorite(surah.number) },
                                        isDownloaded = state.downloadedSurahNumbers.contains(surah.number),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.requestDownloadSurah(surah) },
                                        onClick = { viewModel.selectSurah(surah) }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        if (isLandscape) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.juzList, key = { it.number }) { juz ->
                                    JuzListItemCard(
                                        juz = juz,
                                        isDownloaded = state.downloadedJuzNumbers.contains(juz.number),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "juz_${juz.number}",
                                        onDownloadClick = { viewModel.requestDownloadJuz(juz) },
                                        onClick = { viewModel.selectJuz(juz) }
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.juzList, key = { it.number }) { juz ->
                                    JuzListItemCard(
                                        juz = juz,
                                        isDownloaded = state.downloadedJuzNumbers.contains(juz.number),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "juz_${juz.number}",
                                        onDownloadClick = { viewModel.requestDownloadJuz(juz) },
                                        onClick = { viewModel.selectJuz(juz) }
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
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
                        } else if (isLandscape) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(favoriteSurahs, key = { it.number }) { surah ->
                                    SurahListItemCard(
                                        surah = surah,
                                        isFavorite = true,
                                        onFavoriteClick = { viewModel.toggleFavorite(surah.number) },
                                        isDownloaded = state.downloadedSurahNumbers.contains(surah.number),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.requestDownloadSurah(surah) },
                                        onClick = { viewModel.selectSurah(surah) }
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(favoriteSurahs, key = { it.number }) { surah ->
                                    SurahListItemCard(
                                        surah = surah,
                                        isFavorite = true,
                                        onFavoriteClick = { viewModel.toggleFavorite(surah.number) },
                                        isDownloaded = state.downloadedSurahNumbers.contains(surah.number),
                                        isDownloading = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}",
                                        onDownloadClick = { viewModel.requestDownloadSurah(surah) },
                                        onClick = { viewModel.selectSurah(surah) }
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
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
                        } else if (isLandscape) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.bookmarks, key = { "${it.surahNumber}_${it.ayahNumber}" }) { bm ->
                                    QuranBookmarkCard(
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
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                items(state.bookmarks, key = { "${it.surahNumber}_${it.ayahNumber}" }) { bm ->
                                    QuranBookmarkCard(
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
