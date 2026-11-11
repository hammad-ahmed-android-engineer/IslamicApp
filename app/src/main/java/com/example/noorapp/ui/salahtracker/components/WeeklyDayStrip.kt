package com.example.noorapp.ui.salahtracker.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.example.noorapp.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.ui.salahtracker.DayTrackerUiModel

@Composable
fun WeeklyDayStrip(
    days: List<DayTrackerUiModel>,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
    ) {
        days.forEach { day ->
            DayItemCard(
                day = day,
                onClick = { onDaySelected(day.dateString) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DayItemCard(
    day: DayTrackerUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (day.isSelected) MedinaSage else PureAlabaster,
        label = "day_bg"
    )
    val textColor = if (day.isSelected) PureAlabaster else TextPrimary
    val subTextColor = if (day.isSelected) MedinaSageLight else TextSecondary

    AppCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = if (day.isSelected) MedinaSage else if (day.isToday) DesertOchre else DividerColor,
        borderWidth = if (day.isSelected) AppDimensions.borderWidthThick else AppDimensions.borderWidthThin,
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingTiny,
        elevation = if (day.isSelected) AppDimensions.elevationMedium else AppDimensions.elevationSubtle,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppDimensions.paddingExtraSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppText.Caption(
                text = day.dayName,
                fontWeight = if (day.isSelected) FontWeight.Bold else FontWeight.Medium,
                color = subTextColor
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))

            AppText.TitleSmall(
                text = "${day.dayNumber}",
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

            Box(
                modifier = Modifier
                    .size(AppDimensions.iconRegular)
                    .clip(CircleShape)
                    .background(
                        when {
                            day.isSelected && day.isAllCompleted -> PureAlabaster
                            day.isSelected -> MedinaSageLight.copy(alpha = 0.3f)
                            day.isAllCompleted -> MedinaSage
                            day.completedCount > 0 -> DesertOchre
                            else -> DividerColor.copy(alpha = 0.6f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (day.isAllCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.tracker_completed_desc),
                        tint = if (day.isSelected) MedinaSage else PureAlabaster,
                        modifier = Modifier.size(AppDimensions.iconTiny)
                    )
                } else if (day.completedCount > 0) {
                    AppText.Caption(
                        text = "${day.completedCount}",
                        fontSizeOverride = AppDimensions.textMicro,
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.paddingTiny)
                            .clip(CircleShape)
                            .background(
                                if (day.isSelected) PureAlabaster.copy(alpha = 0.5f)
                                else TextSecondary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            if (day.isToday) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                Box(
                    modifier = Modifier
                        .size(AppDimensions.paddingTiny)
                        .clip(CircleShape)
                        .background(DesertOchre)
                )
            }
        }
    }
}
