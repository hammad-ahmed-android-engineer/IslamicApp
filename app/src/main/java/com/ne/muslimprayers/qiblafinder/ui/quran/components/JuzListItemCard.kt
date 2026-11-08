package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.quran.JuzInfo

@Composable
fun JuzListItemCard(
    juz: JuzInfo,
    isDownloaded: Boolean = false,
    isDownloading: Boolean = false,
    onDownloadClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    IslamicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppDimensions.avatarSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    AppText.TitleMedium(
                        text = "${juz.number}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                Column(modifier = Modifier.weight(1f)) {
                    AppText.TitleMedium(
                        text = "Juz ${juz.number} - ${juz.englishName}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    AppText.BodySmall(
                        text = "Starts at Surah ${juz.startingSurahName} (${juz.startingSurahNumber})",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Only show download button if NOT already downloaded
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppDimensions.iconMedium),
                        color = MedinaSage,
                        strokeWidth = AppDimensions.borderWidthThick
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                } else if (!isDownloaded && onDownloadClick != null) {
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Para Audio",
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                    Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                }

                AppText.ArabicText(
                    text = juz.arabicName,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 19.sp,
                    maxLines = 1,
                    applySafePadding = false
                )
            }
        }
    }
}
