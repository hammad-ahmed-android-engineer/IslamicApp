package com.example.noorapp.ui.quran.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppButton
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCircularProgress
import com.example.noorapp.core.designsystem.AppFilterChip
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.ui.quran.AVAILABLE_TAFSEERS
import com.example.noorapp.ui.quran.TafseerOption

/**
 * Pure body composable for Ayah Tafseer commentary inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun AyahTafseerContent(
    selectedTafseerId: Int,
    isLoading: Boolean,
    tafseerContent: String,
    errorMessage: String?,
    isLandscape: Boolean,
    options: List<TafseerOption> = AVAILABLE_TAFSEERS,
    onSelectTafseer: (Int) -> Unit,
    onRetry: () -> Unit
) {
    val selectedOption = options.firstOrNull { it.id == selectedTafseerId } ?: options.first()

    AppText.Caption(
        text = stringResource(R.string.quran_commentary_scholar_label),
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary
    )

    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
    ) {
        options.forEach { option ->
            val isSelected = option.id == selectedTafseerId
            AppFilterChip(
                selected = isSelected,
                onClick = { onSelectTafseer(option.id) },
                selectedContainerColor = MedinaSage,
                selectedLabelColor = PureAlabaster,
                label = {
                    AppText.Caption(
                        text = stringResource(
                            R.string.quran_tafseer_chip_format,
                            option.name,
                            option.languageLabel
                        ),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = AppDimensions.dialogMinHeight,
                max = if (isLandscape) AppDimensions.dialogContentMaxHeightCompact else AppDimensions.dialogContentMaxHeightExpanded
            ),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        borderWidth = AppDimensions.borderWidthHairline,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingZero
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.paddingLarge),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppCircularProgress(
                        color = MedinaSage,
                        modifier = Modifier.size(AppDimensions.paddingExtraLarge)
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    AppText.Caption(
                        text = stringResource(R.string.quran_loading_tafseer_format, selectedOption.name),
                        color = TextSecondary
                    )
                }
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.paddingMedium),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppText.BodyMedium(
                        text = stringResource(R.string.quran_failed_load_tafseer),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                    AppButton(onClick = onRetry) {
                        AppText.ButtonText(
                            text = stringResource(R.string.retry),
                            color = PureAlabaster
                        )
                    }
                }
            }
        } else {
            val resolvedText = tafseerContent.ifBlank {
                stringResource(R.string.quran_no_tafseer_available)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.paddingMediumSmall),
                contentPadding = PaddingValues(bottom = AppDimensions.paddingMediumSmall)
            ) {
                item {
                    AppText.Caption(
                        text = stringResource(
                            R.string.quran_tafseer_author_format,
                            selectedOption.name,
                            selectedOption.author
                        ),
                        color = DesertOchre,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    when (selectedOption.language) {
                        "ur" -> AppText.UrduText(
                            text = resolvedText,
                            fontSize = AppDimensions.textArabicPreview,
                            color = TextPrimary,
                            modifier = Modifier.padding(vertical = AppDimensions.paddingExtraSmall)
                        )
                        "ar" -> AppText.ArabicText(
                            text = resolvedText,
                            fontSize = AppDimensions.textArabicPreview,
                            color = TextPrimary,
                            applySafePadding = false
                        )
                        else -> AppText.BodyMedium(
                            text = resolvedText,
                            fontSizeOverride = AppDimensions.textBodyMedium,
                            color = TextPrimary,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

