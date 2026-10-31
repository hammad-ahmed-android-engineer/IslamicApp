package com.ne.muslimprayers.qiblafinder.ui.salahtracker.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageDark
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.theme.ssp
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.salahtracker.SalahPrayerItemUi

@Composable
fun PrayerChecklistSection(
    formattedDate: String,
    isToday: Boolean,
    completedCount: Int,
    prayers: List<SalahPrayerItemUi>,
    onTogglePrayer: (PrayerType) -> Unit,
    onMarkAll: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppText.TitleMedium(
                    text = if (isToday) "Today's Prayers Checklist" else "Prayers for $formattedDate",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                AppText.BodySmall(
                    text = "Did you offer your prayers on this day? ($completedCount/5 completed)",
                    color = TextSecondary
                )
            }

            // Quick Mark All / Reset
            Row(horizontalArrangement = Arrangement.spacedBy(6.sdp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.sdp))
                        .background(MedinaSageLight)
                        .clickableDebounced {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMarkAll(true)
                        }
                        .padding(horizontal = 8.sdp, vertical = 4.sdp)
                ) {
                    Text(
                        text = "Mark All",
                        fontSize = 11.ssp,
                        fontWeight = FontWeight.Bold,
                        color = MedinaSage
                    )
                }

                if (completedCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.sdp))
                            .background(DividerColor.copy(alpha = 0.5f))
                            .clickableDebounced {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onMarkAll(false)
                            }
                            .padding(horizontal = 8.sdp, vertical = 4.sdp)
                    ) {
                        Text(
                            text = "Reset",
                            fontSize = 11.ssp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.sdp))

        // Checklist of 5 prayers
        Column(verticalArrangement = Arrangement.spacedBy(8.sdp)) {
            prayers.forEach { prayer ->
                PrayerCheckboxCard(
                    prayer = prayer,
                    onToggle = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onTogglePrayer(prayer.type)
                    }
                )
            }
        }
    }
}

@Composable
private fun PrayerCheckboxCard(
    prayer: SalahPrayerItemUi,
    onToggle: () -> Unit
) {
    val cardBgColor by animateColorAsState(
        targetValue = if (prayer.isOffered) MedinaSageLight.copy(alpha = 0.5f) else PureAlabaster,
        label = "card_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (prayer.isOffered) MedinaSage.copy(alpha = 0.4f) else DividerColor,
        label = "card_border"
    )

    val icon: ImageVector = when (prayer.type) {
        PrayerType.FAJR -> Icons.Default.WbTwilight
        PrayerType.DHUHR -> Icons.Default.WbSunny
        PrayerType.ASR -> Icons.Default.WbSunny
        PrayerType.MAGHRIB -> Icons.Default.NightsStay
        PrayerType.ISHA -> Icons.Default.DarkMode
        else -> Icons.Default.WbSunny
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.sdp))
            .clickableDebounced { onToggle() }
            .border(1.sdp, borderColor, RoundedCornerShape(16.sdp)),
        shape = RoundedCornerShape(16.sdp),
        color = cardBgColor,
        tonalElevation = if (prayer.isOffered) 2.sdp else 1.sdp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.sdp, vertical = 12.sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(36.sdp)
                        .clip(CircleShape)
                        .background(if (prayer.isOffered) MedinaSage else DesertOchreLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (prayer.isOffered) Icons.Default.Check else icon,
                        contentDescription = null,
                        tint = if (prayer.isOffered) PureAlabaster else DesertOchre,
                        modifier = Modifier.size(18.sdp)
                    )
                }

                Spacer(modifier = Modifier.width(12.sdp))

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.sdp)
                    ) {
                        AppText.TitleMedium(
                            text = prayer.englishName,
                            fontWeight = FontWeight.Bold,
                            color = if (prayer.isOffered) MedinaSageDark else TextPrimary
                        )

                        Text(
                            text = "(${prayer.arabicName})",
                            fontSize = 12.ssp,
                            fontWeight = FontWeight.Normal,
                            color = MedinaSage
                        )
                    }

                    Spacer(modifier = Modifier.height(2.sdp))

                    AppText.BodySmall(
                        text = prayer.description,
                        color = TextSecondary
                    )
                }
            }

            // Material 3 Checkbox
            Checkbox(
                checked = prayer.isOffered,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MedinaSage,
                    checkmarkColor = PureAlabaster,
                    uncheckedColor = TextSecondary.copy(alpha = 0.5f)
                )
            )
        }
    }
}
