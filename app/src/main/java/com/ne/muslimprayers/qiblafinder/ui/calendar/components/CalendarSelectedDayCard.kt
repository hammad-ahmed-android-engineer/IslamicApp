package com.ne.muslimprayers.qiblafinder.ui.calendar.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageDark
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.domain.model.IslamicCalendarInfo
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun CalendarSelectedDayCard(day: IslamicCalendarInfo) {
    IslamicCard(
        backgroundColor = MedinaSage,
        borderColor = DesertOchre.copy(alpha = 0.4f)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    AppText.TitleMedium(
                        text = day.gregorianFormatted,
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                    AppText.BodyMedium(
                        text = day.hijriFormatted,
                        fontWeight = FontWeight.SemiBold,
                        color = DesertOchre,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .size(AppDimensions.badgeCircleMedium)
                        .clip(CircleShape)
                        .background(DesertOchre.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
            }

            if (day.holidays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AppDimensions.cornerMediumSmall))
                        .background(MedinaSageDark)
                        .padding(horizontal = AppDimensions.paddingRegular, vertical = AppDimensions.paddingExtraSmall)
                ) {
                    AppText.BodySmall(
                        text = "Islamic Occasion: ${day.holidays.joinToString(", ")}",
                        fontWeight = FontWeight.Bold,
                        color = MedinaSageLight
                    )
                }
            }
        }
    }
}
