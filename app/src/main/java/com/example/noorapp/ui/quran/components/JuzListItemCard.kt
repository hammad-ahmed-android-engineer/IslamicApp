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
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.ui.quran.JuzInfo

@Composable
fun JuzListItemCard(
    juz: JuzInfo,
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
                        text = stringResource(R.string.quran_juz_list_subtitle_format, juz.totalVerses),
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
                        contentDescription = stringResource(R.string.quran_download_juz_audio_desc),
                        tint = TextSecondary,
                        modifier = Modifier.size(AppDimensions.iconLarge)
                    )
                }
            }
        }
    }
}
