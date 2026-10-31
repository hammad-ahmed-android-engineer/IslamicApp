package com.ne.muslimprayers.qiblafinder.ui.prayers

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import com.ne.muslimprayers.qiblafinder.ui.prayers.components.PrayerTimingItemCard

@Composable
fun PrayersTimingScreen(
    viewModel: PrayersViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = "Prayer Timings",
                onBackClick = onBackClick
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (state.isLoading && state.schedule == null) {
            LoadingState(modifier = Modifier.padding(paddingValues))
        } else if (state.errorMessage != null && state.schedule == null) {
            ErrorState(
                modifier = Modifier.padding(paddingValues),
                message = state.errorMessage ?: "Failed to load timings",
                onRetry = { viewModel.loadData() }
            )
        } else {
            val schedule = state.schedule
            val alarmsMap = state.alarms.associate { it.prayerType to it.isAlarmActive }

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.sdp),
                    horizontalArrangement = Arrangement.spacedBy(16.sdp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 4.sdp),
                        verticalArrangement = Arrangement.spacedBy(12.sdp)
                    ) {
                        IslamicCard(
                            backgroundColor = MedinaSage,
                            borderColor = DesertOchre.copy(alpha = 0.4f)
                        ) {
                            Column {
                                AppText.TitleMedium(
                                    text = schedule?.dateHijri ?: "1447 AH",
                                    fontWeight = FontWeight.Bold,
                                    color = PureAlabaster,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.sdp))
                                AppText.BodyMedium(
                                    text = schedule?.dateGregorian ?: "Today",
                                    color = MedinaSageLight,
                                    maxLines = 1
                                )
                            }
                        }

                        IslamicCard(
                            backgroundColor = PureAlabaster
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MedinaSage,
                                    modifier = Modifier.size(20.sdp)
                                )
                                Spacer(modifier = Modifier.width(10.sdp))
                                AppText.BodySmall(
                                    text = "Timings are computed using Muslim World League (MWL) calculation conventions based on your geographical location.",
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(top = 4.sdp, bottom = 24.sdp),
                        verticalArrangement = Arrangement.spacedBy(10.sdp)
                    ) {
                        schedule?.allPrayers?.let { prayers ->
                            items(prayers) { prayer ->
                                val isAlarmOn = alarmsMap[prayer.type] ?: false
                                PrayerTimingItemCard(
                                    prayer = prayer,
                                    isAlarmActive = isAlarmOn,
                                    onToggleAlarm = {
                                        viewModel.toggleAlarm(
                                            prayerType = prayer.type,
                                            time = prayer.time24,
                                            isCurrentlyActive = isAlarmOn
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.sdp),
                    contentPadding = PaddingValues(top = 4.sdp, bottom = 24.sdp),
                    verticalArrangement = Arrangement.spacedBy(12.sdp)
                ) {
                    item {
                        IslamicCard(
                            backgroundColor = MedinaSage,
                            borderColor = DesertOchre.copy(alpha = 0.4f)
                        ) {
                            Column {
                                AppText.TitleMedium(
                                    text = schedule?.dateHijri ?: "1447 AH",
                                    fontWeight = FontWeight.Bold,
                                    color = PureAlabaster,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.sdp))
                                AppText.BodyMedium(
                                    text = schedule?.dateGregorian ?: "Today",
                                    color = MedinaSageLight,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    schedule?.allPrayers?.let { prayers ->
                        items(prayers) { prayer ->
                            val isAlarmOn = alarmsMap[prayer.type] ?: false
                            PrayerTimingItemCard(
                                prayer = prayer,
                                isAlarmActive = isAlarmOn,
                                onToggleAlarm = {
                                    viewModel.toggleAlarm(
                                        prayerType = prayer.type,
                                        time = prayer.time24,
                                        isCurrentlyActive = isAlarmOn
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
