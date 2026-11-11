package com.example.noorapp.ui.quran.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCircularProgress
import com.example.noorapp.core.designsystem.AppDivider
import com.example.noorapp.core.designsystem.AppErrorState
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppLinearProgress
import com.example.noorapp.core.designsystem.AppLoadingState
import com.example.noorapp.core.designsystem.AppSelectableCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppVerticalDivider
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.HeroBannerGradient
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.domain.model.Ayah
import com.example.noorapp.domain.model.QuranDownloadStatus
import com.example.noorapp.domain.model.QuranStaticData
import com.example.noorapp.domain.model.Surah
import com.example.noorapp.ui.quran.JuzInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun SurahDetailReader(
    surah: Surah,
    verses: List<Ayah>,
    allSurahs: List<Surah> = QuranStaticData.ALL_SURAHS,
    isLoading: Boolean,
    errorMessage: String?,
    currentlyPlayingAyah: Int?,
    arabicFontSize: Float,
    translationFontSize: Float = 15f,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    showUrduTranslation: Boolean = true,
    selectedQuranFont: String = "muhammadi",
    useIndoPakScript: Boolean = true,
    isColoredQuran: Boolean = true,
    isDownloaded: Boolean = false,
    downloadStatus: QuranDownloadStatus = QuranDownloadStatus(),
    bookmarkedAyahKeys: Set<String> = emptySet(),
    isJuzMode: Boolean = false,
    juzNumber: Int? = null,
    selectedJuz: JuzInfo? = null,
    isLandscape: Boolean,
    isFullScreen: Boolean = false,
    isAutoScrollEnabled: Boolean = true,
    isContinuousScrolling: Boolean = false,
    scrollSpeed: Float = 1.0f,
    onToggleContinuousScrolling: () -> Unit = {},
    onSetScrollSpeed: (Float) -> Unit = {},
    onToggleFullScreen: () -> Unit = {},
    onToggleAutoScroll: () -> Unit = {},
    onToggleColoredQuran: () -> Unit = {},
    onDownloadSurah: () -> Unit = {},
    onDeleteSurahAudio: () -> Unit = {},
    onBackClick: () -> Unit,
    onPlayAyah: (Ayah) -> Unit,
    onAdjustFontSize: (Float) -> Unit,
    onOpenSettings: () -> Unit,
    onBookmarkAyah: (Ayah) -> Unit,
    onRetry: () -> Unit,
    onCopyAyah: (Ayah) -> Unit,
    onShareAyah: (Ayah) -> Unit,
    onOpenTafseer: (Ayah) -> Unit = {},
    onToggleTranslation: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    // Smooth continuous auto-scrolling that seamlessly pauses during manual user touch/drag/fling
    // and automatically resumes from the new page/scroll position once the user releases.
    LaunchedEffect(isContinuousScrolling, scrollSpeed) {
        if (isContinuousScrolling) {
            while (isActive) {
                try {
                    listState.scrollBy(1.5f * scrollSpeed)
                } catch (e: CancellationException) {
                    if (!isActive) throw e
                    while (isActive && listState.isScrollInProgress) {
                        delay(50L)
                    }
                    delay(120L)
                }
                delay(16L)
            }
        }
    }

    // Auto-Scroll to the playing Ayah during recitation when continuous scroll is off
    LaunchedEffect(currentlyPlayingAyah, isAutoScrollEnabled, isContinuousScrolling) {
        if (isAutoScrollEnabled && !isContinuousScrolling && currentlyPlayingAyah != null && verses.isNotEmpty()) {
            val targetIndex = verses.indexOfFirst {
                it.globalAyahNumber == currentlyPlayingAyah || it.numberInSurah == currentlyPlayingAyah
            }
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    val isDownloadingThis = downloadStatus.isDownloading &&
        (downloadStatus.targetKey == "surah_${surah.number}" ||
            (juzNumber != null && downloadStatus.targetKey == "juz_$juzNumber"))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmParchment)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Reader Header TopBar
            AnimatedVisibility(
                visible = !isFullScreen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AppCard(
                    backgroundColor = WarmParchment,
                    borderColor = Color.Transparent,
                    borderWidth = AppDimensions.paddingZero,
                    cornerRadius = AppDimensions.paddingZero,
                    contentPadding = AppDimensions.paddingZero,
                    elevation = AppDimensions.elevationLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(
                                    horizontal = AppDimensions.paddingSmall,
                                    vertical = AppDimensions.paddingTiny
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIconButton(
                                onClick = onBackClick,
                                modifier = Modifier.size(AppDimensions.iconButtonCompact)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back),
                                    tint = TextPrimary,
                                    modifier = Modifier.size(AppDimensions.iconMedium)
                                )
                            }

                            Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))

                            val (headerTitle, headerSubtitle) = resolveReaderHeaderText(
                                isJuzMode = isJuzMode,
                                surah = surah,
                                verses = verses,
                                selectedJuz = selectedJuz,
                                allSurahs = allSurahs
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                AppText.TitleMedium(
                                    text = headerTitle,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                AppText.BodySmall(
                                    text = headerSubtitle,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMicro)
                            ) {
                                if (isDownloadingThis) {
                                    Box(
                                        modifier = Modifier.size(AppDimensions.iconButtonCompact),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppCircularProgress(
                                            modifier = Modifier.size(AppDimensions.iconSmall),
                                            color = MedinaSage,
                                            strokeWidth = AppDimensions.borderWidthThick
                                        )
                                    }
                                } else if (!isDownloaded) {
                                    AppIconButton(
                                        onClick = onDownloadSurah,
                                        modifier = Modifier.size(AppDimensions.iconButtonCompact)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = stringResource(R.string.quran_download_audio_desc),
                                            tint = MedinaSage,
                                            modifier = Modifier.size(AppDimensions.iconMedium)
                                        )
                                    }
                                }

                                // Full-Screen Toggle
                                AppIconButton(
                                    onClick = onToggleFullScreen,
                                    modifier = Modifier.size(AppDimensions.iconButtonCompact)
                                ) {
                                    Icon(
                                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = stringResource(R.string.quran_full_screen),
                                        tint = MedinaSage,
                                        modifier = Modifier.size(AppDimensions.iconMedium)
                                    )
                                }

                                // Quran Settings Button
                                AppIconButton(
                                    onClick = onOpenSettings,
                                    modifier = Modifier.size(AppDimensions.iconButtonCompact)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = stringResource(R.string.quran_settings),
                                        tint = MedinaSage,
                                        modifier = Modifier.size(AppDimensions.iconMedium)
                                    )
                                }
                            }
                        }

                        if (isDownloadingThis) {
                            AppLinearProgress(
                                progress = { downloadStatus.progressPercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(AppDimensions.progressBarHeight),
                                color = MedinaSage,
                                trackColor = MedinaSageLight
                            )
                        }
                    }
                }
            }

            if (isLoading && verses.isEmpty()) {
                AppLoadingState(message = stringResource(R.string.quran_loading_verses))
            } else if (errorMessage != null && verses.isEmpty()) {
                AppErrorState(
                    message = errorMessage.ifBlank {
                        if (isJuzMode) stringResource(R.string.quran_failed_load_juz_verses)
                        else stringResource(R.string.quran_failed_load_verses)
                    },
                    onRetry = onRetry
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = if (isLandscape) AppDimensions.paddingExtraLarge else AppDimensions.paddingMediumSmall,
                        end = if (isLandscape) AppDimensions.paddingExtraLarge else AppDimensions.paddingMediumSmall,
                        top = AppDimensions.paddingSmall,
                        bottom = AppDimensions.readerBottomPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                ) {
                    itemsIndexed(
                        items = verses,
                        key = { index, ayah -> "${ayah.surahNumber}_${ayah.numberInSurah}_$index" }
                    ) { index, ayah ->
                        val effectiveSurahNum = if (ayah.surahNumber > 0) ayah.surahNumber else surah.number

                        // Show header ONLY when a new Surah starts (never show Juz start header)
                        val isNewSurahStart = ayah.numberInSurah == 1 || (!isJuzMode && index == 0)

                        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)) {
                            if (isNewSurahStart) {
                                val surahMeta = remember(effectiveSurahNum, allSurahs) {
                                    allSurahs.find { it.number == effectiveSurahNum }
                                        ?: QuranStaticData.ALL_SURAHS.find { it.number == effectiveSurahNum }
                                        ?: surah
                                }
                                SurahStartHeaderBanner(
                                    surah = surahMeta,
                                    selectedQuranFont = selectedQuranFont,
                                    showTranslation = showTranslation || showUrduTranslation,
                                    onToggleTranslation = onToggleTranslation,
                                    onOpenTafseer = { onOpenTafseer(ayah) }
                                )
                            }

                            // Verse Card
                            val isPlaying = currentlyPlayingAyah != null &&
                                (currentlyPlayingAyah == ayah.globalAyahNumber ||
                                    (!isJuzMode && currentlyPlayingAyah == ayah.numberInSurah))
                            val bookmarkKey = "${effectiveSurahNum}_${ayah.numberInSurah}"
                            val isBookmarked = bookmarkedAyahKeys.contains(bookmarkKey)

                            AyahCardItem(
                                ayah = ayah,
                                surahNumber = effectiveSurahNum,
                                arabicFontSize = arabicFontSize,
                                translationFontSize = translationFontSize,
                                showTranslation = showTranslation,
                                showTransliteration = showTransliteration,
                                showUrduTranslation = showUrduTranslation,
                                selectedQuranFont = selectedQuranFont,
                                useIndoPakScript = useIndoPakScript,
                                isColoredQuran = isColoredQuran,
                                isPlaying = isPlaying,
                                isBookmarked = isBookmarked,
                                onPlay = { onPlayAyah(ayah) },
                                onBookmark = { onBookmarkAyah(ayah) },
                                onCopy = { onCopyAyah(ayah) },
                                onShare = { onShareAyah(ayah) },
                                onOpenTafseer = { onOpenTafseer(ayah) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Floating Controls: Screen Resize Icon + Auto-Scroll Bar (up to 4x)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isFullScreen) AppDimensions.paddingMedium else AppDimensions.paddingMediumLarge)
                .padding(horizontal = AppDimensions.paddingMediumSmall),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
        ) {
            if (isFullScreen) {
                AppCard(
                    onClick = onToggleFullScreen,
                    cornerRadius = AppDimensions.avatarSize,
                    backgroundColor = MedinaSage,
                    borderColor = DesertOchre.copy(alpha = 0.5f),
                    borderWidth = AppDimensions.borderWidthThin,
                    contentPadding = AppDimensions.paddingZero,
                    elevation = AppDimensions.elevationHigh,
                    modifier = Modifier.size(AppDimensions.avatarSize)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = stringResource(R.string.quran_exit_fullscreen_desc),
                            tint = PureAlabaster,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }
            }

            AppCard(
                cornerRadius = AppDimensions.cornerDialog,
                backgroundColor = PureAlabaster.copy(alpha = 0.96f),
                borderColor = MedinaSage.copy(alpha = 0.25f),
                borderWidth = AppDimensions.borderWidthThin,
                contentPadding = AppDimensions.paddingZero,
                elevation = AppDimensions.elevationHigh
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = AppDimensions.paddingRegular,
                            vertical = AppDimensions.paddingExtraSmall
                        )
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                ) {
                    AppSelectableCard(
                        isSelected = isContinuousScrolling,
                        onClick = onToggleContinuousScrolling,
                        selectedBackgroundColor = MedinaSage,
                        unselectedBackgroundColor = MedinaSageLight,
                        selectedBorderColor = MedinaSage,
                        unselectedBorderColor = MedinaSageLight,
                        borderWidth = AppDimensions.paddingZero,
                        cornerRadius = AppDimensions.cornerLarge,
                        contentPadding = AppDimensions.paddingZero,
                        elevation = AppDimensions.elevationNone
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = AppDimensions.paddingRegular,
                                vertical = AppDimensions.paddingExtraSmall
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                        ) {
                            Icon(
                                imageVector = if (isContinuousScrolling) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isContinuousScrolling) {
                                    stringResource(R.string.quran_pause_autoscroll_desc)
                                } else {
                                    stringResource(R.string.quran_start_autoscroll_desc)
                                },
                                tint = if (isContinuousScrolling) PureAlabaster else MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                            AppText.Caption(
                                text = if (isContinuousScrolling) {
                                    stringResource(R.string.quran_scrolling)
                                } else {
                                    stringResource(R.string.quran_auto_scroll)
                                },
                                fontWeight = FontWeight.Bold,
                                color = if (isContinuousScrolling) PureAlabaster else MedinaSage
                            )
                        }
                    }

                    val speedOptions = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f, 4.0f)
                    speedOptions.forEach { speed ->
                        val isSelected = scrollSpeed == speed
                        val speedLabel = if (speed % 1f == 0f) {
                            stringResource(R.string.quran_speed_int_format, speed.toInt())
                        } else {
                            stringResource(R.string.quran_speed_dec_format, speed)
                        }
                        AppSelectableCard(
                            isSelected = isSelected,
                            onClick = { onSetScrollSpeed(speed) },
                            selectedBackgroundColor = MedinaSage,
                            unselectedBackgroundColor = WarmParchment,
                            selectedBorderColor = MedinaSage,
                            unselectedBorderColor = DividerColor,
                            borderWidth = AppDimensions.borderWidthThin,
                            cornerRadius = AppDimensions.cornerMedium,
                            contentPadding = AppDimensions.paddingZero,
                            elevation = AppDimensions.elevationNone
                        ) {
                            AppText.Caption(
                                text = speedLabel,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PureAlabaster else TextSecondary,
                                modifier = Modifier.padding(
                                    horizontal = AppDimensions.paddingExtraSmall,
                                    vertical = AppDimensions.paddingTiny
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Computes the detail header (title, subtitle) dynamically from the Quran structure:
 * - In Surah mode: shows "Surah <name> (<arabicName>)".
 * - In Juz mode: checks whether the first Ayah of the Juz begins at Ayah 1 of a Surah
 *   (new Surah -> shows the Surah name) or Ayah > 1 (continues a Surah from the previous Juz
 *   -> shows the Juz name).
 */
@Composable
private fun resolveReaderHeaderText(
    isJuzMode: Boolean,
    surah: Surah,
    verses: List<Ayah>,
    selectedJuz: JuzInfo?,
    allSurahs: List<Surah>
): Pair<String, String> {
    val verseCount = verses.size.takeIf { it > 0 } ?: selectedJuz?.totalVerses ?: surah.totalVerses
    if (!isJuzMode) {
        return stringResource(R.string.quran_surah_header_format, surah.name, surah.arabicName) to
            stringResource(R.string.quran_surah_subtitle_format, surah.englishMeaning, verseCount)
    }

    val firstAyah = verses.firstOrNull()
    val startsNewSurah = firstAyah?.let { it.numberInSurah == 1 }
        ?: selectedJuz?.startsNewSurah
        ?: false
    val firstSurahNum = firstAyah?.surahNumber?.takeIf { it > 0 }
        ?: selectedJuz?.startingSurahNumber
        ?: surah.number
    val startingSurah = allSurahs.find { it.number == firstSurahNum }
        ?: QuranStaticData.ALL_SURAHS.find { it.number == firstSurahNum }

    val juzLabel = selectedJuz?.let {
        stringResource(R.string.quran_juz_name_format, it.number, it.englishName)
    } ?: surah.name

    return if (startsNewSurah) {
        val displaySurah = startingSurah ?: surah
        stringResource(R.string.quran_surah_header_format, displaySurah.name, displaySurah.arabicName) to
            stringResource(R.string.quran_surah_subtitle_format, juzLabel, verseCount)
    } else {
        val continuingName = startingSurah?.name ?: selectedJuz?.startingSurahName ?: surah.name
        juzLabel to stringResource(R.string.quran_continues_surah_format, continuingName, verseCount)
    }
}

@Composable
private fun SurahStartHeaderBanner(
    surah: Surah,
    selectedQuranFont: String,
    showTranslation: Boolean = true,
    onToggleTranslation: () -> Unit = {},
    onOpenTafseer: () -> Unit = {}
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = AppDimensions.cornerLarge,
        backgroundColor = MedinaSage,
        borderColor = DesertOchre.copy(alpha = 0.55f),
        borderWidth = AppDimensions.borderWidthThin,
        contentPadding = AppDimensions.paddingZero,
        elevation = AppDimensions.elevationMedium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroBannerGradient)
                .padding(
                    horizontal = AppDimensions.paddingMediumSmall,
                    vertical = AppDimensions.paddingMediumSmall
                )
        ) {
            // Top Section: Left (Calligraphic Surah Name + Translation & Tafseer buttons) | Right (Kaaba Circle + Ayaat count)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = AppDimensions.paddingRegular),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (surah.arabicName.isNotBlank()) {
                        AppText.ArabicText(
                            text = surah.arabicName,
                            color = PureAlabaster,
                            fontSize = AppDimensions.textArabicBanner,
                            textAlign = TextAlign.Center,
                            fontKeyOverride = selectedQuranFont,
                            maxLines = 1,
                            applySafePadding = false
                        )
                    } else {
                        AppText.HeadingMedium(
                            text = stringResource(R.string.quran_surah_name_format, surah.name),
                            color = PureAlabaster,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppCard(
                            onClick = onToggleTranslation,
                            modifier = Modifier.weight(1f),
                            cornerRadius = AppDimensions.cornerSmall,
                            backgroundColor = if (showTranslation) PureAlabaster.copy(alpha = 0.16f) else PureAlabaster.copy(alpha = 0.06f),
                            borderColor = PureAlabaster.copy(alpha = 0.45f),
                            borderWidth = AppDimensions.borderWidthThin,
                            contentPadding = AppDimensions.paddingZero,
                            elevation = AppDimensions.elevationNone
                        ) {
                            Box(
                                modifier = Modifier.padding(
                                    vertical = AppDimensions.paddingExtraSmall,
                                    horizontal = AppDimensions.paddingExtraSmall
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText.BodySmall(
                                    text = stringResource(R.string.quran_translation_btn),
                                    fontWeight = FontWeight.Medium,
                                    color = PureAlabaster,
                                    maxLines = 1
                                )
                            }
                        }

                        AppCard(
                            onClick = onOpenTafseer,
                            modifier = Modifier.weight(1f),
                            cornerRadius = AppDimensions.cornerSmall,
                            backgroundColor = PureAlabaster.copy(alpha = 0.10f),
                            borderColor = PureAlabaster.copy(alpha = 0.45f),
                            borderWidth = AppDimensions.borderWidthThin,
                            contentPadding = AppDimensions.paddingZero,
                            elevation = AppDimensions.elevationNone
                        ) {
                            Box(
                                modifier = Modifier.padding(
                                    vertical = AppDimensions.paddingExtraSmall,
                                    horizontal = AppDimensions.paddingExtraSmall
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                AppText.BodySmall(
                                    text = stringResource(R.string.quran_tafseer_btn),
                                    fontWeight = FontWeight.Medium,
                                    color = PureAlabaster,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                AppVerticalDivider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = AppDimensions.paddingMicro),
                    thickness = AppDimensions.dividerHairline,
                    color = PureAlabaster.copy(alpha = 0.22f)
                )

                Column(
                    modifier = Modifier.padding(
                        start = AppDimensions.paddingMediumSmall,
                        end = AppDimensions.paddingTiny
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.badgeCircleLarge)
                            .clip(CircleShape)
                            .background(PureAlabaster.copy(alpha = 0.14f))
                            .border(AppDimensions.borderWidthThick, PureAlabaster, CircleShape)
                            .padding(AppDimensions.paddingExtraSmall),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_kaaba),
                            contentDescription = stringResource(R.string.quran_surah_revelation_desc),
                            modifier = Modifier.size(AppDimensions.iconExtraLarge)
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                    AppText.BodySmall(
                        text = stringResource(R.string.quran_ayaat_count_format, surah.totalVerses),
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                }
            }

            // Bottom Bismillah Section (shown for all Surahs except Surah At-Tawbah #9)
            if (surah.number != 9) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))
                AppDivider(
                    thickness = AppDimensions.dividerHairline,
                    color = PureAlabaster.copy(alpha = 0.22f)
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppText.UrduText(
                        text = stringResource(R.string.quran_bismillah_urdu),
                        fontSize = AppDimensions.textBodyMedium,
                        color = PureAlabaster.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1.05f)
                            .padding(end = AppDimensions.paddingSmall)
                    )

                    AppText.ArabicText(
                        text = stringResource(R.string.quran_bismillah_arabic),
                        color = PureAlabaster,
                        fontSize = AppDimensions.textBismillahBanner,
                        textAlign = TextAlign.End,
                        fontKeyOverride = selectedQuranFont,
                        applySafePadding = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
