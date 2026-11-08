package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.HeroBannerGradient
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranDownloadStatus
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranStaticData
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter
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
    onOpenTafseer: (Ayah) -> Unit = {}
) {
    var showWaqfGuideDialog by remember { mutableStateOf(false) }
    var isContinuousScrolling by remember { mutableStateOf(false) }
    var scrollSpeed by remember { mutableFloatStateOf(1.0f) }
    val listState = rememberLazyListState()

    // Smooth continuous auto-scrolling (teleprompter mode)
    LaunchedEffect(isContinuousScrolling, scrollSpeed) {
        if (isContinuousScrolling) {
            while (isActive) {
                listState.scrollBy(1.5f * scrollSpeed)
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

    if (showWaqfGuideDialog) {
        TajweedWaqfGuideDialog(onDismiss = { showWaqfGuideDialog = false })
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
                    tonalElevation = 2.sdp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 8.sdp, vertical = 4.sdp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.size(34.sdp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.sdp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.sdp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                AppText.TitleMedium(
                                    text = if (isJuzMode) surah.name else "Surah ${surah.name} (${surah.arabicName})",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                AppText.BodySmall(
                                    text = "${surah.englishMeaning} • ${verses.size.takeIf { it > 0 } ?: surah.totalVerses} Verses",
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.sdp)
                            ) {
                                // Only show Download button if NOT already downloaded
                                if (isDownloadingThis) {
                                    Box(
                                        modifier = Modifier.size(34.sdp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.sdp),
                                            color = MedinaSage,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                } else if (!isDownloaded) {
                                    IconButton(
                                        onClick = onDownloadSurah,
                                        modifier = Modifier.size(34.sdp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Download Audio",
                                            tint = MedinaSage,
                                            modifier = Modifier.size(20.sdp)
                                        )
                                    }
                                }

                                // Full-Screen Toggle
                                IconButton(
                                    onClick = onToggleFullScreen,
                                    modifier = Modifier.size(34.sdp)
                                ) {
                                    Icon(
                                        imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = "Full Screen",
                                        tint = MedinaSage,
                                        modifier = Modifier.size(20.sdp)
                                    )
                                }

                                // Quran Settings Button
                                IconButton(
                                    onClick = onOpenSettings,
                                    modifier = Modifier.size(34.sdp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Quran Settings",
                                        tint = MedinaSage,
                                        modifier = Modifier.size(20.sdp)
                                    )
                                }
                            }
                        }

                        if (isDownloadingThis) {
                            LinearProgressIndicator(
                                progress = { downloadStatus.progressPercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.sdp),
                                color = MedinaSage,
                                trackColor = MedinaSageLight
                            )
                        }
                    }
                }
            }

            // Informational guide, font & Coloured Tajweed mode strip
            if (!isFullScreen) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.sdp, vertical = 4.sdp)
                            .clip(RoundedCornerShape(10.sdp))
                            .background(DesertOchreLight.copy(alpha = 0.5f))
                            .padding(horizontal = 10.sdp, vertical = 6.sdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickableDebounced { showWaqfGuideDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.sdp)
                                    .clip(CircleShape)
                                    .background(DesertOchre),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "۝",
                                    color = PureAlabaster,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.sdp))
                            val shortFontTitle = when (selectedQuranFont) {
                                "hafs", "uthmani" -> "Hafs"
                                "muhammadi" -> "Muhammadi"
                                "al_majeed" -> "Al-Majeed"
                                "al_mushaf" -> "Al-Mushaf"
                                "al_qalam" -> "Al-Qalam"
                                "nabi" -> "Nabi"
                                "pdms_saleem", "pdms", "indopak" -> "PDMS Saleem"
                                else -> "Muhammadi"
                            }
                            Text(
                                text = "$shortFontTitle • Tajweed & Waqf Guide",
                                color = TextPrimary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.sdp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.sdp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.sdp))
                                    .clickableDebounced { onToggleColoredQuran() },
                                color = if (isColoredQuran) MedinaSageLight else PureAlabaster,
                                border = BorderStroke(1.dp, if (isColoredQuran) MedinaSage else DividerColor)
                            ) {
                                Text(
                                    text = if (isColoredQuran) "🎨 Coloured" else "📖 Plain",
                                    modifier = Modifier.padding(horizontal = 8.sdp, vertical = 4.sdp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isColoredQuran) MedinaSage else TextPrimary,
                                    maxLines = 1
                                )
                            }

                            IconButton(
                                onClick = { showWaqfGuideDialog = true },
                                modifier = Modifier.size(26.sdp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = "Tajweed Waqf Signs Guide",
                                    tint = DesertOchre,
                                    modifier = Modifier.size(16.sdp)
                                )
                            }
                        }
                    }

                    // Compact Tajweed Color Legend Bar when Coloured Quran is active
                    if (isColoredQuran) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.sdp, vertical = 2.sdp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.sdp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TajweedLegendChip("Waqf / Pause", QuranTextFormatter.TAJWEED_WAQF_COLOR)
                            TajweedLegendChip("Madd", QuranTextFormatter.TAJWEED_MADD_COLOR)
                            TajweedLegendChip("Ghunnah", QuranTextFormatter.TAJWEED_GHUNNAH_COLOR)
                            TajweedLegendChip("Ikhfa/Idgham", QuranTextFormatter.TAJWEED_IKHFA_COLOR)
                            TajweedLegendChip("Qalqalah", QuranTextFormatter.TAJWEED_QALQALAH_COLOR)
                            TajweedLegendChip("Iqlab", QuranTextFormatter.TAJWEED_IQLAB_COLOR)
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
                        start = 16.sdp,
                        end = 16.sdp,
                        top = 8.sdp,
                        bottom = 84.sdp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.sdp)
                ) {
                    itemsIndexed(
                        items = verses,
                        key = { index, ayah -> "${ayah.surahNumber}_${ayah.numberInSurah}_$index" }
                    ) { index, ayah ->
                        val prevAyah = if (index > 0) verses[index - 1] else null
                        val effectiveSurahNum = if (ayah.surahNumber > 0) ayah.surahNumber else surah.number

                        val isNewJuzStart = index == 0 || (prevAyah != null && ayah.juz != prevAyah.juz)
                        val isNewSurahTransition = index == 0 ||
                                (prevAyah != null && effectiveSurahNum != (if (prevAyah.surahNumber > 0) prevAyah.surahNumber else surah.number))

                        Column(verticalArrangement = Arrangement.spacedBy(10.sdp)) {
                            // 1. Juz / Para Start Indicator Banner
                            if (isNewJuzStart) {
                                JuzStartHeaderBanner(
                                    juzNumber = ayah.juz,
                                    isMidSurahTransition = index > 0
                                )
                            }

                            // 2. Surah Start Header Banner + Bismillah
                            if (isNewSurahTransition) {
                                val surahMeta = remember(effectiveSurahNum, allSurahs) {
                                    allSurahs.find { it.number == effectiveSurahNum }
                                        ?: QuranStaticData.ALL_SURAHS.find { it.number == effectiveSurahNum }
                                        ?: surah
                                }
                                SurahStartHeaderBanner(
                                    surah = surahMeta,
                                    startingAyahNumber = ayah.numberInSurah,
                                    selectedQuranFont = selectedQuranFont
                                )
                            }

                            // 3. Verse Card
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
                .padding(bottom = if (isFullScreen) 16.sdp else 20.sdp)
                .padding(horizontal = 12.sdp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.sdp)
        ) {
            if (isFullScreen) {
                Surface(
                    onClick = onToggleFullScreen,
                    shape = CircleShape,
                    color = MedinaSage,
                    contentColor = PureAlabaster,
                    shadowElevation = 6.dp,
                    border = BorderStroke(1.dp, DesertOchre.copy(alpha = 0.5f)),
                    modifier = Modifier.size(40.sdp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FullscreenExit,
                            contentDescription = "Exit Full Screen",
                            tint = PureAlabaster,
                            modifier = Modifier.size(22.sdp)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(24.sdp),
                color = PureAlabaster.copy(alpha = 0.96f),
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MedinaSage.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.sdp, vertical = 6.sdp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.sdp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.sdp))
                            .clickableDebounced {
                                isContinuousScrolling = !isContinuousScrolling
                            },
                        color = if (isContinuousScrolling) MedinaSage else MedinaSageLight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.sdp, vertical = 6.sdp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.sdp)
                        ) {
                            Icon(
                                imageVector = if (isContinuousScrolling) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isContinuousScrolling) "Pause Auto-Scroll" else "Start Auto-Scroll",
                                tint = if (isContinuousScrolling) PureAlabaster else MedinaSage,
                                modifier = Modifier.size(16.sdp)
                            )
                            Text(
                                text = if (isContinuousScrolling) "Scrolling" else "Auto-Scroll",
                                fontSize = 11.5.sp,
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
                                .clip(RoundedCornerShape(12.sdp))
                                .clickableDebounced {
                                    scrollSpeed = speed
                                },
                            color = if (isSelected) MedinaSage else WarmParchment,
                            border = BorderStroke(1.dp, if (isSelected) MedinaSage else DividerColor)
                        ) {
                            Text(
                                text = speedLabel,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PureAlabaster else TextSecondary,
                                modifier = Modifier.padding(horizontal = 6.sdp, vertical = 4.sdp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JuzStartHeaderBanner(
    juzNumber: Int,
    isMidSurahTransition: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.sdp),
        color = DesertOchreLight.copy(alpha = 0.75f),
        border = BorderStroke(1.2.dp, DesertOchre)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.sdp, vertical = 8.sdp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.sdp)
                        .clip(CircleShape)
                        .background(DesertOchre),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$juzNumber",
                        color = PureAlabaster,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.sdp))
                Column {
                    Text(
                        text = if (isMidSurahTransition)
                            "NEW JUZ / PARA STARTED • JUZ $juzNumber"
                        else
                            "JUZ / PARA $juzNumber",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "الجزء $juzNumber من القرآن الكريم",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
            Text(
                text = "۞",
                fontSize = 20.sp,
                color = DesertOchre,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SurahStartHeaderBanner(
    surah: Surah,
    startingAyahNumber: Int,
    selectedQuranFont: String
) {
    val isSurahBeginning = startingAyahNumber == 1

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.sdp),
        color = MedinaSage,
        border = BorderStroke(1.5.dp, DesertOchre.copy(alpha = 0.65f)),
        tonalElevation = 4.sdp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroBannerGradient)
                .padding(horizontal = 16.sdp, vertical = 14.sdp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Badge: New Surah Started vs Continuing Surah
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.sdp))
                    .background(DesertOchre.copy(alpha = 0.28f))
                    .border(0.8.dp, DesertOchre, RoundedCornerShape(20.sdp))
                    .padding(horizontal = 12.sdp, vertical = 3.sdp)
            ) {
                Text(
                    text = if (isSurahBeginning)
                        "✦ SURAH ${surah.number} • ${surah.revelationType.uppercase()} • ${surah.totalVerses} VERSES ✦"
                    else
                        "✦ SURAH ${surah.number} • CONTINUING FROM AYAH $startingAyahNumber ✦",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = PureAlabaster
                )
            }

            Spacer(modifier = Modifier.height(8.sdp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppText.HeadingMedium(
                        text = "Surah ${surah.name}",
                        color = PureAlabaster,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    if (surah.englishMeaning.isNotBlank()) {
                        AppText.BodySmall(
                            text = surah.englishMeaning,
                            color = MedinaSageLight,
                            maxLines = 1
                        )
                    }
                }

                if (surah.arabicName.isNotBlank()) {
                    AppText.ArabicText(
                        text = surah.arabicName,
                        color = DesertOchreLight,
                        fontSize = 24.sp,
                        fontKeyOverride = selectedQuranFont,
                        maxLines = 1
                    )
                }
            }

            // Bismillah Banner at the start of every Surah except Surah At-Tawbah (9) and Surah Al-Fatiha (1)
            if (isSurahBeginning && surah.number != 9 && surah.number != 1) {
                Spacer(modifier = Modifier.height(10.sdp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.sdp))
                        .background(PureAlabaster.copy(alpha = 0.12f))
                        .border(0.8.dp, DesertOchre.copy(alpha = 0.45f), RoundedCornerShape(12.sdp))
                        .padding(vertical = 6.sdp, horizontal = 8.sdp),
                    contentAlignment = Alignment.Center
                ) {
                    AppText.ArabicText(
                        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                        color = PureAlabaster,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        fontKeyOverride = selectedQuranFont,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun TajweedLegendChip(
    label: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.sdp))
            .background(PureAlabaster)
            .border(0.8.dp, color.copy(alpha = 0.45f), RoundedCornerShape(8.sdp))
            .padding(horizontal = 8.sdp, vertical = 3.sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.sdp)
    ) {
        Box(
            modifier = Modifier
                .size(8.sdp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}
