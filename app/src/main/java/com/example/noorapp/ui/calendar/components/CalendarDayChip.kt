package com.example.noorapp.ui.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppSelectableCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.domain.model.IslamicCalendarInfo

@Composable
fun CalendarDayChip(
    dayInfo: IslamicCalendarInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val hasHoliday = dayInfo.holidays.isNotEmpty()

    AppSelectableCard(
        isSelected = isSelected,
        onClick = onClick,
        modifier = Modifier
            .width(AppDimensions.calendarDayChipWidth)
            .height(AppDimensions.calendarDayChipHeight),
        selectedBackgroundColor = MedinaSage,
        unselectedBackgroundColor = if (hasHoliday) MedinaSageLight else PureAlabaster,
        selectedBorderColor = MedinaSage,
        unselectedBorderColor = if (hasHoliday) DesertOchre else DividerColor,
        borderWidth = if (isSelected) AppDimensions.borderWidthMedium else AppDimensions.borderWidthHairline,
        cornerRadius = AppDimensions.cornerExtraLarge,
        contentPadding = AppDimensions.paddingZero,
        elevation = AppDimensions.elevationSubtle
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
                        contentDescription = stringResource(R.string.calendar_holiday_desc),
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
