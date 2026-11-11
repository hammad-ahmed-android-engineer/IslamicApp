package com.example.noorapp.ui.tasbih.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppSelectableCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.ui.quran.util.QuranTextFormatter

@Composable
fun DhikrPresetChip(
    title: String,
    arabic: String,
    target: Int,
    isSelected: Boolean,
    selectedQuranFont: String = ArabicFontManager.currentFontKey.value,
    onClick: () -> Unit
) {
    val formattedArabic = remember(arabic, selectedQuranFont) {
        QuranTextFormatter.formatArabicPhraseMultiColor(
            rawArabicText = arabic,
            isColored = false,
            fontKey = selectedQuranFont
        )
    }

    AppSelectableCard(
        isSelected = isSelected,
        onClick = onClick,
        selectedBackgroundColor = MedinaSage,
        unselectedBackgroundColor = PureAlabaster,
        selectedBorderColor = MedinaSage,
        unselectedBorderColor = DividerColor,
        borderWidth = AppDimensions.borderWidthThin,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingZero,
        elevation = AppDimensions.elevationNone
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = AppDimensions.paddingMediumSmall,
                vertical = AppDimensions.paddingSmall
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (arabic.isNotBlank()) {
                AppText.AnnotatedArabicText(
                    text = formattedArabic,
                    color = if (isSelected) PureAlabaster else TextPrimary,
                    fontSize = AppDimensions.textArabicPreview,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    fontKeyOverride = selectedQuranFont
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
            }
            AppText.BodyMedium(
                text = stringResource(R.string.tasbih_chip_format, title, target),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PureAlabaster else TextPrimary,
                maxLines = 1
            )
        }
    }
}
