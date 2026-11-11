package com.example.noorapp.ui.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.ui.quran.QuranUiState
import com.example.noorapp.ui.quran.components.settings.QuranColorModeCard
import com.example.noorapp.ui.quran.components.settings.QuranFontSizesCard
import com.example.noorapp.ui.quran.components.settings.QuranOfflineAudioCard
import com.example.noorapp.ui.quran.components.settings.QuranPlaybackControlsCard
import com.example.noorapp.ui.quran.components.settings.QuranReadingTogglesCard
import com.example.noorapp.ui.quran.components.settings.QuranReciterSelectorCard
import com.example.noorapp.ui.quran.components.settings.QuranScriptFontCard

/**
 * Pure body content rendered inside common [com.example.noorapp.core.designsystem.AppBottomSheet] in [com.example.noorapp.ui.quran.QuranScreen].
 */
@Composable
fun QuranSettingsSheetContent(
    state: QuranUiState,
    onToggleTranslation: () -> Unit,
    onToggleTransliteration: () -> Unit,
    onToggleIndoPakScript: () -> Unit,
    onSetQuranFont: (String) -> Unit = {},
    onToggleUrduTranslation: () -> Unit = {},
    onToggleColoredTajweedQuran: (Boolean) -> Unit = {},
    onOpenWaqfGuide: () -> Unit = {},
    onDownloadSurah: () -> Unit = {},
    onDeleteSurahAudio: () -> Unit = {},
    onClearAllAudio: () -> Unit = {},
    onToggleAutoPlay: () -> Unit,
    onToggleAutoScroll: () -> Unit = {},
    onSetReciter: (String) -> Unit,
    onAdjustFontSize: (Float) -> Unit,
    onSetArabicFontSize: (Float) -> Unit = {},
    onSetTranslationFontSize: (Float) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(WarmParchment),
        contentPadding = PaddingValues(vertical = AppDimensions.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
    ) {
        item {
            QuranReciterSelectorCard(
                selectedReciterId = state.selectedReciter,
                onSetReciter = onSetReciter
            )
        }

        item {
            QuranScriptFontCard(
                selectedQuranFont = state.selectedQuranFont,
                onSetQuranFont = onSetQuranFont
            )
        }

        item {
            QuranColorModeCard(
                isColoredTajweedQuran = state.isColoredTajweedQuran,
                onToggleColoredTajweedQuran = onToggleColoredTajweedQuran,
                onOpenWaqfGuide = onOpenWaqfGuide
            )
        }

        item {
            QuranFontSizesCard(
                arabicFontSize = state.arabicFontSize,
                translationFontSize = state.translationFontSize,
                selectedQuranFont = state.selectedQuranFont,
                onSetArabicFontSize = onSetArabicFontSize,
                onSetTranslationFontSize = onSetTranslationFontSize
            )
        }

        item {
            QuranReadingTogglesCard(
                showUrduTranslation = state.showUrduTranslation,
                showTranslation = state.showTranslation,
                showTransliteration = state.showTransliteration,
                onToggleUrduTranslation = onToggleUrduTranslation,
                onToggleTranslation = onToggleTranslation,
                onToggleTransliteration = onToggleTransliteration
            )
        }

        item {
            QuranPlaybackControlsCard(
                autoPlayNext = state.autoPlayNext,
                isAutoScrollEnabled = state.isAutoScrollEnabled,
                onToggleAutoPlay = onToggleAutoPlay,
                onToggleAutoScroll = onToggleAutoScroll
            )
        }

        item {
            QuranOfflineAudioCard(
                state = state,
                onDownloadSurah = onDownloadSurah,
                onDeleteSurahAudio = onDeleteSurahAudio,
                onClearAllAudio = onClearAllAudio
            )
        }

        item {
            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumLarge))
        }
    }
}
