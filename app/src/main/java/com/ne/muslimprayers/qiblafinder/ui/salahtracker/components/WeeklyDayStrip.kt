package com.ne.muslimprayers.qiblafinder.ui.salahtracker.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.theme.ssp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.salahtracker.DayTrackerUiModel

@Composable
fun WeeklyDayStrip(
    days: List<DayTrackerUiModel>,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.sdp)
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

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.sdp))
            .clickableDebounced { onClick() }
            .border(
                width = if (day.isSelected) 2.sdp else 1.sdp,
                color = if (day.isSelected) MedinaSage else if (day.isToday) DesertOchre else DividerColor,
                shape = RoundedCornerShape(16.sdp)
            ),
        shape = RoundedCornerShape(16.sdp),
        color = backgroundColor,
        tonalElevation = if (day.isSelected) 4.sdp else 1.sdp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.sdp, horizontal = 2.sdp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Day Name (e.g. Mon)
            Text(
                text = day.dayName,
                fontSize = 11.ssp,
                fontWeight = if (day.isSelected) FontWeight.Bold else FontWeight.Medium,
                color = subTextColor
            )

            Spacer(modifier = Modifier.height(4.sdp))

            // Day Number (e.g. 24)
            Text(
                text = "${day.dayNumber}",
                fontSize = 15.ssp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(6.sdp))

            // Completion badge indicator
            Box(
                modifier = Modifier
                    .size(18.sdp)
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
                        contentDescription = "Completed",
                        tint = if (day.isSelected) MedinaSage else PureAlabaster,
                        modifier = Modifier.size(12.sdp)
                    )
                } else if (day.completedCount > 0) {
                    Text(
                        text = "${day.completedCount}",
                        fontSize = 9.ssp,
                        fontWeight = FontWeight.Bold,
                        color = if (day.isSelected) PureAlabaster else PureAlabaster
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(5.sdp)
                            .clip(CircleShape)
                            .background(if (day.isSelected) PureAlabaster.copy(alpha = 0.5f) else TextSecondary.copy(alpha = 0.3f))
                    )
                }
            }

            // Today indicator dot under badge
            if (day.isToday) {
                Spacer(modifier = Modifier.height(3.sdp))
                Box(
                    modifier = Modifier
                        .size(4.sdp)
                        .clip(CircleShape)
                        .background(if (day.isSelected) DesertOchre else DesertOchre)
                )
            }
        }
    }
}
