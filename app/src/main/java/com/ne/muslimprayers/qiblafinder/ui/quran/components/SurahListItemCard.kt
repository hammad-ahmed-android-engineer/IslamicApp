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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.domain.model.Surah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

private val FavoriteHeartRed = Color(0xFFD9383A)

@Composable
fun SurahListItemCard(
    surah: Surah,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
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
            // Left: Number + Calligraphic Arabic Surah Name + Verses Count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AppText.TitleMedium(
                    text = "${surah.number}",
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.widthIn(min = AppDimensions.minNumberWidth)
                )

                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                Column(modifier = Modifier.weight(1f)) {
                    AppText.ArabicText(
                        text = surah.arabicName,
                        color = MedinaSage,
                        fontSize = AppDimensions.textArabicCardHero,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        applySafePadding = false
                    )
                    AppText.BodySmall(
                        text = "Surah ${surah.name} • Verses ${surah.totalVerses}",
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Right: Favorite Heart Icon + Cloud Download Icon (hidden if already downloaded)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMicro)
            ) {
                if (onFavoriteClick != null) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Surah",
                            tint = FavoriteHeartRed,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }

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
                            contentDescription = "Download Surah Audio",
                            tint = TextSecondary,
                            modifier = Modifier.size(AppDimensions.iconLarge)
                        )
                    }
                }
            }
        }
    }
}
