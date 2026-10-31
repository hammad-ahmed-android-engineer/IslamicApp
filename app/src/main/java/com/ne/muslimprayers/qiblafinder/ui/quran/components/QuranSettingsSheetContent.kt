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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.quran.AVAILABLE_RECITERS
import com.ne.muslimprayers.qiblafinder.ui.quran.QuranUiState
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranAudioDownloadManager

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
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(WarmParchment),
        contentPadding = PaddingValues(horizontal = 20.sdp, vertical = 12.sdp),
        verticalArrangement = Arrangement.spacedBy(16.sdp)
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

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.sdp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.sdp)
                    )
                }
            }
        }

        // Section 1: Qir'ah & Audio Reciter Choice
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.sdp))
                    .background(PureAlabaster)
                    .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp))
                    .padding(14.sdp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = MedinaSage,
                        modifier = Modifier.size(18.sdp)
                    )
                    AppText.BodyMedium(
                        text = "Qari & Qir'ah / Riwayah",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.sdp))

                // Horizontally scrollable reciter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
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
                                Column(modifier = Modifier.padding(vertical = 2.sdp)) {
                                    Text(
                                        text = reciter.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                    )
                                    Text(
                                        text = reciter.riwayah,
                                        fontSize = 9.sp,
                                        color = if (isSelected) PureAlabaster.copy(alpha = 0.85f) else TextSecondary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // Section 2: Quranic Script & Calligraphy Font
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.sdp))
                    .background(PureAlabaster)
                    .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp))
                    .padding(14.sdp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = MedinaSage,
                        modifier = Modifier.size(18.sdp)
                    )
                    AppText.BodyMedium(
                        text = "Quranic Script & Font",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.sdp))

                // 2-column cards showing all 7 authentic fonts from fonts folder with live previews
                Column(verticalArrangement = Arrangement.spacedBy(8.sdp)) {
                    SCRIPT_OPTIONS.chunked(2).forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.sdp)
                        ) {
                            rowOptions.forEach { opt ->
                                val isSelected = state.selectedQuranFont == opt.key ||
                                        (state.selectedQuranFont == "indopak" && opt.key == "muhammadi") ||
                                        (state.selectedQuranFont == "uthmani" && opt.key == "hafs") ||
                                        (state.selectedQuranFont == "pdms" && opt.key == "pdms_saleem")
                                Surface(
                                    onClick = {
                                        onSetQuranFont(opt.key)
                                        com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager.setFont(context, opt.key)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.sdp),
                                    color = if (isSelected) MedinaSageLight else PureAlabaster,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 1.5.dp else 0.8.dp,
                                        color = if (isSelected) MedinaSage else DividerColor
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.sdp, horizontal = 10.sdp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = opt.title,
                                                fontSize = 12.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                color = if (isSelected) MedinaSage else TextPrimary,
                                                maxLines = 1
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = MedinaSage,
                                                    modifier = Modifier.size(16.sdp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.sdp))
                                        Text(
                                            text = opt.subtitle,
                                            fontSize = 10.sp,
                                            color = if (isSelected) MedinaSage else TextSecondary,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(6.sdp))
                                        // Live sample rendered in this specific font
                                        Text(
                                            text = "بِسْمِ اللَّهِ",
                                            fontFamily = com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager.getFontFamily(opt.key),
                                            fontSize = 17.sp,
                                            color = if (isSelected) MedinaSage else DesertOchre,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End,
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

        // Section: Mushaf Display Mode (Coloured Tajweed vs Plain Quran)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.sdp))
                    .background(PureAlabaster)
                    .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp))
                    .padding(14.sdp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = DesertOchre,
                        modifier = Modifier.size(18.sdp)
                    )
                    AppText.BodyMedium(
                        text = "Mushaf Color Mode",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.sdp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
                    // Coloured Quran Option
                    Surface(
                        onClick = { onToggleColoredTajweedQuran(true) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.sdp),
                        color = if (state.isColoredTajweedQuran) MedinaSageLight else PureAlabaster,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (state.isColoredTajweedQuran) 1.5.dp else 0.8.dp,
                            color = if (state.isColoredTajweedQuran) MedinaSage else DividerColor
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.sdp, horizontal = 8.sdp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎨 Coloured Quran",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.isColoredTajweedQuran) MedinaSage else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.sdp))
                            Text(
                                text = "Tajweed & Waqf rules",
                                fontSize = 10.sp,
                                color = if (state.isColoredTajweedQuran) MedinaSage else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    // Plain Quran Option
                    Surface(
                        onClick = { onToggleColoredTajweedQuran(false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.sdp),
                        color = if (!state.isColoredTajweedQuran) MedinaSageLight else PureAlabaster,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (!state.isColoredTajweedQuran) 1.5.dp else 0.8.dp,
                            color = if (!state.isColoredTajweedQuran) MedinaSage else DividerColor
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.sdp, horizontal = 8.sdp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📖 Plain Quran",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!state.isColoredTajweedQuran) MedinaSage else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.sdp))
                            Text(
                                text = "Classic monochrome",
                                fontSize = 10.sp,
                                color = if (!state.isColoredTajweedQuran) MedinaSage else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Arabic Calligraphy Font Size
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.sdp))
                    .background(PureAlabaster)
                    .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp))
                    .padding(14.sdp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.sdp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = DesertOchre,
                            modifier = Modifier.size(18.sdp)
                        )
                        AppText.BodyMedium(
                            text = "Arabic Font Size",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Size Badge
                    Surface(
                        color = DesertOchreLight,
                        shape = RoundedCornerShape(6.sdp)
                    ) {
                        Text(
                            text = "${state.arabicFontSize.toInt()} sp",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesertOchre,
                            modifier = Modifier.padding(horizontal = 8.sdp, vertical = 2.sdp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.sdp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { onAdjustFontSize(-2f) },
                        shape = RoundedCornerShape(10.sdp),
                        color = WarmParchment,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.sdp, vertical = 8.sdp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.sdp)
                        ) {
                            Icon(Icons.Default.TextDecrease, contentDescription = "Smaller", tint = TextPrimary)
                            Text("Smaller", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }

                    Surface(
                        onClick = { onAdjustFontSize(2f) },
                        shape = RoundedCornerShape(10.sdp),
                        color = WarmParchment,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.sdp, vertical = 8.sdp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.sdp)
                        ) {
                            Icon(Icons.Default.TextIncrease, contentDescription = "Larger", tint = TextPrimary)
                            Text("Larger", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Section 4: Translations & Displays
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.sdp))
                    .background(PureAlabaster)
                    .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp))
                    .padding(14.sdp),
                verticalArrangement = Arrangement.spacedBy(12.sdp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = MedinaSage,
                        modifier = Modifier.size(18.sdp)
                    )
                    AppText.BodyMedium(
                        text = "Translations & Displays",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.8.dp)

                // Urdu Translation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Urdu Translation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        Text("Tafheem-ul-Quran (Nastaleeq script)", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = state.showUrduTranslation,
                        onCheckedChange = { onToggleUrduTranslation() },
                        colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                    )
                }

                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.8.dp)

                // English Translation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("English Translation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        Text("Saheeh International commentary", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = state.showTranslation,
                        onCheckedChange = { onToggleTranslation() },
                        colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                    )
                }

                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.8.dp)

                // English Transliteration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("English Transliteration", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        Text("Phonetic Latin characters", fontSize = 11.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = state.showTransliteration,
                        onCheckedChange = { onToggleTransliteration() },
                        colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                    )
                }
            }
        }

        // Section 5: Recitation Controls (Auto-Play & Auto-Scroll)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.sdp))
                    .background(PureAlabaster)
                    .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp))
                    .padding(14.sdp),
                verticalArrangement = Arrangement.spacedBy(12.sdp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.sdp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleOutline,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(20.sdp)
                        )
                        Column {
                            Text("Continuous Recitation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text("Automatically proceed to next Ayah audio", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = state.autoPlayNext,
                        onCheckedChange = { onToggleAutoPlay() },
                        colors = SwitchDefaults.colors(checkedThumbColor = PureAlabaster, checkedTrackColor = MedinaSage)
                    )
                }

                HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), thickness = 0.8.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.sdp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerticalAlignBottom,
                            contentDescription = null,
                            tint = DesertOchre,
                            modifier = Modifier.size(20.sdp)
                        )
                        Column {
                            Text("Auto-Scroll During Recitation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text("Keep active verse centered on screen", fontSize = 11.sp, color = TextSecondary)
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

        // Section: Offline Audio & Storage
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.sdp))
                    .background(PureAlabaster)
                    .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp))
                    .padding(14.sdp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.sdp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(18.sdp)
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

                Spacer(modifier = Modifier.height(10.sdp))

                // If currently inside a Surah, offer quick download / status
                if (state.selectedSurah != null) {
                    val surah = state.selectedSurah
                    val isDownloadingThis = state.downloadStatus.isDownloading && state.downloadStatus.targetKey == "surah_${surah.number}"

                    Surface(
                        color = WarmParchment,
                        shape = RoundedCornerShape(12.sdp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.sdp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Surah ${surah.name} Audio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (state.isCurrentSurahDownloaded) "Downloaded for offline recitation"
                                    else if (isDownloadingThis) "Downloading: ${state.downloadStatus.progressPercentage}%"
                                    else "${surah.totalVerses} Ayahs available to download",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }

                            if (isDownloadingThis) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.sdp),
                                    color = MedinaSage,
                                    strokeWidth = 2.dp
                                )
                            } else if (state.isCurrentSurahDownloaded) {
                                IconButton(
                                    onClick = onDeleteSurahAudio,
                                    modifier = Modifier.size(28.sdp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Surah Audio",
                                        tint = DesertOchre,
                                        modifier = Modifier.size(18.sdp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = onDownloadSurah,
                                    modifier = Modifier.size(28.sdp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Download Surah Audio",
                                        tint = MedinaSage,
                                        modifier = Modifier.size(18.sdp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.sdp))
                }

                // Clear audio cache button
                Surface(
                    onClick = {
                        QuranAudioDownloadManager.deleteAllAudio(context)
                        onClearAllAudio()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.sdp),
                    color = PureAlabaster,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, DividerColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.sdp, horizontal = 12.sdp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.sdp)
                        )
                        Spacer(modifier = Modifier.width(6.sdp))
                        Text(
                            text = "Clear All Offline Audio Cache",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(20.sdp))
        }
    }
}
