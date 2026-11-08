package com.ne.muslimprayers.qiblafinder.ui.ibadat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.AppTypography
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun DuaItemCard(
    dua: DuaItem,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    IslamicCard(
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppText.Caption(
                        text = dua.category,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                    AppText.TitleMedium(
                        text = dua.title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                }

                Row {
                    IconButton(onClick = onCopy) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                    .background(MedinaSageLight.copy(alpha = 0.35f))
                    .border(
                        AppDimensions.borderWidthThin,
                        DesertOchre.copy(alpha = 0.3f),
                        RoundedCornerShape(AppDimensions.cornerMedium)
                    )
                    .padding(AppDimensions.paddingSmall)
            ) {
                AppText.ArabicText(
                    text = dua.arabicText,
                    color = MedinaSage,
                    fontSize = AppTypography.QuranArabicMedium.fontSize,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (dua.transliteration.isNotBlank()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                AppText.BodySmall(
                    text = dua.transliteration,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            AppText.BodyMedium(
                text = "\"${dua.translation}\"",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (dua.reference.isNotBlank()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                AppText.Caption(
                    text = stringResource(R.string.duas_reference_label, dua.reference),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
