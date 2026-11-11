package com.example.noorapp.ui.quran.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppDivider
import com.example.noorapp.core.designsystem.AppSlider
import com.example.noorapp.core.designsystem.AppStatusPill
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

@Composable
fun QuranFontSizesCard(
    arabicFontSize: Float,
    translationFontSize: Float,
    selectedQuranFont: String,
    onSetArabicFontSize: (Float) -> Unit,
    onSetTranslationFontSize: (Float) -> Unit
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
            // Arabic Verse Size Controls
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
                        text = stringResource(R.string.quran_arabic_font_size),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                AppStatusPill(
                    text = stringResource(R.string.quran_font_size_sp_format, arabicFontSize.toInt()),
                    textColor = DesertOchre,
                    backgroundColor = DesertOchreLight,
                    cornerRadius = AppDimensions.cornerExtraSmall
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.TextDecrease,
                    contentDescription = stringResource(R.string.quran_smaller_verse_desc),
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(AppDimensions.iconRegular)
                        .clickable { onSetArabicFontSize(arabicFontSize - 1f) }
                )
                AppSlider(
                    value = arabicFontSize,
                    onValueChange = onSetArabicFontSize,
                    valueRange = 18f..44f,
                    modifier = Modifier.weight(1f),
                    thumbColor = DesertOchre,
                    activeTrackColor = DesertOchre,
                    inactiveTrackColor = DesertOchreLight
                )
                Icon(
                    imageVector = Icons.Default.TextIncrease,
                    contentDescription = stringResource(R.string.quran_larger_verse_desc),
                    tint = DesertOchre,
                    modifier = Modifier
                        .size(AppDimensions.iconMedium)
                        .clickable { onSetArabicFontSize(arabicFontSize + 1f) }
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
                    text = stringResource(R.string.quran_bismillah_arabic),
                    fontSize = AppDimensions.scaledSp(arabicFontSize),
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    fontKeyOverride = selectedQuranFont,
                    applySafePadding = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
            AppDivider(
                color = DividerColor.copy(alpha = 0.6f),
                thickness = AppDimensions.dividerHairline
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            // Translation Font Size Controls
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
                        text = stringResource(R.string.quran_translation_font_size),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                AppStatusPill(
                    text = stringResource(R.string.quran_font_size_sp_format, translationFontSize.toInt()),
                    textColor = MedinaSage,
                    backgroundColor = MedinaSageLight,
                    cornerRadius = AppDimensions.cornerExtraSmall
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.TextDecrease,
                    contentDescription = stringResource(R.string.quran_smaller_translation_desc),
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(AppDimensions.iconRegular)
                        .clickable { onSetTranslationFontSize(translationFontSize - 1f) }
                )
                AppSlider(
                    value = translationFontSize,
                    onValueChange = onSetTranslationFontSize,
                    valueRange = 12f..26f,
                    modifier = Modifier.weight(1f),
                    thumbColor = MedinaSage,
                    activeTrackColor = MedinaSage,
                    inactiveTrackColor = MedinaSageLight
                )
                Icon(
                    imageVector = Icons.Default.TextIncrease,
                    contentDescription = stringResource(R.string.quran_larger_translation_desc),
                    tint = MedinaSage,
                    modifier = Modifier
                        .size(AppDimensions.iconMedium)
                        .clickable { onSetTranslationFontSize(translationFontSize + 1f) }
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
                    text = stringResource(R.string.quran_bismillah_preview_urdu),
                    fontSize = AppDimensions.scaledSp(translationFontSize * 1.15f),
                    lineHeight = AppDimensions.scaledSp(translationFontSize * 2.15f),
                    color = TextPrimary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                AppText.BodyMedium(
                    text = stringResource(R.string.quran_bismillah_preview_english),
                    fontSizeOverride = AppDimensions.scaledSp(translationFontSize),
                    lineHeightOverride = AppDimensions.scaledSp(translationFontSize * 1.45f),
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

