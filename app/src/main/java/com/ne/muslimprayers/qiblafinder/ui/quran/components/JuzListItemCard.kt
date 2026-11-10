package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
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
            // Left: Juz Number + Calligraphic Arabic Juz Name + English Name & Verses Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AppText.TitleMedium(
                    text = "${juz.number}",
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.widthIn(min = AppDimensions.minNumberWidth)
                )

                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                Box(
                    modifier = Modifier.widthIn(min = AppDimensions.arabicTitleMinWidth),
                    contentAlignment = Alignment.Center
                ) {
                    AppText.ArabicText(
                        text = juz.arabicName,
                        color = MedinaSage,
                        fontSize = AppDimensions.textArabicCardHero,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        applySafePadding = false
                    )
                }

                Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))

                Column(modifier = Modifier.weight(1f)) {
                    AppText.TitleMedium(
                        text = juz.englishName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    AppText.BodySmall(
                        text = "Verses ${juz.totalVerses}",
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Right: Cloud Download Button (hidden if already downloaded)
            if (isDownloading) {
                Box(
                    modifier = Modifier.size(AppDimensions.buttonSmallHeight),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppDimensions.iconMedium),
                        color = MedinaSage,
                        strokeWidth = AppDimensions.borderWidthThick
                    )
                }
            } else if (!isDownloaded && onDownloadClick != null) {
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CloudDownload,
                        contentDescription = "Download Para Audio",
                        tint = TextSecondary,
                        modifier = Modifier.size(AppDimensions.iconLarge)
                    )
                }
            }
        }
    }
}
