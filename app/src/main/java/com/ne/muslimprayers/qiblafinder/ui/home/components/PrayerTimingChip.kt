package com.ne.muslimprayers.qiblafinder.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun PrayerTimingChip(
    prayer: SinglePrayer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (prayer.type) {
        PrayerType.FAJR -> Icons.Default.WbTwilight
        PrayerType.SUNRISE -> Icons.Default.WbSunny
        PrayerType.DHUHR -> Icons.Default.WbSunny
        PrayerType.ASR -> Icons.Default.WbSunny
        PrayerType.MAGHRIB -> Icons.Default.NightsStay
        PrayerType.ISHA -> Icons.Default.NightsStay
    }

    val arabicName = when (prayer.type) {
        PrayerType.FAJR -> "الفجر"
        PrayerType.SUNRISE -> "الشروق"
        PrayerType.DHUHR -> "الظهر"
        PrayerType.ASR -> "العصر"
        PrayerType.MAGHRIB -> "المغرب"
        PrayerType.ISHA -> "العشاء"
    }

    Surface(
        modifier = modifier
            .width(112.sdp)
            .clip(RoundedCornerShape(18.sdp))
            .clickableDebounced { onClick() }
            .border(
                width = if (prayer.isNext) 1.5.sdp else 1.sdp,
                color = if (prayer.isNext) MedinaSage else DividerColor,
                shape = RoundedCornerShape(18.sdp)
            ),
        shape = RoundedCornerShape(18.sdp),
        color = if (prayer.isNext) MedinaSage else PureAlabaster,
        tonalElevation = if (prayer.isNext) 5.sdp else 1.sdp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.sdp, horizontal = 10.sdp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon + Next Indicator
            Box(
                modifier = Modifier
                    .size(36.sdp)
                    .clip(CircleShape)
                    .background(if (prayer.isNext) DesertOchre.copy(alpha = 0.25f) else MedinaSageLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (prayer.isNext) DesertOchre else MedinaSage,
                    modifier = Modifier.size(20.sdp)
                )
            }

            Spacer(modifier = Modifier.height(6.sdp))

            // Prayer Name & Arabic
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AppText.BodySmall(
                    text = prayer.type.displayName,
                    fontWeight = FontWeight.Bold,
                    color = if (prayer.isNext) PureAlabaster else TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(3.sdp))
                AppText.Caption(
                    text = arabicName,
                    color = if (prayer.isNext) DesertOchre else TextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(3.sdp))

            // Time Formatted
            AppText.TitleMedium(
                text = prayer.timeFormatted,
                fontWeight = FontWeight.Bold,
                color = if (prayer.isNext) DesertOchre else MedinaSage,
                maxLines = 1
            )
        }
    }
}
