package com.ne.muslimprayers.qiblafinder.ui.home.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun DailyHadithCard(
    hadith: Hadith?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    IslamicCard(
        backgroundColor = PureAlabaster,
        onClick = onClick,
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
                            .background(MedinaSageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconRegular)
                        )
                    }
                    Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))
                    Column {
                        AppText.TitleMedium(
                            text = "Hadith of the Day",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        AppText.Caption(
                            text = "حديث اليوم النبوي",
                            color = TextSecondary
                        )
                    }
                }

                // Action icons: Copy & Share
                Row(horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)) {
                    if (hadith != null) {
                        Box(
                            modifier = Modifier
                                .size(AppDimensions.actionCircleSize)
                                .clip(CircleShape)
                                .background(MedinaSageLight)
                                .clickable {
                                    val text = "\"${hadith.englishText}\"\n\n— ${hadith.source} (${hadith.chapter})"
                                    com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(context, "Hadith", text, "Hadith copied to clipboard")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Hadith",
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
                                    val text = "\"${hadith.englishText}\"\n\n— ${hadith.source} (${hadith.chapter})"
                                    com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(context, text, "Share Hadith via")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Hadith",
                                tint = DesertOchre,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            if (hadith != null) {
                AppText.BodyMedium(
                    text = "\"${hadith.englishText}\"",
                    color = TextPrimary,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText.BodySmall(
                        text = "— ${hadith.source} (${hadith.chapter})",
                        fontWeight = FontWeight.Bold,
                        color = MedinaSage,
                        maxLines = 1
                    )
                    AppText.Caption(
                        text = "Tap for full view",
                        color = DesertOchre,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                AppText.BodyMedium(
                    text = "Loading authentic Hadith...",
                    color = TextSecondary
                )
            }
        }
    }
}
