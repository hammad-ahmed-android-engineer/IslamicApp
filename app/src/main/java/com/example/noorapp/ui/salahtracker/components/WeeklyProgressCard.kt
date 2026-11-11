package com.example.noorapp.ui.salahtracker.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Star
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
import com.example.noorapp.core.designsystem.AppLinearProgress
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.domain.model.PrayerType

@Composable
fun WeeklyProgressCard(
    completedCount: Int,
    totalCount: Int = 35,
    percentage: Float,
    breakdown: Map<PrayerType, Pair<Int, Int>>,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "weekly_progress"
    )

    AppCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = DividerColor,
        cornerRadius = AppDimensions.cornerExtraLarge,
        contentPadding = AppDimensions.paddingMediumSmall,
        elevation = AppDimensions.elevationLow
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.iconButtonCompact)
                            .clip(CircleShape)
                            .background(DesertOchreLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = DesertOchre,
                            modifier = Modifier.size(AppDimensions.iconRegular)
                        )
                    }

                    Column {
                        AppText.TitleMedium(
                            text = stringResource(R.string.tracker_weekly_progress),
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        AppText.BodySmall(
                            text = stringResource(R.string.tracker_weekly_completed_format, completedCount, totalCount),
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                        .background(MedinaSageLight)
                        .padding(
                            horizontal = AppDimensions.paddingRegular,
                            vertical = AppDimensions.paddingTiny
                        )
                ) {
                    AppText.BodyMedium(
                        text = "${(percentage * 100).toInt()}%",
                        color = MedinaSage,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            AppLinearProgress(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.paddingSmall)
                    .clip(RoundedCornerShape(AppDimensions.cornerMicro)),
                color = MedinaSage,
                trackColor = MedinaSageLight
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    PrayerType.FAJR to stringResource(R.string.prayer_fajr),
                    PrayerType.DHUHR to stringResource(R.string.prayer_dhuhr),
                    PrayerType.ASR to stringResource(R.string.prayer_asr),
                    PrayerType.MAGHRIB to stringResource(R.string.prayer_maghrib),
                    PrayerType.ISHA to stringResource(R.string.prayer_isha)
                ).forEach { (type, label) ->
                    val pair = breakdown[type] ?: Pair(0, 7)
                    PrayerBreakdownItem(
                        name = label,
                        completed = pair.first,
                        total = pair.second
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerBreakdownItem(
    name: String,
    completed: Int,
    total: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppText.Caption(
            text = name,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
        AppText.BodySmall(
            text = stringResource(R.string.salah_fraction_format, completed, total),
            fontWeight = FontWeight.Bold,
            color = if (completed == total && total > 0) MedinaSage else TextPrimary
        )
    }
}
