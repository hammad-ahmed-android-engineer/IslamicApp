package com.example.noorapp.ui.hadith.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.domain.model.Hadith

/**
 * Pure body content for displaying a full Hadith inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun HadithDetailContent(
    hadith: Hadith
) {
    AppText.TitleMedium(
        text = hadith.chapter,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    if (hadith.arabicText.isNotBlank()) {
        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
        AppText.ArabicText(
            text = hadith.arabicText,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

    AppText.BodyLarge(
        text = hadith.englishText,
        color = MaterialTheme.colorScheme.onSurface
    )
}

