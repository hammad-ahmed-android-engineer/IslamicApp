package com.example.noorapp.ui.quran.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.domain.model.BookmarkItem

@Composable
fun QuranBookmarkCard(
    bookmark: BookmarkItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        cornerRadius = AppDimensions.cornerMediumLarge,
        contentPadding = AppDimensions.paddingMediumSmall,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    AppStatusPill(
                        text = stringResource(
                            R.string.quran_ayah_badge_format,
                            bookmark.surahNumber,
                            bookmark.ayahNumber
                        ),
                        textColor = MedinaSage,
                        backgroundColor = MedinaSageLight,
                        cornerRadius = AppDimensions.cornerExtraSmall
                    )
                    AppText.TitleSmall(
                        text = stringResource(R.string.quran_surah_name_format, bookmark.surahName),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                AppText.ArabicText(
                    text = bookmark.arabicSnippet,
                    color = MedinaSage,
                    fontSize = AppDimensions.textArabicPreview,
                    maxLines = 1,
                    applySafePadding = false
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                AppText.BodySmall(
                    text = bookmark.englishSnippet,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            AppIconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = stringResource(R.string.quran_remove_bookmark_desc),
                    tint = TextSecondary,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }
        }
    }
}
