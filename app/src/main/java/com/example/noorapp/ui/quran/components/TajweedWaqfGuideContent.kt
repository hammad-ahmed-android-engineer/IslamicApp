package com.example.noorapp.ui.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.ui.quran.util.QuranTextFormatter

/**
 * Pure list content for the Tajweed & Waqf pause signs guide inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun TajweedWaqfGuideContent(
    isLandscape: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                max = if (isLandscape) {
                    AppDimensions.dialogContentMaxHeightCompact
                } else {
                    AppDimensions.dialogContentMaxHeightExpanded
                }
            ),
        contentPadding = PaddingValues(vertical = AppDimensions.paddingTiny),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
    ) {
        items(QuranTextFormatter.WAQF_SIGNS_CATALOG) { item ->
            WaqfSignCard(item)
        }
    }
}

@Composable
private fun WaqfSignCard(item: QuranTextFormatter.WaqfSignInfo) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingMediumSmall
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Symbol Circle + Title & Arabic Name on left, Rule pill on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.avatarSize)
                            .clip(CircleShape)
                            .background(DesertOchreLight)
                            .border(AppDimensions.borderWidthThin, DesertOchre.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText.ArabicText(
                            text = item.symbol,
                            fontSize = AppDimensions.textHeadingMedium,
                            fontWeight = FontWeight.Bold,
                            color = DesertOchre,
                            textAlign = TextAlign.Center,
                            applySafePadding = false
                        )
                    }

                    Column {
                        AppText.BodyMedium(
                            text = stringResource(item.englishNameRes),
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        AppText.Caption(
                            text = stringResource(item.arabicNameRes),
                            color = MedinaSage,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                        .background(MedinaSageLight)
                        .padding(
                            horizontal = AppDimensions.paddingSmall,
                            vertical = AppDimensions.paddingTiny
                        )
                ) {
                    AppText.Caption(
                        text = stringResource(item.ruleRes),
                        fontWeight = FontWeight.Bold,
                        color = MedinaSage
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            AppText.BodySmall(
                text = stringResource(item.descriptionRes),
                color = TextSecondary
            )
        }
    }
}

