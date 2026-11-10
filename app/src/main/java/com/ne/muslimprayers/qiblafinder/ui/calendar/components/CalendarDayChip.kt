package com.ne.muslimprayers.qiblafinder.ui.calendar.components

import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.domain.model.IslamicCalendarInfo
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun CalendarDayChip(
    dayInfo: IslamicCalendarInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val hasHoliday = dayInfo.holidays.isNotEmpty()

    Surface(
        modifier = Modifier
            .width(AppDimensions.calendarDayChipWidth)
            .height(AppDimensions.calendarDayChipHeight)
            .clip(RoundedCornerShape(AppDimensions.cornerExtraLarge))
            .clickableDebounced { onClick() }
            .border(
                width = if (isSelected) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
                color = if (isSelected) MedinaSage else if (hasHoliday) DesertOchre else DividerColor,
                shape = RoundedCornerShape(AppDimensions.cornerExtraLarge)
            ),
        shape = RoundedCornerShape(AppDimensions.cornerExtraLarge),
        color = if (isSelected) MedinaSage else if (hasHoliday) MedinaSageLight else PureAlabaster,
        tonalElevation = AppDimensions.elevationSubtle
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.paddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AppText.BodySmall(
                text = dayInfo.weekday.take(3),
                color = if (isSelected) PureAlabaster.copy(alpha = 0.85f) else TextSecondary
            )

            AppText.HeadingLarge(
                text = "${dayInfo.gregorianDay}",
                color = if (isSelected) PureAlabaster else TextPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (hasHoliday) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Holiday",
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconTiny)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingMicro))
                }
                AppText.BodySmall(
                    text = "${dayInfo.hijriDay}",
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) DesertOchre else MedinaSage
                )
            }
        }
    }
}
