package com.ne.muslimprayers.qiblafinder.ui.alarm.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.PrayerType
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun AdhanAudioInfoCard(
    masterEnabled: Boolean = true,
    onToggleMaster: (Boolean) -> Unit = {},
    onEnableAll: () -> Unit = {},
    onDisableAll: () -> Unit = {},
    isPlayingPreview: Boolean = false,
    previewPrayerType: PrayerType? = null,
    onTogglePreview: (PrayerType) -> Unit = {}
) {
    IslamicCard(
        backgroundColor = PureAlabaster,
        borderColor = if (masterEnabled) MedinaSage.copy(alpha = 0.4f) else DividerColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.sdp)) {
            // Master Notifications Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (masterEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = if (masterEnabled) MedinaSage else TextSecondary,
                        modifier = Modifier.size(22.sdp)
                    )
                    Spacer(modifier = Modifier.width(10.sdp))
                    Column {
                        AppText.TitleMedium(
                            text = "Prayer Notifications & Adhan",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        AppText.Caption(
                            text = if (masterEnabled) "Daily prayer alerts are active" else "All prayer alerts are muted",
                            color = if (masterEnabled) MedinaSage else TextSecondary
                        )
                    }
                }

                Switch(
                    checked = masterEnabled,
                    onCheckedChange = onToggleMaster,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PureAlabaster,
                        checkedTrackColor = MedinaSage,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DividerColor
                    )
                )
            }

            HorizontalDivider(color = DividerColor.copy(alpha = 0.6f), thickness = 0.8.dp)

            // Quick Enable All / Disable All + Adhan Preview Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.sdp)
            ) {
                Surface(
                    onClick = onEnableAll,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.sdp),
                    color = MedinaSageLight,
                    border = BorderStroke(0.8.dp, MedinaSage.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.sdp, horizontal = 8.sdp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(15.sdp)
                        )
                        Spacer(modifier = Modifier.width(5.sdp))
                        AppText.BodySmall(
                            text = "Enable All",
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage
                        )
                    }
                }

                Surface(
                    onClick = onDisableAll,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.sdp),
                    color = WarmParchment,
                    border = BorderStroke(0.8.dp, DividerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.sdp, horizontal = 8.sdp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(15.sdp)
                        )
                        Spacer(modifier = Modifier.width(5.sdp))
                        AppText.BodySmall(
                            text = "Disable All",
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Adhan Audio Preview Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isFajrPlaying = isPlayingPreview && previewPrayerType == PrayerType.FAJR
                val isMakkahPlaying = isPlayingPreview && previewPrayerType != PrayerType.FAJR

                Surface(
                    onClick = { onTogglePreview(PrayerType.FAJR) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.sdp),
                    color = if (isFajrPlaying) DesertOchre else DesertOchreLight.copy(alpha = 0.65f),
                    border = BorderStroke(0.8.dp, DesertOchre.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.sdp, horizontal = 8.sdp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFajrPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isFajrPlaying) PureAlabaster else DesertOchre,
                            modifier = Modifier.size(15.sdp)
                        )
                        Spacer(modifier = Modifier.width(4.sdp))
                        AppText.Caption(
                            text = if (isFajrPlaying) "Stop Fajr" else "Test Fajr Adhan",
                            fontWeight = FontWeight.Bold,
                            color = if (isFajrPlaying) PureAlabaster else DesertOchre
                        )
                    }
                }

                Surface(
                    onClick = { onTogglePreview(PrayerType.DHUHR) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.sdp),
                    color = if (isMakkahPlaying) MedinaSage else WarmParchment,
                    border = BorderStroke(0.8.dp, if (isMakkahPlaying) MedinaSage else DividerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.sdp, horizontal = 8.sdp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isMakkahPlaying) Icons.Default.Stop else Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (isMakkahPlaying) PureAlabaster else MedinaSage,
                            modifier = Modifier.size(15.sdp)
                        )
                        Spacer(modifier = Modifier.width(4.sdp))
                        AppText.Caption(
                            text = if (isMakkahPlaying) "Stop Adhan" else "Test Makkah Adhan",
                            fontWeight = FontWeight.Bold,
                            color = if (isMakkahPlaying) PureAlabaster else TextPrimary
                        )
                    }
                }
            }
        }
    }
}
