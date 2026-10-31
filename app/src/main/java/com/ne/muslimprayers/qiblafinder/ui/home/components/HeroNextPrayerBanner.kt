package com.ne.muslimprayers.qiblafinder.ui.home.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.HeroBannerGradient
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.domain.model.SinglePrayer
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun HeroNextPrayerBanner(
    hijriDate: String,
    gregorianDate: String,
    nextPrayerName: String,
    countdownText: String,
    isLoading: Boolean,
    currentPrayer: SinglePrayer? = null,
    nextPrayer: SinglePrayer? = null,
    onNavigateToCompass: () -> Unit = {},
    onNavigateToPrayersTiming: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val nextPrayerArabicName = when ((nextPrayer?.type?.name ?: nextPrayerName).lowercase()) {
        "fajr" -> "الفجر"
        "sunrise" -> "الشروق"
        "dhuhr" -> "الظهر"
        "asr" -> "العصر"
        "maghrib" -> "المغرب"
        "isha" -> "العشاء"
        else -> ""
    }

    val currentPrayerArabicName = when (currentPrayer?.type) {
        PrayerType.FAJR -> "الفجر"
        PrayerType.SUNRISE -> "الشروق"
        PrayerType.DHUHR -> "الظهر"
        PrayerType.ASR -> "العصر"
        PrayerType.MAGHRIB -> "المغرب"
        PrayerType.ISHA -> "العشاء"
        null -> ""
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.sdp))
            .border(1.2.sdp, DesertOchre.copy(alpha = 0.45f), RoundedCornerShape(24.sdp)),
        shape = RoundedCornerShape(24.sdp),
        color = MedinaSage,
        tonalElevation = 6.sdp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroBannerGradient)
                .padding(18.sdp)
        ) {
            Column {
                // Top Date & Current Status Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        AppText.TitleMedium(
                            text = hijriDate.ifBlank { "Islamic Calendar" },
                            fontWeight = FontWeight.Bold,
                            color = PureAlabaster,
                            maxLines = 1
                        )
                        AppText.BodySmall(
                            text = gregorianDate.ifBlank { "Today" },
                            color = MedinaSageLight.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DesertOchre.copy(alpha = 0.35f))
                            .border(0.8.sdp, DesertOchre, CircleShape)
                            .padding(horizontal = 10.sdp, vertical = 4.sdp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = DesertOchreLight,
                                modifier = Modifier.size(12.sdp)
                            )
                            Spacer(modifier = Modifier.width(4.sdp))
                            AppText.Caption(
                                text = if (currentPrayer != null) "Active: ${currentPrayer.type.displayName}" else "Next Prayer",
                                color = DesertOchreLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Current Prayer Details Card (when current prayer information is available)
                if (currentPrayer != null) {
                    Spacer(modifier = Modifier.height(12.sdp))
                    Surface(
                        color = PureAlabaster.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.sdp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, PureAlabaster.copy(alpha = 0.22f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.sdp, vertical = 8.sdp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.sdp)
                                        .clip(CircleShape)
                                        .background(DesertOchre)
                                )
                                Spacer(modifier = Modifier.width(7.sdp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "CURRENT PRAYER: ${currentPrayer.type.displayName}",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PureAlabaster
                                        )
                                        if (currentPrayerArabicName.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(5.sdp))
                                            Text(
                                                text = "($currentPrayerArabicName)",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DesertOchreLight
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Started at ${currentPrayer.timeFormatted}",
                                        fontSize = 10.5.sp,
                                        color = MedinaSageLight.copy(alpha = 0.95f)
                                    )
                                }
                            }

                            if (nextPrayer != null) {
                                Text(
                                    text = "Ends ${nextPrayer.timeFormatted}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DesertOchreLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.sdp))

                // Middle Upcoming Prayer & Countdown display
                if (isLoading && countdownText.isBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.sdp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PureAlabaster, strokeWidth = 2.sdp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                AppText.DisplayLarge(
                                    text = nextPrayer?.type?.displayName ?: nextPrayerName.ifBlank { "Fajr" },
                                    color = PureAlabaster,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                if (nextPrayerArabicName.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(8.sdp))
                                    AppText.TitleMedium(
                                        text = nextPrayerArabicName,
                                        color = DesertOchre,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            AppText.BodySmall(
                                text = if (nextPrayer != null) "Next at ${nextPrayer.timeFormatted} • Starts in" else "Starts in",
                                color = MedinaSageLight.copy(alpha = 0.85f)
                            )
                        }

                        // Countdown Pill Box
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.sdp))
                                .background(DesertOchre)
                                .padding(horizontal = 12.sdp, vertical = 7.sdp)
                        ) {
                            AppText.HeadingMedium(
                                text = countdownText.ifBlank { "--:--:--" },
                                color = PureAlabaster,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.sdp))

                // Bottom Quick Action Buttons inside Hero Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.sdp)
                ) {
                    // Qibla Direction Shortcut
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.sdp))
                            .background(PureAlabaster.copy(alpha = 0.15f))
                            .clickableDebounced { onNavigateToCompass() }
                            .padding(vertical = 8.sdp, horizontal = 10.sdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompassCalibration,
                            contentDescription = "Qibla Compass",
                            tint = DesertOchre,
                            modifier = Modifier.size(16.sdp)
                        )
                        Spacer(modifier = Modifier.width(6.sdp))
                        AppText.BodySmall(
                            text = "Qibla Direction",
                            color = PureAlabaster,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Full Timings Shortcut
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.sdp))
                            .background(PureAlabaster.copy(alpha = 0.15f))
                            .clickableDebounced { onNavigateToPrayersTiming() }
                            .padding(vertical = 8.sdp, horizontal = 10.sdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Timings",
                            tint = MedinaSageLight,
                            modifier = Modifier.size(16.sdp)
                        )
                        Spacer(modifier = Modifier.width(6.sdp))
                        AppText.BodySmall(
                            text = "Prayer Times",
                            color = PureAlabaster,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
