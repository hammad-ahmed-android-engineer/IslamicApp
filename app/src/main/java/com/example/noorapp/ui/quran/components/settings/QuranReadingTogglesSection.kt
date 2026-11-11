package com.example.noorapp.ui.quran.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppDivider
import com.example.noorapp.core.designsystem.AppSwitch
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary

@Composable
fun QuranReadingTogglesCard(
    showUrduTranslation: Boolean,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    onToggleUrduTranslation: () -> Unit,
    onToggleTranslation: () -> Unit,
    onToggleTransliteration: () -> Unit
) {
    AppCard(
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
                    text = stringResource(R.string.quran_translations_displays_section),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            SettingsDivider()
            SettingsToggleRow(
                title = stringResource(R.string.quran_urdu_translation_title),
                subtitle = stringResource(R.string.quran_urdu_translation_subtitle),
                checked = showUrduTranslation,
                onCheckedChange = { onToggleUrduTranslation() }
            )
            SettingsDivider()
            SettingsToggleRow(
                title = stringResource(R.string.quran_english_translation_title),
                subtitle = stringResource(R.string.quran_english_translation_subtitle),
                checked = showTranslation,
                onCheckedChange = { onToggleTranslation() }
            )
            SettingsDivider()
            SettingsToggleRow(
                title = stringResource(R.string.quran_english_transliteration_title),
                subtitle = stringResource(R.string.quran_english_transliteration_subtitle),
                checked = showTransliteration,
                onCheckedChange = { onToggleTransliteration() }
            )
        }
    }
}

@Composable
fun QuranPlaybackControlsCard(
    autoPlayNext: Boolean,
    isAutoScrollEnabled: Boolean,
    onToggleAutoPlay: () -> Unit,
    onToggleAutoScroll: () -> Unit
) {
    AppCard(
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
                            text = stringResource(R.string.quran_continuous_recitation_title),
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        AppText.Caption(
                            text = stringResource(R.string.quran_continuous_recitation_subtitle),
                            color = TextSecondary
                        )
                    }
                }
                AppSwitch(
                    checked = autoPlayNext,
                    onCheckedChange = { onToggleAutoPlay() }
                )
            }

            SettingsDivider()

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
                            text = stringResource(R.string.quran_autoscroll_recitation_title),
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        AppText.Caption(
                            text = stringResource(R.string.quran_autoscroll_recitation_subtitle),
                            color = TextSecondary
                        )
                    }
                }
                AppSwitch(
                    checked = isAutoScrollEnabled,
                    onCheckedChange = { onToggleAutoScroll() }
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AppText.BodyMedium(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            AppText.Caption(
                text = subtitle,
                color = TextSecondary
            )
        }
        AppSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsDivider() {
    AppDivider(
        color = DividerColor.copy(alpha = 0.5f),
        thickness = AppDimensions.dividerHairline
    )
}

