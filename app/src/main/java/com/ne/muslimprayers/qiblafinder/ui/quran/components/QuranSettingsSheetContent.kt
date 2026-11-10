package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.quran.AVAILABLE_RECITERS
import com.ne.muslimprayers.qiblafinder.ui.quran.QuranUiState
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranAudioDownloadManager
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter

data class FontScriptOption(
    val key: String,
    val title: String,
    val subtitle: String
)

val SCRIPT_OPTIONS = listOf(
    FontScriptOption("muhammadi", "Muhammadi", "Indo-Pak Script (Bold)"),
    FontScriptOption("hafs", "Hafs", "Medina Standard (Uthmani)"),
    FontScriptOption("al_majeed", "Al Majeed", "South Asian Quranic"),
    FontScriptOption("al_mushaf", "Al Mushaf", "Classic Mushaf"),
    FontScriptOption("al_qalam", "Al Qalam", "Qalam Calligraphy"),
    FontScriptOption("nabi", "Nabi", "Naskh Calligraphic"),
    FontScriptOption("pdms_saleem", "PDMS Saleem", "Pakistani Classical")
)

@Composable
fun QuranSettingsSheetContent(
    state: QuranUiState,
    onToggleTranslation: () -> Unit,
    onToggleTransliteration: () -> Unit,
    onToggleIndoPakScript: () -> Unit,
    onSetQuranFont: (String) -> Unit = {},
    onToggleUrduTranslation: () -> Unit = {},
    onToggleColoredTajweedQuran: (Boolean) -> Unit = {},
    onDownloadSurah: () -> Unit = {},
    onDeleteSurahAudio: () -> Unit = {},
    onClearAllAudio: () -> Unit = {},
    onToggleAutoPlay: () -> Unit,
    onToggleAutoScroll: () -> Unit = {},
    onSetReciter: (String) -> Unit,
    onAdjustFontSize: (Float) -> Unit,
    onSetArabicFontSize: (Float) -> Unit = {},
    onSetTranslationFontSize: (Float) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    var showWaqfGuideDialog by remember { mutableStateOf(false) }

    if (showWaqfGuideDialog) {
        TajweedWaqfGuideDialog(onDismiss = { showWaqfGuideDialog = false })
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(WarmParchment),
        contentPadding = PaddingValues(
            horizontal = AppDimensions.paddingMediumLarge,
            vertical = AppDimensions.paddingMediumSmall
        ),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
    ) {
        // Sheet Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    AppText.HeadingMedium(
                        text = "Quran Preferences",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    AppText.Caption(
                        text = "Customize script, recitation, fonts, and displays",
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
            }
        }

        // Section 1: Qir'ah & Audio Reciter Choice
        item {
            IslamicCard(
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
                            text = "Qari & Qir'ah / Riwayah",
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
                            val isSelected = state.selectedReciter == reciter.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetReciter(reciter.id) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MedinaSage,
                                    selectedLabelColor = PureAlabaster,
                                    containerColor = WarmParchment,
                                    labelColor = TextPrimary
                                ),
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

        // Section 2: Quranic Script & Calligraphy Font
        item {
            IslamicCard(
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
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconRegular)
                        )
                        AppText.BodyMedium(
                            text = "Quranic Script & Font",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                    Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)) {
                        SCRIPT_OPTIONS.chunked(2).forEach { rowOptions ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                            ) {
                                rowOptions.forEach { opt ->
                                    val isSelected = state.selectedQuranFont == opt.key ||
                                            (state.selectedQuranFont == "indopak" && opt.key == "muhammadi") ||
                                            (state.selectedQuranFont == "uthmani" && opt.key == "hafs") ||
                                            (state.selectedQuranFont == "pdms" && opt.key == "pdms_saleem")
                                    Surface(
                                        onClick = {
                                            onSetQuranFont(opt.key)
                                            ArabicFontManager.setFont(context, opt.key)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(AppDimensions.cornerMedium),
                                        color = if (isSelected) MedinaSageLight else PureAlabaster,
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
                                            color = if (isSelected) MedinaSage else DividerColor
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    vertical = AppDimensions.paddingRegular,
                                                    horizontal = AppDimensions.paddingRegular
                                                )
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AppText.BodySmall(
                                                    text = opt.title,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                    color = if (isSelected) MedinaSage else TextPrimary,
                                                    maxLines = 1
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "Selected",
                                                        tint = MedinaSage,
                                                        modifier = Modifier.size(AppDimensions.iconSmall)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                            AppText.Caption(
                                                text = opt.subtitle,
                                                fontSizeOverride = AppDimensions.textMicro,
                                                color = if (isSelected) MedinaSage else TextSecondary,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                                            AppText.ArabicText(
                                                text = "بِسْمِ اللَّهِ",
                                                fontKeyOverride = opt.key,
                                                fontSize = AppDimensions.textArabicPreview,
                                                color = if (isSelected) MedinaSage else DesertOchre,
                                                textAlign = TextAlign.Start,
                                                applySafePadding = false,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                                if (rowOptions.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Mushaf Display Mode (Coloured Tajweed vs Plain Quran) + Guide & Legend
        item {
            IslamicCard(
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
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = null,
                            tint = DesertOchre,
                            modifier = Modifier.size(AppDimensions.iconRegular)
                        )
                        AppText.BodyMedium(
                            text = "Mushaf Color Mode",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        Surface(
                            onClick = { onToggleColoredTajweedQuran(true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(AppDimensions.cornerMedium),
                            color = if (state.isColoredTajweedQuran) MedinaSageLight else PureAlabaster,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (state.isColoredTajweedQuran) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
                                color = if (state.isColoredTajweedQuran) MedinaSage else DividerColor
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = AppDimensions.paddingRegular,
                                        horizontal = AppDimensions.paddingSmall
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppText.BodySmall(
                                    text = "🎨 Coloured Quran",
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isColoredTajweedQuran) MedinaSage else TextPrimary
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                AppText.Caption(
                                    text = "Tajweed & Waqf rules",
                                    fontSizeOverride = AppDimensions.textMicro,
                                    color = if (state.isColoredTajweedQuran) MedinaSage else TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        Surface(
                            onClick = { onToggleColoredTajweedQuran(false) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(AppDimensions.cornerMedium),
                            color = if (!state.isColoredTajweedQuran) MedinaSageLight else PureAlabaster,
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (!state.isColoredTajweedQuran) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
                                color = if (!state.isColoredTajweedQuran) MedinaSage else DividerColor
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = AppDimensions.paddingRegular,
                                        horizontal = AppDimensions.paddingSmall
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AppText.BodySmall(
                                    text = "📖 Plain Quran",
                                    fontWeight = FontWeight.Bold,
                                    color = if (!state.isColoredTajweedQuran) MedinaSage else TextPrimary
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                AppText.Caption(
                                    text = "Classic monochrome",
                                    fontSizeOverride = AppDimensions.textMicro,
                                    color = if (!state.isColoredTajweedQuran) MedinaSage else TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                    // Tajweed Color Legend Chips inside Settings
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingsTajweedLegendChip("Waqf / Pause", QuranTextFormatter.TAJWEED_WAQF_COLOR)
                        SettingsTajweedLegendChip("Madd", QuranTextFormatter.TAJWEED_MADD_COLOR)
                        SettingsTajweedLegendChip("Ghunnah", QuranTextFormatter.TAJWEED_GHUNNAH_COLOR)
                        SettingsTajweedLegendChip("Ikhfa/Idgham", QuranTextFormatter.TAJWEED_IKHFA_COLOR)
                        SettingsTajweedLegendChip("Qalqalah", QuranTextFormatter.TAJWEED_QALQALAH_COLOR)
                        SettingsTajweedLegendChip("Iqlab", QuranTextFormatter.TAJWEED_IQLAB_COLOR)
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                    Surface(
                        onClick = { showWaqfGuideDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppDimensions.cornerMediumSmall),
                        color = DesertOchreLight.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(
                            AppDimensions.borderWidthHairline,
                            DesertOchre.copy(alpha = 0.45f)
                        )
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
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = DesertOchre,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
                            AppText.BodySmall(
                                text = "View Tajweed & Waqf Stopping Signs Guide",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Verse & Translation Font Size Sliders with Live Bismillah Sample
        item {
            IslamicCard(
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
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = DesertOchre,
                                modifier = Modifier.size(AppDimensions.iconRegular)
                            )
                            AppText.BodyMedium(
                                text = "Verse (Arabic) Font Size",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            color = DesertOchreLight,
                            shape = RoundedCornerShape(AppDimensions.cornerExtraSmall)
                        ) {
                            AppText.BodySmall(
                                text = "${state.arabicFontSize.toInt()} sp",
                                fontWeight = FontWeight.Bold,
                                color = DesertOchre,
                                modifier = Modifier.padding(
                                    horizontal = AppDimensions.paddingSmall,
                                    vertical = AppDimensions.paddingMicro
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextDecrease,
                            contentDescription = "Smaller Verse Text",
                            tint = TextSecondary,
                            modifier = Modifier
                                .size(AppDimensions.iconRegular)
                                .clickable { onSetArabicFontSize(state.arabicFontSize - 1f) }
                        )
                        Slider(
                            value = state.arabicFontSize,
                            onValueChange = { onSetArabicFontSize(it) },
                            valueRange = 18f..44f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = DesertOchre,
                                activeTrackColor = DesertOchre,
                                inactiveTrackColor = DesertOchreLight
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.TextIncrease,
                            contentDescription = "Larger Verse Text",
                            tint = DesertOchre,
                            modifier = Modifier
                                .size(AppDimensions.iconMedium)
                                .clickable { onSetArabicFontSize(state.arabicFontSize + 1f) }
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                            .background(WarmParchment.copy(alpha = 0.7f))
                            .border(
                                AppDimensions.borderWidthHairline,
                                DesertOchre.copy(alpha = 0.35f),
                                RoundedCornerShape(AppDimensions.cornerMedium)
                            )
                            .padding(
                                horizontal = AppDimensions.paddingMediumSmall,
                                vertical = AppDimensions.paddingRegular
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText.ArabicText(
                            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                            fontSize = state.arabicFontSize.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            fontKeyOverride = state.selectedQuranFont,
                            applySafePadding = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
                    HorizontalDivider(
                        color = DividerColor.copy(alpha = 0.6f),
                        thickness = AppDimensions.dividerHairline
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

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
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconRegular)
                            )
                            AppText.BodyMedium(
                                text = "Translation Font Size",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            color = MedinaSageLight,
                            shape = RoundedCornerShape(AppDimensions.cornerExtraSmall)
                        ) {
                            AppText.BodySmall(
                                text = "${state.translationFontSize.toInt()} sp",
                                fontWeight = FontWeight.Bold,
                                color = MedinaSage,
                                modifier = Modifier.padding(
                                    horizontal = AppDimensions.paddingSmall,
                                    vertical = AppDimensions.paddingMicro
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextDecrease,
                            contentDescription = "Smaller Translation Text",
                            tint = TextSecondary,
                            modifier = Modifier
                                .size(AppDimensions.iconRegular)
                                .clickable { onSetTranslationFontSize(state.translationFontSize - 1f) }
                        )
                        Slider(
                            value = state.translationFontSize,
                            onValueChange = { onSetTranslationFontSize(it) },
                            valueRange = 12f..26f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = MedinaSage,
                                activeTrackColor = MedinaSage,
                                inactiveTrackColor = MedinaSageLight
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.TextIncrease,
                            contentDescription = "Larger Translation Text",
                            tint = MedinaSage,
                            modifier = Modifier
                                .size(AppDimensions.iconMedium)
                                .clickable { onSetTranslationFontSize(state.translationFontSize + 1f) }
                        )
                    }

                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                            .background(WarmParchment.copy(alpha = 0.55f))
                            .border(
                                AppDimensions.borderWidthHairline,
                                MedinaSage.copy(alpha = 0.25f),
                                RoundedCornerShape(AppDimensions.cornerMedium)
                            )
                            .padding(
                                horizontal = AppDimensions.paddingMediumSmall,
                                vertical = AppDimensions.paddingRegular
                            ),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                    ) {
                        AppText.UrduText(
                            text = "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے",
                            fontSize = (state.translationFontSize * 1.15f).sp,
                            lineHeight = (state.translationFontSize * 2.15f).sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        AppText.BodyMedium(
                            text = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                            fontSizeOverride = state.translationFontSize.sp,
                            lineHeightOverride = (state.translationFontSize * 1.45f).sp,
                            color = TextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Section 4: Translations & Displays
        item {
            IslamicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PureAlabaster,
                borderColor = DividerColor,
                borderWidth = AppDimensions.borderWidthHairline,
                cornerRadius = AppDimensions.cornerLarge,
                contentPadding = AppDimensions.paddingMediumSmall
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconRegular)
                        )
                        AppText.BodyMedium(
                            text = "Translations & Displays",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    HorizontalDivider(
                        color = DividerColor.copy(alpha = 0.5f),
                        thickness = AppDimensions.dividerHairline
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            AppText.BodyMedium(
                                text = "Urdu Translation",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            AppText.Caption(
                                text = "Tafheem-ul-Quran (Nastaleeq script)",
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = state.showUrduTranslation,
                            onCheckedChange = { onToggleUrduTranslation() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                        )
                    }

                    HorizontalDivider(
                        color = DividerColor.copy(alpha = 0.5f),
                        thickness = AppDimensions.dividerHairline
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            AppText.BodyMedium(
                                text = "English Translation",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            AppText.Caption(
                                text = "Saheeh International commentary",
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = state.showTranslation,
                            onCheckedChange = { onToggleTranslation() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                        )
                    }

                    HorizontalDivider(
                        color = DividerColor.copy(alpha = 0.5f),
                        thickness = AppDimensions.dividerHairline
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            AppText.BodyMedium(
                                text = "English Transliteration",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            AppText.Caption(
                                text = "Phonetic Latin characters",
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = state.showTransliteration,
                            onCheckedChange = { onToggleTransliteration() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                        )
                    }
                }
            }
        }

        // Section 5: Recitation Controls (Auto-Play & Auto-Scroll)
        item {
            IslamicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PureAlabaster,
                borderColor = DividerColor,
                borderWidth = AppDimensions.borderWidthHairline,
                cornerRadius = AppDimensions.cornerLarge,
                contentPadding = AppDimensions.paddingMediumSmall
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleOutline,
                                contentDescription = null,
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconMedium)
                            )
                            Column {
                                AppText.BodyMedium(
                                    text = "Continuous Recitation",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                AppText.Caption(
                                    text = "Automatically proceed to next Ayah audio",
                                    color = TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = state.autoPlayNext,
                            onCheckedChange = { onToggleAutoPlay() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                        )
                    }

                    HorizontalDivider(
                        color = DividerColor.copy(alpha = 0.5f),
                        thickness = AppDimensions.dividerHairline
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerticalAlignBottom,
                                contentDescription = null,
                                tint = DesertOchre,
                                modifier = Modifier.size(AppDimensions.iconMedium)
                            )
                            Column {
                                AppText.BodyMedium(
                                    text = "Auto-Scroll During Recitation",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                AppText.Caption(
                                    text = "Keep active verse centered on screen",
                                    color = TextSecondary
                                )
                            }
                        }
                        Switch(
                            checked = state.isAutoScrollEnabled,
                            onCheckedChange = { onToggleAutoScroll() },
                            colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                        )
                    }
                }
            }
        }

        // Section: Offline Audio & Storage
        item {
            IslamicCard(
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
                                text = "Offline Audio Storage",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        val totalStorageMb = QuranAudioDownloadManager.getTotalAudioStorageMb(context)
                        AppText.Caption(
                            text = String.format("%.1f MB used", totalStorageMb),
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

                        Surface(
                            color = WarmParchment,
                            shape = RoundedCornerShape(AppDimensions.cornerMedium),
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
                                        text = if (isJuz) "Juz ${state.selectedJuz!!.number} (${state.selectedJuz.englishName}) Audio" else "Surah ${surah.name} Audio",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    AppText.Caption(
                                        text = if (state.isCurrentSurahDownloaded) "Downloaded for offline recitation"
                                        else if (isDownloadingThis) "Downloading: ${state.downloadStatus.progressPercentage}%"
                                        else "${state.verses.size.takeIf { it > 0 } ?: surah.totalVerses} Ayahs available to download",
                                        fontSizeOverride = AppDimensions.textMicro,
                                        color = TextSecondary
                                    )
                                }

                                if (isDownloadingThis) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(AppDimensions.iconMedium),
                                        color = MedinaSage,
                                        strokeWidth = AppDimensions.borderWidthThick
                                    )
                                } else if (state.isCurrentSurahDownloaded) {
                                    IconButton(
                                        onClick = onDeleteSurahAudio,
                                        modifier = Modifier.size(AppDimensions.cornerPill)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Surah Audio",
                                            tint = DesertOchre,
                                            modifier = Modifier.size(AppDimensions.iconRegular)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = onDownloadSurah,
                                        modifier = Modifier.size(AppDimensions.cornerPill)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Download Surah Audio",
                                            tint = MedinaSage,
                                            modifier = Modifier.size(AppDimensions.iconRegular)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    }

                    Surface(
                        onClick = {
                            QuranAudioDownloadManager.deleteAllAudio(context)
                            onClearAllAudio()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppDimensions.cornerMediumSmall),
                        color = PureAlabaster,
                        border = androidx.compose.foundation.BorderStroke(
                            AppDimensions.borderWidthHairline,
                            DividerColor
                        )
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
                                text = "Clear All Offline Audio Cache",
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumLarge))
        }
    }
}

@Composable
private fun SettingsTajweedLegendChip(
    label: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(AppDimensions.cornerSmall))
            .background(WarmParchment)
            .border(
                AppDimensions.borderWidthHairline,
                color.copy(alpha = 0.45f),
                RoundedCornerShape(AppDimensions.cornerSmall)
            )
            .padding(
                horizontal = AppDimensions.paddingSmall,
                vertical = AppDimensions.paddingTiny
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
    ) {
        Box(
            modifier = Modifier
                .size(AppDimensions.paddingSmall)
                .clip(CircleShape)
                .background(color)
        )
        AppText.Caption(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}
