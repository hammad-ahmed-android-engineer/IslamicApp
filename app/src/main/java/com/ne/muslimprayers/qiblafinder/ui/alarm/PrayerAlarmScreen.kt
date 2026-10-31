package com.ne.muslimprayers.qiblafinder.ui.alarm

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.alarm.components.AdhanAudioInfoCard
import com.ne.muslimprayers.qiblafinder.ui.alarm.components.PrayerAlarmRowCard
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState

@Composable
fun PrayerAlarmScreen(
    viewModel: PrayerAlarmViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = "Prayer Alarms & Adhan",
                onBackClick = onBackClick
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (state.isLoading && state.alarms.isEmpty()) {
            LoadingState(modifier = Modifier.padding(paddingValues))
        } else {
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
                        AdhanAudioInfoCard()
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(top = 4.sdp, bottom = 24.sdp),
                        verticalArrangement = Arrangement.spacedBy(10.sdp)
                    ) {
                        items(state.alarms) { alarmItem ->
                            PrayerAlarmRowCard(
                                alarm = alarmItem,
                                onToggle = { viewModel.toggleAlarm(alarmItem) }
                            )
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
                        AdhanAudioInfoCard()
                    }

                    items(state.alarms) { alarmItem ->
                        PrayerAlarmRowCard(
                            alarm = alarmItem,
                            onToggle = { viewModel.toggleAlarm(alarmItem) }
                        )
                    }
                }
            }
        }
    }
}
