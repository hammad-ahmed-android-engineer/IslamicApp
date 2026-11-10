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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.HeroBannerGradient
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
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
            .clip(RoundedCornerShape(AppDimensions.cornerDialog))
            .border(
                AppDimensions.borderWidthThin,
                DesertOchre.copy(alpha = 0.45f),
                RoundedCornerShape(AppDimensions.cornerDialog)
            ),
        shape = RoundedCornerShape(AppDimensions.cornerDialog),
        color = MedinaSage,
        tonalElevation = AppDimensions.elevationHigh
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroBannerGradient)
                .padding(AppDimensions.paddingMedium)
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
                            .border(AppDimensions.borderWidthHairline, DesertOchre, CircleShape)
                            .padding(
                                horizontal = AppDimensions.paddingRegular,
                                vertical = AppDimensions.paddingTiny
                            )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = DesertOchreLight,
                                modifier = Modifier.size(AppDimensions.iconTiny)
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
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
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
                    Surface(
                        color = PureAlabaster.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(AppDimensions.cornerMedium),
                        border = androidx.compose.foundation.BorderStroke(
                            AppDimensions.borderWidthHairline,
                            PureAlabaster.copy(alpha = 0.22f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = AppDimensions.paddingMediumSmall,
                                    vertical = AppDimensions.paddingSmall
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(AppDimensions.paddingSmall)
                                        .clip(CircleShape)
                                        .background(DesertOchre)
                                )
                                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AppText.BodySmall(
                                            text = "CURRENT PRAYER: ${currentPrayer.type.displayName}",
                                            fontWeight = FontWeight.Bold,
                                            color = PureAlabaster
                                        )
                                        if (currentPrayerArabicName.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                                            AppText.BodySmall(
                                                text = "($currentPrayerArabicName)",
                                                fontWeight = FontWeight.Bold,
                                                color = DesertOchreLight
                                            )
                                        }
                                    }
                                    AppText.Caption(
                                        text = "Started at ${currentPrayer.timeFormatted}",
                                        color = MedinaSageLight.copy(alpha = 0.95f)
                                    )
                                }
                            }

                            if (nextPrayer != null) {
                                AppText.Caption(
                                    text = "Ends ${nextPrayer.timeFormatted}",
                                    fontWeight = FontWeight.SemiBold,
                                    color = DesertOchreLight
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                // Middle Upcoming Prayer & Countdown display
                if (isLoading && countdownText.isBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppDimensions.fabSize),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PureAlabaster,
                            strokeWidth = AppDimensions.borderWidthThick
                        )
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
                                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
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
                                .clip(RoundedCornerShape(AppDimensions.cornerMediumLarge))
                                .background(DesertOchre)
                                .padding(
                                    horizontal = AppDimensions.paddingMediumSmall,
                                    vertical = AppDimensions.paddingExtraSmall
                                )
                        ) {
                            AppText.HeadingMedium(
                                text = countdownText.ifBlank { "--:--:--" },
                                color = PureAlabaster,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))

                // Bottom Quick Action Buttons inside Hero Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                ) {
                    // Qibla Direction Shortcut
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                            .background(PureAlabaster.copy(alpha = 0.15f))
                            .clickableDebounced { onNavigateToCompass() }
                            .padding(
                                vertical = AppDimensions.paddingSmall,
                                horizontal = AppDimensions.paddingRegular
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CompassCalibration,
                            contentDescription = "Qibla Compass",
                            tint = DesertOchre,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
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
                            .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                            .background(PureAlabaster.copy(alpha = 0.15f))
                            .clickableDebounced { onNavigateToPrayersTiming() }
                            .padding(
                                vertical = AppDimensions.paddingSmall,
                                horizontal = AppDimensions.paddingRegular
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Timings",
                            tint = MedinaSageLight,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
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
