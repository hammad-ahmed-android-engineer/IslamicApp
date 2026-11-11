package com.example.noorapp.ui.quran.components

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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCircularProgress
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.domain.model.Surah

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
    AppCard(
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
                        text = stringResource(R.string.quran_surah_list_subtitle_format, surah.name, surah.totalVerses),
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
                    AppIconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(R.string.quran_favorite_surah_desc),
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
                        AppCircularProgress(
                            modifier = Modifier.size(AppDimensions.iconMedium),
                            color = MedinaSage,
                            strokeWidth = AppDimensions.borderWidthThick
                        )
                    }
                } else if (!isDownloaded && onDownloadClick != null) {
                    AppIconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudDownload,
                            contentDescription = stringResource(R.string.quran_download_surah_audio_desc),
                            tint = TextSecondary,
                            modifier = Modifier.size(AppDimensions.iconLarge)
                        )
                    }
                }
            }
        }
    }
}
