package com.example.noorapp.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCardHeaderRow
import com.example.noorapp.core.designsystem.AppLinearProgress
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
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.titleRes
import com.example.noorapp.domain.model.DailySalahRecord
import com.example.noorapp.domain.model.PrayerType

@Composable
fun TodaySalahTrackerCard(
    record: DailySalahRecord?,
    onTogglePrayer: (PrayerType, Boolean) -> Unit,
    onNavigateToFullTracker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = record?.completedCount ?: 0
    val targetProgress = completedCount / 5f
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 400),
        label = "home_tracker_progress"
    )

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        cornerRadius = AppDimensions.cornerExtraLarge,
        elevation = AppDimensions.elevationLow,
        contentPadding = AppDimensions.paddingMediumSmall
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row
            AppCardHeaderRow(
                title = stringResource(R.string.home_today_tracker_title),
                subtitle = stringResource(R.string.home_today_tracker_arabic),
                icon = Icons.Default.EventAvailable,
                iconTint = MedinaSage,
                iconBackgroundColor = MedinaSageLight,
                trailingContent = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                            .background(MedinaSageLight.copy(alpha = 0.7f))
                            .clickableDebounced { onNavigateToFullTracker() }
                            .padding(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingTiny),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                    ) {
                        AppText.Caption(
                            text = stringResource(R.string.home_weekly_btn),
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.home_weekly_history_desc),
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconTiny)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            // Progress text & bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText.BodySmall(
                    text = if (completedCount == 5) {
                        stringResource(R.string.home_all_prayers_offered)
                    } else {
                        stringResource(R.string.home_track_5_prayers)
                    },
                    color = if (completedCount == 5) MedinaSage else TextSecondary,
                    fontWeight = if (completedCount == 5) FontWeight.Bold else FontWeight.Normal
                )
                AppText.Caption(
                    text = stringResource(R.string.home_tracker_progress_format, completedCount, completedCount * 20),
                    fontWeight = FontWeight.Bold,
                    color = if (completedCount == 5) DesertOchre else MedinaSage
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

            AppLinearProgress(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.progressLinearHeight)
                    .clip(CircleShape),
                color = if (completedCount == 5) DesertOchre else MedinaSage,
                trackColor = WarmParchment
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            // 5 Prayers interactive checkbox items
            val prayers = listOf(
                PrayerType.FAJR,
                PrayerType.DHUHR,
                PrayerType.ASR,
                PrayerType.MAGHRIB,
                PrayerType.ISHA
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
            ) {
                prayers.forEach { prayer ->
                    val isOffered = record?.isPrayerOffered(prayer) == true
                    HomePrayerCheckItem(
                        prayer = prayer,
                        isOffered = isOffered,
                        onToggle = { onTogglePrayer(prayer, !isOffered) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomePrayerCheckItem(
    prayer: PrayerType,
    isOffered: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isOffered) MedinaSageLight else WarmParchment,
        label = "prayer_chip_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isOffered) MedinaSage else DividerColor,
        label = "prayer_chip_border"
    )

    AppSelectableCard(
        isSelected = isOffered,
        onClick = onToggle,
        modifier = modifier,
        selectedBackgroundColor = bgColor,
        unselectedBackgroundColor = bgColor,
        selectedBorderColor = borderColor,
        unselectedBorderColor = borderColor,
        borderWidth = AppDimensions.borderWidthThin,
        cornerRadius = AppDimensions.cornerMedium,
        contentPadding = AppDimensions.paddingZero,
        elevation = AppDimensions.elevationNone
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppDimensions.paddingSmall, horizontal = AppDimensions.paddingMicro),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Check icon box
            Box(
                modifier = Modifier
                    .size(AppDimensions.iconMedium)
                    .clip(CircleShape)
                    .background(if (isOffered) MedinaSage else PureAlabaster)
                    .border(
                        AppDimensions.borderWidthThin,
                        if (isOffered) MedinaSage else DividerColor,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isOffered) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.home_prayer_offered_desc),
                        tint = PureAlabaster,
                        modifier = Modifier.size(AppDimensions.iconExtraSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))

            AppText.Caption(
                text = stringResource(prayer.titleRes()),
                fontWeight = if (isOffered) FontWeight.Bold else FontWeight.Medium,
                color = if (isOffered) MedinaSage else TextPrimary,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
