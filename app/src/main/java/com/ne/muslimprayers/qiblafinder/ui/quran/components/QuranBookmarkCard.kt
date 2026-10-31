package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.quran.BookmarkItem

@Composable
fun QuranBookmarkCard(
    bookmark: BookmarkItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, DividerColor, RoundedCornerShape(14.sdp)),
        colors = CardDefaults.cardColors(containerColor = PureAlabaster),
        shape = RoundedCornerShape(14.sdp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.sdp)
                ) {
                    Surface(
                        color = MedinaSageLight,
                        shape = RoundedCornerShape(6.sdp)
                    ) {
                        Text(
                            text = "‹ ${bookmark.surahNumber}.${bookmark.ayahNumber} ›",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage,
                            modifier = Modifier.padding(horizontal = 6.sdp, vertical = 2.sdp)
                        )
                    }
                    Text(
                        text = "Surah ${bookmark.surahName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(6.sdp))
                Text(
                    text = bookmark.arabicSnippet,
                    fontSize = 13.sp,
                    color = MedinaSage,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.sdp))
                Text(
                    text = bookmark.englishSnippet,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove Bookmark",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.sdp)
                )
            }
        }
    }
}
