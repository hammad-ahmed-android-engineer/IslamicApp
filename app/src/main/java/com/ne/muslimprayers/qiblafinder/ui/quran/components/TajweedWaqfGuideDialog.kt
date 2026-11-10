package com.ne.muslimprayers.qiblafinder.ui.quran.components

import android.content.res.Configuration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter

@Composable
fun TajweedWaqfGuideDialog(
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.85f else 0.94f)
                .clip(RoundedCornerShape(AppDimensions.cornerDialog))
                .border(
                    AppDimensions.borderWidthThin,
                    DividerColor,
                    RoundedCornerShape(AppDimensions.cornerDialog)
                ),
            color = WarmParchment,
            shape = RoundedCornerShape(AppDimensions.cornerDialog),
            tonalElevation = AppDimensions.elevationHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isLandscape) AppDimensions.paddingMedium else AppDimensions.paddingMediumLarge)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppDimensions.buttonSmallHeight)
                                .clip(CircleShape)
                                .background(MedinaSageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconMedium)
                            )
                        }

                        Column {
                            AppText.TitleMedium(
                                text = "Tajweed Pause Marks",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            AppText.Caption(
                                text = "علامات الوقف في القرآن الكريم",
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(AppDimensions.iconButtonCompact)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                AppText.BodySmall(
                    text = "Tajweed pause symbols are placed above words to guide the reciter where to stop or continue, ensuring the sacred meaning remains preserved.",
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                // Responsive List of Waqf Signs
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

                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MedinaSage),
                    shape = RoundedCornerShape(AppDimensions.cornerMediumLarge)
                ) {
                    AppText.ButtonText(
                        text = "Understood",
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                }
            }
        }
    }
}

@Composable
private fun WaqfSignCard(item: QuranTextFormatter.WaqfSignInfo) {
    IslamicCard(
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
                            text = item.englishName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        AppText.Caption(
                            text = item.arabicName,
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
                        text = item.rule,
                        fontWeight = FontWeight.Bold,
                        color = MedinaSage
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            AppText.BodySmall(
                text = item.description,
                color = TextSecondary
            )
        }
    }
}
