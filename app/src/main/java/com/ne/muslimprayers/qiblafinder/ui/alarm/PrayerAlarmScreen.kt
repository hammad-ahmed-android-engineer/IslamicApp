package com.ne.muslimprayers.qiblafinder.ui.alarm

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.common.PrayerNotificationMode
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.alarm.components.AdhanAudioInfoCard
import com.ne.muslimprayers.qiblafinder.ui.alarm.components.PrayerAlarmRowCard
import com.ne.muslimprayers.qiblafinder.ui.alarm.components.SunriseSunsetHeaderCard
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState

@Composable
fun PrayerAlarmScreen(
    viewModel: PrayerAlarmViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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
                        .padding(horizontal = AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = AppDimensions.paddingTiny),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                    ) {
                        AdhanAudioInfoCard(
                            masterEnabled = state.masterNotificationsEnabled,
                            onToggleMaster = viewModel::toggleMasterNotifications,
                            onEnableAll = { viewModel.setAllAlarmsEnabled(true) },
                            onDisableAll = { viewModel.setAllAlarmsEnabled(false) },
                            isPlayingPreview = state.isPlayingPreview,
                            previewPrayerType = state.previewPrayerType,
                            onTogglePreview = viewModel::togglePreviewAdhan
                        )
                        SunriseSunsetHeaderCard(
                            sunriseTime = state.sunriseTime,
                            sunsetTime = state.sunsetTime
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(
                            top = AppDimensions.paddingTiny,
                            bottom = AppDimensions.paddingLarge
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                    ) {
                        items(state.alarms, key = { it.id }) { alarmItem ->
                            val mode = state.notificationModes[alarmItem.prayerType]
                                ?: PrayerNotificationMode.ADHAN
                            PrayerAlarmRowCard(
                                alarm = alarmItem,
                                notificationMode = mode,
                                masterEnabled = state.masterNotificationsEnabled,
                                onToggle = { viewModel.toggleAlarm(alarmItem) },
                                onSelectMode = { selectedMode ->
                                    viewModel.setPrayerNotificationMode(alarmItem.prayerType, selectedMode)
                                }
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = AppDimensions.paddingMedium),
                    contentPadding = PaddingValues(
                        top = AppDimensions.paddingTiny,
                        bottom = AppDimensions.paddingLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                ) {
                    item {
                        AdhanAudioInfoCard(
                            masterEnabled = state.masterNotificationsEnabled,
                            onToggleMaster = viewModel::toggleMasterNotifications,
                            onEnableAll = { viewModel.setAllAlarmsEnabled(true) },
                            onDisableAll = { viewModel.setAllAlarmsEnabled(false) },
                            isPlayingPreview = state.isPlayingPreview,
                            previewPrayerType = state.previewPrayerType,
                            onTogglePreview = viewModel::togglePreviewAdhan
                        )
                    }

                    item {
                        SunriseSunsetHeaderCard(
                            sunriseTime = state.sunriseTime,
                            sunsetTime = state.sunsetTime
                        )
                    }

                    items(state.alarms, key = { it.id }) { alarmItem ->
                        val mode = state.notificationModes[alarmItem.prayerType]
                            ?: PrayerNotificationMode.ADHAN
                        PrayerAlarmRowCard(
                            alarm = alarmItem,
                            notificationMode = mode,
                            masterEnabled = state.masterNotificationsEnabled,
                            onToggle = { viewModel.toggleAlarm(alarmItem) },
                            onSelectMode = { selectedMode ->
                                viewModel.setPrayerNotificationMode(alarmItem.prayerType, selectedMode)
                            }
                        )
                    }
                }
            }
        }
    }
}
