package com.example.noorapp.ui.alarm

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noorapp.R
import com.example.noorapp.core.common.PermissionPreferencesManager
import com.example.noorapp.core.common.PrayerNotificationMode
import com.example.noorapp.core.designsystem.rememberAppPermissionHandler
import com.example.noorapp.core.designsystem.AppLoadingState
import com.example.noorapp.core.designsystem.AppTopAppBar
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.ui.alarm.components.AdhanAudioInfoCard
import com.example.noorapp.ui.alarm.components.PrayerAlarmRowCard
import com.example.noorapp.ui.alarm.components.SunriseSunsetHeaderCard

@Composable
fun PrayerAlarmScreen(
    viewModel: PrayerAlarmViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val notificationPermissions = remember { PermissionPreferencesManager.notificationPermissions() }
    val notificationPermissionHandler = rememberAppPermissionHandler(
        permissionKey = PermissionPreferencesManager.KEY_NOTIFICATION,
        permissions = notificationPermissions,
        isGranted = { PermissionPreferencesManager.hasNotificationPermission(context) },
        dialogTitle = stringResource(R.string.perm_notification_title),
        dialogMessage = stringResource(R.string.perm_notification_message),
        onGranted = {}
    )

    var hasCheckedNotificationPermission by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!hasCheckedNotificationPermission) {
            hasCheckedNotificationPermission = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !PermissionPreferencesManager.hasNotificationPermission(context) &&
                !notificationPermissionHandler.hasReachedSettingsThreshold()
            ) {
                notificationPermissionHandler.request()
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.prayer_alarm_screen_title),
                onBackClick = onBackClick
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (state.isLoading && state.alarms.isEmpty()) {
            AppLoadingState(modifier = Modifier.padding(paddingValues))
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
                            .verticalScroll(rememberScrollState())
                            .padding(
                                top = AppDimensions.paddingTiny,
                                bottom = AppDimensions.paddingLarge
                            ),
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
                            sunriseTime = state.sunriseTime.ifBlank { stringResource(R.string.prayer_alarm_time_am_placeholder) },
                            sunsetTime = state.sunsetTime.ifBlank { stringResource(R.string.prayer_alarm_time_pm_placeholder) }
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
                            sunriseTime = state.sunriseTime.ifBlank { stringResource(R.string.prayer_alarm_time_am_placeholder) },
                            sunsetTime = state.sunsetTime.ifBlank { stringResource(R.string.prayer_alarm_time_pm_placeholder) }
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
