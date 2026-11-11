package com.example.noorapp.ui.home.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCircularProgress
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.HeroBannerGradient
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.utils.arabicTitleRes
import com.example.noorapp.core.utils.prayerArabicResByKey
import com.example.noorapp.core.utils.prayerNameResByKey
import com.example.noorapp.core.utils.titleRes
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.model.SinglePrayer

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
    val nextPrayerArabicName = nextPrayer?.type?.arabicTitleRes()?.let { stringResource(it) }
        ?: prayerArabicResByKey(nextPrayerName)?.let { stringResource(it) }
        ?: ""
    val resolvedNextPrayerName = nextPrayer?.type?.titleRes()?.let { stringResource(it) }
        ?: prayerNameResByKey(nextPrayerName)?.let { stringResource(it) }
        ?: nextPrayerName.ifBlank { stringResource(R.string.prayer_fajr) }

    val currentPrayerArabicName = currentPrayer?.type?.arabicTitleRes()?.let { stringResource(it) } ?: ""
    val currentPrayerEnglishName = currentPrayer?.type?.titleRes()?.let { stringResource(it) } ?: ""

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MedinaSage,
        borderColor = DesertOchre.copy(alpha = 0.45f),
        borderWidth = AppDimensions.borderWidthThin,
        cornerRadius = AppDimensions.cornerDialog,
        contentPadding = AppDimensions.paddingZero,
        elevation = AppDimensions.elevationHigh
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
                            text = hijriDate.ifBlank { stringResource(R.string.home_islamic_calendar_fallback) },
                            fontWeight = FontWeight.Bold,
                            color = PureAlabaster,
                            maxLines = 1
                        )
                        AppText.BodySmall(
                            text = gregorianDate.ifBlank { stringResource(R.string.home_today_fallback) },
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
                                text = if (currentPrayer != null) {
                                    stringResource(R.string.home_active_prayer_badge, currentPrayerEnglishName)
                                } else {
                                    stringResource(R.string.home_next_prayer)
                                },
                                color = DesertOchreLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Current Prayer Details Card (when current prayer information is available)
                if (currentPrayer != null) {
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
                    AppCard(
                        backgroundColor = PureAlabaster.copy(alpha = 0.14f),
                        borderColor = PureAlabaster.copy(alpha = 0.22f),
                        borderWidth = AppDimensions.borderWidthHairline,
                        cornerRadius = AppDimensions.cornerMedium,
                        contentPadding = AppDimensions.paddingZero,
                        elevation = AppDimensions.elevationNone,
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
                                            text = stringResource(
                                                R.string.home_current_prayer_prefix,
                                                currentPrayerEnglishName
                                            ),
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
                                        text = stringResource(R.string.home_started_at, currentPrayer.timeFormatted),
                                        color = MedinaSageLight.copy(alpha = 0.95f)
                                    )
                                }
                            }

                            if (nextPrayer != null) {
                                AppText.Caption(
                                    text = stringResource(R.string.home_ends_at, nextPrayer.timeFormatted),
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
                        AppCircularProgress(
                            color = PureAlabaster,
                            strokeWidth = AppDimensions.borderWidthThick
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                AppText.DisplayLarge(
                                    text = resolvedNextPrayerName,
                                    color = PureAlabaster,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f, fill = false)
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
                                text = if (nextPrayer != null) {
                                    stringResource(R.string.home_next_at_starts_in, nextPrayer.timeFormatted)
                                } else {
                                    stringResource(R.string.home_starts_in)
                                },
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
                                text = countdownText.ifBlank { stringResource(R.string.home_countdown_placeholder) },
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
                            contentDescription = stringResource(R.string.home_qibla_compass_desc),
                            tint = DesertOchre,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
                        AppText.BodySmall(
                            text = stringResource(R.string.home_qibla_direction_btn),
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
                            contentDescription = stringResource(R.string.home_timings_desc),
                            tint = MedinaSageLight,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingExtraSmall))
                        AppText.BodySmall(
                            text = stringResource(R.string.home_prayer_times_btn),
                            color = PureAlabaster,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
