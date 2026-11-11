package com.example.noorapp.ui.salahtracker.components

import androidx.compose.foundation.background
import com.example.noorapp.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCircleIconButton
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary

@Composable
fun WeeklyNavigationHeader(
    weekRangeText: String,
    weekOffset: Int,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onJumpToToday: () -> Unit
) {
    AppCard(
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
            AppCircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.tracker_prev_week_desc),
                onClick = onPreviousWeek,
                size = AppDimensions.iconButtonCompact,
                iconSize = AppDimensions.iconRegular,
                tint = MedinaSage,
                containerColor = MedinaSageLight,
                borderColor = MedinaSageLight
            )

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
                                text = stringResource(R.string.tracker_today_btn),
                                color = PureAlabaster,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            AppCircleIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.tracker_next_week_desc),
                onClick = onNextWeek,
                size = AppDimensions.iconButtonCompact,
                iconSize = AppDimensions.iconRegular,
                tint = MedinaSage,
                containerColor = MedinaSageLight,
                borderColor = MedinaSageLight
            )
        }
    }
}
