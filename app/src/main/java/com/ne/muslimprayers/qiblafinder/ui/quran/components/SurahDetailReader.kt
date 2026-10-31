package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerticalAlignBottom
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranDownloadStatus
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
import androidx.compose.ui.text.font.FontWeight
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
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState

@Composable
fun SurahDetailReader(
    surah: Surah,
    verses: List<Ayah>,
    isLoading: Boolean,
    errorMessage: String?,
    currentlyPlayingAyah: Int?,
    arabicFontSize: Float,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    showUrduTranslation: Boolean = true,
    selectedQuranFont: String = "indopak",
    useIndoPakScript: Boolean = true,
    isColoredQuran: Boolean = true,
    isDownloaded: Boolean = false,
    downloadStatus: QuranDownloadStatus = QuranDownloadStatus(),
    bookmarkedAyahNumbers: Set<Int> = emptySet(),
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
            val targetIndex = verses.indexOfFirst { it.numberInSurah == currentlyPlayingAyah }
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    if (showWaqfGuideDialog) {
        TajweedWaqfGuideDialog(onDismiss = { showWaqfGuideDialog = false })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmParchment)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Reader Header TopBar (Hidden when in Full Screen mode for immersive reading)
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
                                    text = "Surah ${surah.name} (${surah.arabicName})",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                AppText.BodySmall(
                                    text = "${surah.englishMeaning} • ${surah.totalVerses} Verses",
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.sdp)
                            ) {
                                // Download Whole Surah Audio Button
                                val isDownloadingThisSurah = downloadStatus.isDownloading && downloadStatus.targetKey == "surah_${surah.number}"
                                IconButton(
                                    onClick = {
                                        if (isDownloaded) {
                                            onDeleteSurahAudio()
                                        } else if (!isDownloadingThisSurah) {
                                            onDownloadSurah()
                                        }
                                    },
                                    modifier = Modifier.size(34.sdp)
                                ) {
                                    if (isDownloadingThisSurah) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.sdp),
                                            color = MedinaSage,
                                            strokeWidth = 2.dp
                                        )
                                    } else if (isDownloaded) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Surah Downloaded (Offline Available)",
                                            tint = MedinaSage,
                                            modifier = Modifier.size(20.sdp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Download Surah Audio",
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

                                // Quran Settings Button - Guaranteed visible on screen
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

                        // Linear download progress indicator when downloading this Surah
                        if (downloadStatus.isDownloading && downloadStatus.targetKey == "surah_${surah.number}") {
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

            // Bismillah Header Banner for all Surahs except Surah At-Tawbah (9) and Al-Fatiha (1)
            if (surah.number != 9 && surah.number != 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HeroBannerGradient)
                        .padding(vertical = if (isFullScreen) 8.sdp else 12.sdp),
                    contentAlignment = Alignment.Center
                ) {
                    AppText.HeadingLarge(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        color = PureAlabaster
                    )
                }
            }

            // Informational guide & mode strip
            if (!isFullScreen) {
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
                            "hafs" -> "Hafs"
                            "muhammadi" -> "Muhammadi"
                            "al_majeed" -> "Al-Majeed"
                            "al_mushaf" -> "Al-Mushaf"
                            "al_qalam" -> "Al-Qalam"
                            "nabi" -> "Nabi"
                            "pdms_saleem", "pdms" -> "PDMS Saleem"
                            "uthmani" -> "Hafs"
                            else -> "Muhammadi"
                        }
                        Text(
                            text = "$shortFontTitle • Waqf Active",
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
            }

            if (isLoading && verses.isEmpty()) {
                LoadingState(message = "Loading Holy Verses...")
            } else if (errorMessage != null && verses.isEmpty()) {
                ErrorState(message = errorMessage, onRetry = onRetry)
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.sdp, vertical = 8.sdp),
                    verticalArrangement = Arrangement.spacedBy(14.sdp)
                ) {
                    itemsIndexed(verses) { index, ayah ->
                        val isPlaying = currentlyPlayingAyah == ayah.numberInSurah
                        val isBookmarked = bookmarkedAyahNumbers.contains(ayah.numberInSurah)

                        AyahCardItem(
                            ayah = ayah,
                            surahNumber = surah.number,
                            arabicFontSize = arabicFontSize,
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

        // Floating Auto-Scroll Control Pill (Recitation Page)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isFullScreen) 16.sdp else 20.sdp)
                .padding(horizontal = 14.sdp),
            shape = RoundedCornerShape(24.sdp),
            color = PureAlabaster.copy(alpha = 0.96f),
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, MedinaSage.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.sdp, vertical = 6.sdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.sdp)
            ) {
                // Play / Pause Toggle Button
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

                // Speed Selector Chips: 0.5x, 1x, 1.5x, 2x, 3x
                val speedOptions = listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f)
                speedOptions.forEach { speed ->
                    val isSelected = scrollSpeed == speed
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
                            text = "${speed}x",
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PureAlabaster else TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.sdp, vertical = 4.sdp)
                        )
                    }
                }
            }
        }

        // Full Screen Exit Floating Button
        if (isFullScreen) {
            FloatingActionButton(
                onClick = onToggleFullScreen,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.sdp),
                containerColor = MedinaSage,
                contentColor = PureAlabaster
            ) {
                Icon(
                    imageVector = Icons.Default.FullscreenExit,
                    contentDescription = "Exit Full Screen"
                )
            }
        }
    }
}
