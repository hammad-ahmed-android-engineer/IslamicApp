package com.example.noorapp.ui.ibadat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppArabicScriptBox
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCopyShareActions
import com.example.noorapp.core.designsystem.AppDivider
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.domain.model.DuaItem
import com.example.noorapp.ui.quran.util.QuranTextFormatter

@Composable
fun DuaItemCard(
    dua: DuaItem,
    selectedQuranFont: String = ArabicFontManager.currentFontKey.value,
    arabicFontSize: Float = 26f,
    translationFontSize: Float = 15f,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val accent = remember(dua.category) { duaCategoryAccent(dua.category) }
    val formattedArabic = remember(dua.arabicText, selectedQuranFont) {
        QuranTextFormatter.formatDuaArabicText(
            rawArabicText = dua.arabicText,
            fontKey = selectedQuranFont
        )
    }
    val formattedTranslation = remember(dua.translation) {
        QuranTextFormatter.formatDuaTranslationText(dua.translation)
    }
    val formattedTransliteration = remember(dua.transliteration) {
        QuranTextFormatter.formatDuaTranslationText(dua.transliteration)
    }

    AppCard(
        backgroundColor = PureAlabaster,
        borderColor = accent.primary.copy(alpha = 0.28f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Category Header Strip + Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppStatusPill(
                        text = dua.category,
                        textColor = accent.primary,
                        backgroundColor = accent.container,
                        borderColor = accent.primary.copy(alpha = 0.35f)
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                    AppText.TitleMedium(
                        text = dua.title,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)) {
                    AppCopyShareActions(
                        copyDescription = stringResource(R.string.copy),
                        shareDescription = stringResource(R.string.share),
                        copyTint = accent.primary,
                        copyContainerColor = accent.container,
                        shareTint = accent.primary,
                        shareContainerColor = accent.container,
                        onCopy = onCopy,
                        onShare = onShare
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            // 2. Non-colored Quranic Arabic Script Container with same font, safe overhang padding & bracket handling
            AppArabicScriptBox(
                text = formattedArabic,
                fontSize = AppDimensions.scaledSp(arabicFontSize),
                fontKeyOverride = selectedQuranFont,
                borderColor = accent.primary.copy(alpha = 0.32f)
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
            AppDivider(
                color = DividerColor.copy(alpha = 0.55f),
                thickness = AppDimensions.dividerHairline
            )

            if (formattedTransliteration.isNotBlank()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                AppText.BodySmall(
                    text = formattedTransliteration,
                    fontWeight = FontWeight.SemiBold,
                    fontSizeOverride = AppDimensions.scaledSp(translationFontSize * 0.92f),
                    lineHeightOverride = AppDimensions.scaledSp(translationFontSize * 1.4f),
                    fontStyle = FontStyle.Italic,
                    color = accent.primary
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))

            AppText.BodyMedium(
                text = formattedTranslation,
                fontSizeOverride = AppDimensions.scaledSp(translationFontSize),
                lineHeightOverride = AppDimensions.scaledSp(translationFontSize * 1.5f),
                color = TextPrimary
            )

            if (dua.reference.isNotBlank()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                AppText.Caption(
                    text = stringResource(R.string.duas_reference_label, dua.reference),
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
