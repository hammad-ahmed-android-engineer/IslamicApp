package com.ne.muslimprayers.qiblafinder.ui.salahtracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun WeeklyNavigationHeader(
    weekRangeText: String,
    weekOffset: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onJumpToToday: () -> Unit
) {
    IslamicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        cornerRadius = AppDimensions.cornerLarge,
        elevation = AppDimensions.elevationSubtle,
        contentPadding = AppDimensions.paddingZero
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimensions.paddingRegular, vertical = AppDimensions.paddingExtraSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousWeek,
                modifier = Modifier
                    .size(AppDimensions.iconButtonCompact)
                    .clip(CircleShape)
                    .background(MedinaSageLight)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Week",
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconRegular)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                AppText.TitleMedium(
                    text = weekRangeText,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (weekOffset != 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                            .background(MedinaSage)
                            .clickableDebounced { onJumpToToday() }
                            .padding(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingTiny),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = PureAlabaster,
                                modifier = Modifier.size(AppDimensions.iconTiny)
                            )
                            AppText.LabelSmall(
                                text = "Today",
                                color = PureAlabaster,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onNextWeek,
                modifier = Modifier
                    .size(AppDimensions.iconButtonCompact)
                    .clip(CircleShape)
                    .background(MedinaSageLight)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Week",
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconRegular)
                )
            }
        }
    }
}
