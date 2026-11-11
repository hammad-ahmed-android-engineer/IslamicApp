package com.example.noorapp.ui.quran.components.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCircularProgress
import com.example.noorapp.core.designsystem.AppFilterChip
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.domain.model.AVAILABLE_RECITERS
import com.example.noorapp.ui.quran.QuranUiState

@Composable
fun QuranReciterSelectorCard(
    selectedReciterId: String,
    onSetReciter: (String) -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        borderWidth = AppDimensions.borderWidthHairline,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingMediumSmall
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconRegular)
                )
                AppText.BodyMedium(
                    text = stringResource(R.string.quran_qari_qirah_section),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                AVAILABLE_RECITERS.forEach { reciter ->
                    val isSelected = selectedReciterId == reciter.id
                    AppFilterChip(
                        selected = isSelected,
                        onClick = { onSetReciter(reciter.id) },
                        selectedContainerColor = MedinaSage,
                        selectedLabelColor = PureAlabaster,
                        label = {
                            Column(modifier = Modifier.padding(vertical = AppDimensions.paddingMicro)) {
                                AppText.Caption(
                                    text = reciter.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) PureAlabaster else TextPrimary
                                )
                                AppText.Caption(
                                    text = reciter.riwayah,
                                    fontSizeOverride = AppDimensions.textMicro,
                                    color = if (isSelected) PureAlabaster.copy(alpha = 0.85f) else TextSecondary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuranOfflineAudioCard(
    state: QuranUiState,
    onDownloadSurah: () -> Unit,
    onDeleteSurahAudio: () -> Unit,
    onClearAllAudio: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        borderWidth = AppDimensions.borderWidthHairline,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingMediumSmall
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconRegular)
                    )
                    AppText.BodyMedium(
                        text = stringResource(R.string.quran_offline_audio_storage),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                AppText.Caption(
                    text = stringResource(R.string.quran_storage_used_format, state.totalAudioStorageMb),
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            if (state.selectedSurah != null) {
                val surah = state.selectedSurah
                val isJuz = state.selectedJuz != null
                val targetKey = if (isJuz) "juz_${state.selectedJuz!!.number}" else "surah_${surah.number}"
                val isDownloadingThis = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == targetKey

                AppCard(
                    backgroundColor = WarmParchment,
                    borderColor = WarmParchment,
                    borderWidth = AppDimensions.paddingZero,
                    cornerRadius = AppDimensions.cornerMedium,
                    contentPadding = AppDimensions.paddingZero,
                    elevation = AppDimensions.elevationNone,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.paddingRegular),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            AppText.BodySmall(
                                text = if (isJuz) {
                                    stringResource(R.string.quran_juz_audio_title_format, state.selectedJuz!!.number, state.selectedJuz.englishName)
                                } else {
                                    stringResource(R.string.quran_surah_audio_title_format, surah.name)
                                },
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            AppText.Caption(
                                text = if (state.isCurrentSurahDownloaded) {
                                    stringResource(R.string.quran_downloaded_offline_desc)
                                } else if (isDownloadingThis) {
                                    stringResource(R.string.quran_downloading_percent_format, state.downloadStatus.progressPercentage)
                                } else {
                                    stringResource(
                                        R.string.quran_ayahs_available_download_format,
                                        state.verses.size.takeIf { it > 0 } ?: surah.totalVerses
                                    )
                                },
                                fontSizeOverride = AppDimensions.textMicro,
                                color = TextSecondary
                            )
                        }

                        if (isDownloadingThis) {
                            AppCircularProgress(
                                modifier = Modifier.size(AppDimensions.iconMedium),
                                color = MedinaSage,
                                strokeWidth = AppDimensions.borderWidthThick
                            )
                        } else if (state.isCurrentSurahDownloaded) {
                            AppIconButton(
                                onClick = onDeleteSurahAudio,
                                modifier = Modifier.size(AppDimensions.cornerPill)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = stringResource(R.string.quran_delete_surah_audio_desc),
                                    tint = DesertOchre,
                                    modifier = Modifier.size(AppDimensions.iconRegular)
                                )
                            }
                        } else {
                            AppIconButton(
                                onClick = onDownloadSurah,
                                modifier = Modifier.size(AppDimensions.cornerPill)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = stringResource(R.string.quran_download_surah_audio_desc),
                                    tint = MedinaSage,
                                    modifier = Modifier.size(AppDimensions.iconRegular)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
            }

            AppCard(
                onClick = onClearAllAudio,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = AppDimensions.cornerMediumSmall,
                backgroundColor = PureAlabaster,
                borderColor = DividerColor,
                borderWidth = AppDimensions.borderWidthHairline,
                contentPadding = AppDimensions.paddingZero,
                elevation = AppDimensions.elevationNone
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = AppDimensions.paddingSmall,
                            horizontal = AppDimensions.paddingMediumSmall
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
                    AppText.BodySmall(
                        text = stringResource(R.string.quran_clear_audio_cache_btn),
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

