package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun SurahListItemCard(
    surah: Surah,
    isDownloaded: Boolean = false,
    isDownloading: Boolean = false,
    onDownloadClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.sdp))
            .clickableDebounced { onClick() }
            .border(
                width = 1.sdp,
                color = DividerColor,
                shape = RoundedCornerShape(18.sdp)
            ),
        shape = RoundedCornerShape(18.sdp),
        color = PureAlabaster,
        tonalElevation = 1.sdp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.sdp)
                        .clip(CircleShape)
                        .background(DesertOchre),
                    contentAlignment = Alignment.Center
                ) {
                    AppText.TitleMedium(
                        text = "${surah.number}",
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                }

                Spacer(modifier = Modifier.width(12.sdp))

                Column(modifier = Modifier.weight(1f)) {
                    AppText.TitleMedium(
                        text = surah.name,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    AppText.BodySmall(
                        text = "${surah.englishMeaning} • ${surah.totalVerses} Ayahs (${surah.revelationType})",
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.sdp),
                        color = MedinaSage,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.sdp))
                } else if (isDownloaded) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Downloaded Offline",
                        tint = MedinaSage,
                        modifier = Modifier.size(18.sdp)
                    )
                    Spacer(modifier = Modifier.width(8.sdp))
                } else if (onDownloadClick != null) {
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier.size(28.sdp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Surah Audio",
                            tint = MedinaSage.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.sdp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.sdp))
                }

                AppText.HeadingLarge(
                    text = surah.arabicName,
                    color = MedinaSage,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
