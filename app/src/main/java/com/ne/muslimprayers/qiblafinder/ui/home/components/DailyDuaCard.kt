package com.ne.muslimprayers.qiblafinder.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun DailyDuaCard(
    dua: DuaItem?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    IslamicCard(
        backgroundColor = PureAlabaster,
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.iconButtonCompact)
                            .clip(CircleShape)
                            .background(DesertOchreLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = DesertOchre,
                            modifier = Modifier.size(AppDimensions.iconRegular)
                        )
                    }
                    Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))
                    Column {
                        AppText.TitleMedium(
                            text = "Featured Daily Dua",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        AppText.Caption(
                            text = "دعاء اليوم المأثور",
                            color = TextSecondary
                        )
                    }
                }

                // Copy and Share actions
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)) {
                    if (dua != null) {
                        Box(
                            modifier = Modifier
                                .size(AppDimensions.actionCircleSize)
                                .clip(CircleShape)
                                .background(MedinaSageLight)
                                .clickable {
                                    val text = "${dua.arabicText}\n\n\"${dua.translation}\""
                                    com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(context, "Dua", text, "Dua copied to clipboard")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Dua",
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(AppDimensions.actionCircleSize)
                                .clip(CircleShape)
                                .background(DesertOchreLight)
                                .clickable {
                                    val text = "${dua.arabicText}\n\n\"${dua.translation}\""
                                    com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(context, text, "Share Dua via")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Dua",
                                tint = DesertOchre,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            if (dua != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppDimensions.cornerMediumLarge))
                        .background(MedinaSageLight.copy(alpha = 0.35f))
                        .border(AppDimensions.borderWidthHairline, DesertOchre.copy(alpha = 0.3f), RoundedCornerShape(AppDimensions.cornerMediumLarge))
                        .padding(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingExtraSmall)
                ) {
                    AppText.ArabicText(
                        text = dua.arabicText,
                        color = MedinaSage,
                        fontSize = AppDimensions.textArabicListTitle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))
                AppText.BodyMedium(
                    text = "\"${dua.translation}\"",
                    color = TextPrimary,
                    maxLines = 4
                )
            } else {
                AppText.BodyMedium(
                    text = "Loading daily supplication...",
                    color = TextSecondary
                )
            }
        }
    }
}
