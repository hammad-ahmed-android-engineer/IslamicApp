package com.example.noorapp.ui.quran.components.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppSelectableCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.ui.quran.util.QuranTextFormatter

data class FontScriptOption(
    val key: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int
)

val SCRIPT_OPTIONS = listOf(
    FontScriptOption("muhammadi", R.string.quran_font_muhammadi_title, R.string.quran_font_muhammadi_sub),
    FontScriptOption("hafs", R.string.quran_font_hafs_title, R.string.quran_font_hafs_sub),
    FontScriptOption("al_majeed", R.string.quran_font_al_majeed_title, R.string.quran_font_al_majeed_sub),
    FontScriptOption("al_mushaf", R.string.quran_font_al_mushaf_title, R.string.quran_font_al_mushaf_sub),
    FontScriptOption("al_qalam", R.string.quran_font_al_qalam_title, R.string.quran_font_al_qalam_sub),
    FontScriptOption("nabi", R.string.quran_font_nabi_title, R.string.quran_font_nabi_sub),
    FontScriptOption("pdms_saleem", R.string.quran_font_pdms_title, R.string.quran_font_pdms_sub)
)

@Composable
fun QuranScriptFontCard(
    selectedQuranFont: String,
    onSetQuranFont: (String) -> Unit
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
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconRegular)
                )
                AppText.BodyMedium(
                    text = stringResource(R.string.quran_font_settings),
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
                            val isSelected = selectedQuranFont == opt.key ||
                                (selectedQuranFont == "indopak" && opt.key == "muhammadi") ||
                                (selectedQuranFont == "uthmani" && opt.key == "hafs") ||
                                (selectedQuranFont == "pdms" && opt.key == "pdms_saleem")
                            AppSelectableCard(
                                isSelected = isSelected,
                                onClick = { onSetQuranFont(opt.key) },
                                modifier = Modifier.weight(1f),
                                selectedBackgroundColor = MedinaSageLight,
                                unselectedBackgroundColor = PureAlabaster,
                                selectedBorderColor = MedinaSage,
                                unselectedBorderColor = DividerColor,
                                borderWidth = if (isSelected) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
                                cornerRadius = AppDimensions.cornerMedium,
                                contentPadding = AppDimensions.paddingZero,
                                elevation = AppDimensions.elevationNone
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
                                            text = stringResource(opt.titleRes),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) MedinaSage else TextPrimary,
                                            maxLines = 1
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = stringResource(R.string.quran_selected_desc),
                                                tint = MedinaSage,
                                                modifier = Modifier.size(AppDimensions.iconSmall)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                    AppText.Caption(
                                        text = stringResource(opt.subtitleRes),
                                        fontSizeOverride = AppDimensions.textMicro,
                                        color = if (isSelected) MedinaSage else TextSecondary,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                                    AppText.ArabicText(
                                        text = stringResource(R.string.quran_bismillah_short_arabic),
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

@Composable
fun QuranColorModeCard(
    isColoredTajweedQuran: Boolean,
    onToggleColoredTajweedQuran: (Boolean) -> Unit,
    onOpenWaqfGuide: () -> Unit
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
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = null,
                    tint = DesertOchre,
                    modifier = Modifier.size(AppDimensions.iconRegular)
                )
                AppText.BodyMedium(
                    text = stringResource(R.string.quran_mushaf_color_mode),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                AppSelectableCard(
                    isSelected = isColoredTajweedQuran,
                    onClick = { onToggleColoredTajweedQuran(true) },
                    modifier = Modifier.weight(1f),
                    selectedBackgroundColor = MedinaSageLight,
                    unselectedBackgroundColor = PureAlabaster,
                    selectedBorderColor = MedinaSage,
                    unselectedBorderColor = DividerColor,
                    borderWidth = if (isColoredTajweedQuran) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
                    cornerRadius = AppDimensions.cornerMedium,
                    contentPadding = AppDimensions.paddingZero,
                    elevation = AppDimensions.elevationNone
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
                            text = stringResource(R.string.quran_colored_mode_title),
                            fontWeight = FontWeight.Bold,
                            color = if (isColoredTajweedQuran) MedinaSage else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                        AppText.Caption(
                            text = stringResource(R.string.quran_colored_mode_subtitle),
                            fontSizeOverride = AppDimensions.textMicro,
                            color = if (isColoredTajweedQuran) MedinaSage else TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                AppSelectableCard(
                    isSelected = !isColoredTajweedQuran,
                    onClick = { onToggleColoredTajweedQuran(false) },
                    modifier = Modifier.weight(1f),
                    selectedBackgroundColor = MedinaSageLight,
                    unselectedBackgroundColor = PureAlabaster,
                    selectedBorderColor = MedinaSage,
                    unselectedBorderColor = DividerColor,
                    borderWidth = if (!isColoredTajweedQuran) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
                    cornerRadius = AppDimensions.cornerMedium,
                    contentPadding = AppDimensions.paddingZero,
                    elevation = AppDimensions.elevationNone
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
                            text = stringResource(R.string.quran_plain_mode_title),
                            fontWeight = FontWeight.Bold,
                            color = if (!isColoredTajweedQuran) MedinaSage else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                        AppText.Caption(
                            text = stringResource(R.string.quran_plain_mode_subtitle),
                            fontSizeOverride = AppDimensions.textMicro,
                            color = if (!isColoredTajweedQuran) MedinaSage else TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsTajweedLegendChip(stringResource(R.string.quran_legend_waqf), QuranTextFormatter.TAJWEED_WAQF_COLOR)
                SettingsTajweedLegendChip(stringResource(R.string.quran_legend_madd), QuranTextFormatter.TAJWEED_MADD_COLOR)
                SettingsTajweedLegendChip(stringResource(R.string.quran_legend_ghunnah), QuranTextFormatter.TAJWEED_GHUNNAH_COLOR)
                SettingsTajweedLegendChip(stringResource(R.string.quran_legend_ikhfa_idgham), QuranTextFormatter.TAJWEED_IKHFA_COLOR)
                SettingsTajweedLegendChip(stringResource(R.string.quran_legend_qalqalah), QuranTextFormatter.TAJWEED_QALQALAH_COLOR)
                SettingsTajweedLegendChip(stringResource(R.string.quran_legend_iqlab), QuranTextFormatter.TAJWEED_IQLAB_COLOR)
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            AppCard(
                onClick = onOpenWaqfGuide,
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = AppDimensions.cornerMediumSmall,
                backgroundColor = DesertOchreLight.copy(alpha = 0.65f),
                borderColor = DesertOchre.copy(alpha = 0.45f),
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
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
                    AppText.BodySmall(
                        text = stringResource(R.string.quran_view_waqf_guide_btn),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
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

