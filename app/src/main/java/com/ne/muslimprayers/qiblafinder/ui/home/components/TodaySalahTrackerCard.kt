package com.ne.muslimprayers.qiblafinder.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.theme.ssp
import com.ne.muslimprayers.qiblafinder.domain.model.DailySalahRecord
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

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

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.sdp))
            .border(1.sdp, DividerColor, RoundedCornerShape(20.sdp)),
        shape = RoundedCornerShape(20.sdp),
        color = PureAlabaster,
        tonalElevation = 2.sdp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.sdp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.sdp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.sdp)
                            .clip(CircleShape)
                            .background(MedinaSageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(18.sdp)
                        )
                    }

                    Column {
                        AppText.TitleMedium(
                            text = "Today's Prayer Tracker",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        AppText.Caption(
                            text = "متابعة صلوات اليوم",
                            color = TextSecondary
                        )
                    }
                }

                // View History Pill Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.sdp))
                        .background(MedinaSageLight.copy(alpha = 0.7f))
                        .clickableDebounced { onNavigateToFullTracker() }
                        .padding(horizontal = 8.sdp, vertical = 4.sdp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.sdp)
                ) {
                    AppText.Caption(
                        text = "Weekly",
                        fontWeight = FontWeight.Bold,
                        color = MedinaSage
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Weekly History",
                        tint = MedinaSage,
                        modifier = Modifier.size(12.sdp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.sdp))

            // Progress text & bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText.BodySmall(
                    text = if (completedCount == 5) "Alhamdulillah! All 5 prayers offered 🎉" else "Track your 5 daily prayers:",
                    color = if (completedCount == 5) MedinaSage else TextSecondary,
                    fontWeight = if (completedCount == 5) FontWeight.Bold else FontWeight.Normal
                )
                AppText.Caption(
                    text = "$completedCount / 5 (${(completedCount * 20)}%)",
                    fontWeight = FontWeight.Bold,
                    color = if (completedCount == 5) DesertOchre else MedinaSage
                )
            }

            Spacer(modifier = Modifier.height(6.sdp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.sdp)
                    .clip(CircleShape),
                color = if (completedCount == 5) DesertOchre else MedinaSage,
                trackColor = WarmParchment,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.sdp))

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
                horizontalArrangement = Arrangement.spacedBy(6.sdp)
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

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.sdp))
            .clickableDebounced { onToggle() }
            .border(1.sdp, borderColor, RoundedCornerShape(12.sdp)),
        color = bgColor,
        shape = RoundedCornerShape(12.sdp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.sdp, horizontal = 2.sdp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Check icon box
            Box(
                modifier = Modifier
                    .size(20.sdp)
                    .clip(CircleShape)
                    .background(if (isOffered) MedinaSage else PureAlabaster)
                    .border(
                        1.sdp,
                        if (isOffered) MedinaSage else DividerColor,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isOffered) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Offered",
                        tint = PureAlabaster,
                        modifier = Modifier.size(14.sdp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.sdp))

            AppText.Caption(
                text = prayer.displayName,
                fontWeight = if (isOffered) FontWeight.Bold else FontWeight.Medium,
                color = if (isOffered) MedinaSage else TextPrimary,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}
