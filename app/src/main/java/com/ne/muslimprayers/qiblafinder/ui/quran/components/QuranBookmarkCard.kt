package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.quran.BookmarkItem

@Composable
fun QuranBookmarkCard(
    bookmark: BookmarkItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    IslamicCard(
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
                    Surface(
                        color = MedinaSageLight,
                        shape = RoundedCornerShape(AppDimensions.cornerExtraSmall)
                    ) {
                        AppText.Caption(
                            text = "‹ ${bookmark.surahNumber}.${bookmark.ayahNumber} ›",
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage,
                            modifier = Modifier.padding(
                                horizontal = AppDimensions.paddingExtraSmall,
                                vertical = AppDimensions.paddingMicro
                            )
                        )
                    }
                    AppText.TitleSmall(
                        text = "Surah ${bookmark.surahName}",
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

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove Bookmark",
                    tint = TextSecondary,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }
        }
    }
}
