package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.HeroBannerGradient
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import com.ne.muslimprayers.qiblafinder.ui.quran.JuzInfo
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranDownloadStatus
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranStaticData
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
    var isContinuousScrolling by remember { mutableStateOf(false) }
    var scrollSpeed by remember { mutableFloatStateOf(1.0f) }
    val listState = rememberLazyListState()

    // Smooth continuous auto-scrolling that seamlessly pauses during manual user touch/drag/fling
    // and automatically resumes from the new page/scroll position once the user releases!
    LaunchedEffect(isContinuousScrolling, scrollSpeed) {
        if (isContinuousScrolling) {
            while (isActive) {
                try {
                    listState.scrollBy(1.5f * scrollSpeed)
                } catch (e: CancellationException) {
                    if (!isActive) throw e
                    // User manually scrolled the page; wait until manual scroll gesture finishes,
                    // then resume auto-scrolling from where the user left the page.
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
    LaunchedEffect(currentlyPlayingAyah, isAutoScrollEnabled) {
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
                Surface(
                    color = WarmParchment,
                    tonalElevation = AppDimensions.elevationLow,
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
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.size(AppDimensions.iconButtonCompact)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(AppDimensions.iconMedium)
                                )
                            }

                            Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))

                            val (headerTitle, headerSubtitle) = remember(
                                isJuzMode,
                                surah,
                                verses,
                                selectedJuz,
                                allSurahs
                            ) {
                                resolveReaderHeaderText(
                                    isJuzMode = isJuzMode,
                                    surah = surah,
                                    verses = verses,
                                    selectedJuz = selectedJuz,
                                    allSurahs = allSurahs
                                )
                            }

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
                                // Only show Download button if NOT already downloaded
                                if (isDownloadingThis) {
                                    Box(
                                        modifier = Modifier.size(AppDimensions.iconButtonCompact),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(AppDimensions.iconSmall),
                                            color = MedinaSage,
                                            strokeWidth = AppDimensions.borderWidthThick
                                        )
                                    }
                                } else if (!isDownloaded) {
                                    IconButton(
                                        onClick = onDownloadSurah,
                                        modifier = Modifier.size(AppDimensions.iconButtonCompact)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Download Audio",
                                            tint = MedinaSage,
                                            modifier = Modifier.size(AppDimensions.iconMedium)
                                        )
                                    }
                                }

                                // Full-Screen Toggle
                                IconButton(
                                    onClick = onToggleFullScreen,
                                    modifier = Modifier.size(AppDimensions.iconButtonCompact)
                                ) {
                                    Icon(
                                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = "Full Screen",
                                        tint = MedinaSage,
                                        modifier = Modifier.size(AppDimensions.iconMedium)
                                    )
                                }

                                // Quran Settings Button
                                IconButton(
                                    onClick = onOpenSettings,
                                    modifier = Modifier.size(AppDimensions.iconButtonCompact)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Quran Settings",
                                        tint = MedinaSage,
                                        modifier = Modifier.size(AppDimensions.iconMedium)
                                    )
                                }
                            }
                        }

                        if (isDownloadingThis) {
                            LinearProgressIndicator(
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
                LoadingState(message = "Loading Holy Verses...")
            } else if (errorMessage != null && verses.isEmpty()) {
                ErrorState(message = errorMessage, onRetry = onRetry)
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = AppDimensions.paddingMediumSmall,
                        end = AppDimensions.paddingMediumSmall,
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

        // Bottom Floating Controls: Screen Resize Icon at top of Auto-Scroll Layout (in Full Screen) + Auto-Scroll Bar (up to 4x)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isFullScreen) AppDimensions.paddingMedium else AppDimensions.paddingMediumLarge)
                .padding(horizontal = AppDimensions.paddingMediumSmall),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
        ) {
            if (isFullScreen) {
                Surface(
                    onClick = onToggleFullScreen,
                    shape = CircleShape,
                    color = MedinaSage,
                    contentColor = PureAlabaster,
                    shadowElevation = AppDimensions.elevationHigh,
                    border = BorderStroke(AppDimensions.borderWidthThin, DesertOchre.copy(alpha = 0.5f)),
                    modifier = Modifier.size(AppDimensions.avatarSize)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit Full Screen",
                            tint = PureAlabaster,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(AppDimensions.cornerDialog),
                color = PureAlabaster.copy(alpha = 0.96f),
                shadowElevation = AppDimensions.elevationHigh,
                border = BorderStroke(AppDimensions.borderWidthThin, MedinaSage.copy(alpha = 0.25f))
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
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppDimensions.cornerLarge))
                            .clickableDebounced {
                                isContinuousScrolling = !isContinuousScrolling
                            },
                        color = if (isContinuousScrolling) MedinaSage else MedinaSageLight
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
                                contentDescription = if (isContinuousScrolling) "Pause Auto-Scroll" else "Start Auto-Scroll",
                                tint = if (isContinuousScrolling) PureAlabaster else MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                            AppText.Caption(
                                text = if (isContinuousScrolling) stringResource(R.string.quran_scrolling) else stringResource(R.string.quran_auto_scroll),
                                fontWeight = FontWeight.Bold,
                                color = if (isContinuousScrolling) PureAlabaster else MedinaSage
                            )
                        }
                    }

                    val speedOptions = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f, 4.0f)
                    speedOptions.forEach { speed ->
                        val isSelected = scrollSpeed == speed
                        val speedLabel = if (speed % 1f == 0f) "${speed.toInt()}x" else "${speed}x"
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                                .clickableDebounced {
                                    scrollSpeed = speed
                                },
                            color = if (isSelected) MedinaSage else WarmParchment,
                            border = BorderStroke(
                                AppDimensions.borderWidthThin,
                                if (isSelected) MedinaSage else DividerColor
                            )
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
private fun resolveReaderHeaderText(
    isJuzMode: Boolean,
    surah: Surah,
    verses: List<Ayah>,
    selectedJuz: JuzInfo?,
    allSurahs: List<Surah>
): Pair<String, String> {
    val verseCount = verses.size.takeIf { it > 0 } ?: selectedJuz?.totalVerses ?: surah.totalVerses
    if (!isJuzMode) {
        return "Surah ${surah.name} (${surah.arabicName})" to "${surah.englishMeaning} • $verseCount Verses"
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

    val juzLabel = selectedJuz?.let { "Juz ${it.number} (${it.englishName})" } ?: surah.name

    return if (startsNewSurah) {
        val displaySurah = startingSurah ?: surah
        "Surah ${displaySurah.name} (${displaySurah.arabicName})" to "$juzLabel • $verseCount Verses"
    } else {
        val continuingName = startingSurah?.name ?: selectedJuz?.startingSurahName ?: surah.name
        juzLabel to "Continues Surah $continuingName • $verseCount Verses"
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.cornerLarge),
        color = MedinaSage,
        border = BorderStroke(AppDimensions.borderWidthThin, DesertOchre.copy(alpha = 0.55f)),
        tonalElevation = AppDimensions.elevationMedium
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
                            text = "Surah ${surah.name}",
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
                        Surface(
                            onClick = onToggleTranslation,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(AppDimensions.cornerSmall),
                            color = if (showTranslation) PureAlabaster.copy(alpha = 0.16f) else PureAlabaster.copy(alpha = 0.06f),
                            border = BorderStroke(AppDimensions.borderWidthThin, PureAlabaster.copy(alpha = 0.45f))
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

                        Surface(
                            onClick = onOpenTafseer,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(AppDimensions.cornerSmall),
                            color = PureAlabaster.copy(alpha = 0.10f),
                            border = BorderStroke(AppDimensions.borderWidthThin, PureAlabaster.copy(alpha = 0.45f))
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

                VerticalDivider(
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
                            contentDescription = "Surah Revelation",
                            modifier = Modifier.size(AppDimensions.iconExtraLarge)
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                    AppText.BodySmall(
                        text = "Ayaat : ${surah.totalVerses}",
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                }
            }

            // Bottom Bismillah Section (shown for all Surahs except Surah At-Tawbah #9)
            if (surah.number != 9) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))
                HorizontalDivider(
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
                        text = "شروع کرتا ہوں اللہ تعالیٰ کے نام سے جو بڑا مہربان نہایت رحم والا ہے ۔",
                        fontSize = AppDimensions.textBodyMedium,
                        color = PureAlabaster.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1.05f)
                            .padding(end = AppDimensions.paddingSmall)
                    )

                    AppText.ArabicText(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
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
