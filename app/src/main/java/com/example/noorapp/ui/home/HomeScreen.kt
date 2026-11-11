package com.example.noorapp.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noorapp.R
import com.example.noorapp.core.common.PermissionPreferencesManager
import com.example.noorapp.core.designsystem.AppCircularProgress
import com.example.noorapp.core.designsystem.AppDialog
import com.example.noorapp.core.designsystem.AppErrorState
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.rememberAppPermissionHandler
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.AppUtils
import com.example.noorapp.ui.hadith.components.HadithDetailContent
import com.example.noorapp.ui.home.components.DailyDuaCard
import com.example.noorapp.ui.home.components.DailyHadithCard
import com.example.noorapp.ui.home.components.HeroNextPrayerBanner
import com.example.noorapp.ui.home.components.HomeScreenHeader
import com.example.noorapp.ui.home.components.PrayerTimingChip
import com.example.noorapp.ui.home.components.QuickActionsSection
import com.example.noorapp.ui.home.components.TodaySalahTrackerCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCompass: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToPrayersTiming: () -> Unit,
    onNavigateToPrayerAlarm: () -> Unit,
    onNavigateToSalahTracker: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onScreenResumed()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val locationPermissions = remember { PermissionPreferencesManager.locationPermissions() }
    val locationPermissionHandler = rememberAppPermissionHandler(
        permissionKey = PermissionPreferencesManager.KEY_LOCATION,
        permissions = locationPermissions,
        isGranted = { AppUtils.hasLocationPermission(context) },
        dialogTitle = stringResource(R.string.perm_location_title),
        dialogMessage = stringResource(R.string.perm_location_message),
        onGranted = { viewModel.refreshLocationAndSchedule() }
    )

    val handleRefreshLocation = {
        locationPermissionHandler.request()
    }

    state.selectedHadith?.let { hadith ->
        AppDialog(
            onDismissRequest = { viewModel.selectHadith(null) },
            title = hadith.source,
            headerIcon = Icons.Default.AutoStories,
            headerIconBgColor = null,
            showCloseButton = true,
            dismissButtonText = stringResource(R.string.copy),
            onDismissClick = {
                val text = context.getString(
                    R.string.hadith_copy_format,
                    hadith.englishText,
                    hadith.source,
                    hadith.chapter
                )
                AppUtils.copyToClipboard(
                    context,
                    context.getString(R.string.home_hadith_clipboard_label),
                    text,
                    context.getString(R.string.hadith_copied)
                )
            },
            confirmButtonText = stringResource(R.string.share),
            confirmButtonIcon = Icons.Default.Share,
            onConfirmClick = {
                val text = context.getString(
                    R.string.hadith_share_format,
                    hadith.englishText,
                    hadith.source,
                    hadith.chapter
                )
                AppUtils.shareText(
                    context,
                    text,
                    context.getString(R.string.share_hadith_via)
                )
            },
            scrollable = true,
            usePlatformDefaultWidth = false,
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.85f else 0.92f)
                .fillMaxHeight(0.85f)
        ) {
            HadithDetailContent(hadith = hadith)
        }
    }

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmParchment)
                .statusBarsPadding()
                .padding(horizontal = AppDimensions.paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    top = AppDimensions.paddingSmall,
                    bottom = AppDimensions.paddingExtraLarge
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
            ) {
                item {
                    HomeScreenHeader(
                        locationName = state.locationName,
                        onRefreshLocation = handleRefreshLocation
                    )
                }

                item {
                    HeroNextPrayerBanner(
                        hijriDate = state.hijriDate,
                        gregorianDate = state.gregorianDate,
                        nextPrayerName = state.nextPrayerName,
                        countdownText = state.countdownText,
                        isLoading = state.isLoading,
                        currentPrayer = state.currentPrayer,
                        nextPrayer = state.nextPrayer,
                        onNavigateToCompass = onNavigateToCompass,
                        onNavigateToPrayersTiming = onNavigateToPrayersTiming
                    )
                }

                item {
                    AppText.TitleMedium(
                        text = stringResource(R.string.home_todays_prayers),
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = AppDimensions.paddingSmall)
                    )

                    if (state.isLoading && state.schedule == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(AppDimensions.prayerChipHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            AppCircularProgress(color = MedinaSage)
                        }
                    } else if (state.errorMessage != null && state.schedule == null) {
                        AppErrorState(
                            message = state.errorMessage ?: stringResource(R.string.error_generic),
                            onRetry = { viewModel.refreshLocationAndSchedule() }
                        )
                    } else {
                        val salawat = state.schedule?.mainSalawat.orEmpty()
                        val alarmsMap = state.alarms.associate { it.prayerType to (state.notificationsEnabled && it.isAlarmActive) }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(salawat) { prayer ->
                                val isAlarmOn = alarmsMap[prayer.type] ?: (state.notificationsEnabled && prayer.isAlarmActive)
                                PrayerTimingChip(
                                    prayer = prayer,
                                    isAlarmActive = isAlarmOn,
                                    onToggleAlarm = { viewModel.togglePrayerAlarm(prayer) },
                                    onClick = onNavigateToPrayersTiming
                                )
                            }
                        }
                    }
                }

                item {
                    TodaySalahTrackerCard(
                        record = state.todaySalahRecord,
                        onTogglePrayer = { prayer, offered ->
                            viewModel.togglePrayerOffered(prayer, offered)
                        },
                        onNavigateToFullTracker = onNavigateToSalahTracker
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1.1f),
                contentPadding = PaddingValues(
                    top = AppDimensions.paddingSmall,
                    bottom = AppDimensions.paddingExtraLarge
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
            ) {
                item {
                    QuickActionsSection(
                        onNavigateToCompass = onNavigateToCompass,
                        onNavigateToCalendar = onNavigateToCalendar,
                        onNavigateToPrayersTiming = onNavigateToPrayersTiming,
                        onNavigateToPrayerAlarm = onNavigateToPrayerAlarm
                    )
                }

                item {
                    DailyHadithCard(
                        hadith = state.dailyHadith,
                        onClick = {
                            if (state.dailyHadith != null) {
                                viewModel.selectHadith(state.dailyHadith)
                            }
                        }
                    )
                }

                item {
                    DailyDuaCard(dua = state.dailyDua)
                }
            }
        }
    } else {
        // Portrait View
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmParchment)
                .statusBarsPadding()
                .padding(horizontal = AppDimensions.paddingMedium),
            contentPadding = PaddingValues(
                top = AppDimensions.paddingSmall,
                bottom = AppDimensions.paddingExtraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
        ) {
            item {
                HomeScreenHeader(
                    locationName = state.locationName,
                    onRefreshLocation = handleRefreshLocation
                )
            }

            item {
                HeroNextPrayerBanner(
                    hijriDate = state.hijriDate,
                    gregorianDate = state.gregorianDate,
                    nextPrayerName = state.nextPrayerName,
                    countdownText = state.countdownText,
                    isLoading = state.isLoading,
                    currentPrayer = state.currentPrayer,
                    nextPrayer = state.nextPrayer,
                    onNavigateToCompass = onNavigateToCompass,
                    onNavigateToPrayersTiming = onNavigateToPrayersTiming
                )
            }

            item {
                AppText.TitleMedium(
                    text = stringResource(R.string.home_todays_prayers),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                if (state.isLoading && state.schedule == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppDimensions.prayerChipHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        AppCircularProgress(color = MedinaSage)
                    }
                } else if (state.errorMessage != null && state.schedule == null) {
                    AppErrorState(
                        message = state.errorMessage ?: stringResource(R.string.error_generic),
                        onRetry = { viewModel.refreshLocationAndSchedule() }
                    )
                } else {
                    val salawat = state.schedule?.mainSalawat.orEmpty()
                    val alarmsMap = state.alarms.associate { it.prayerType to (state.notificationsEnabled && it.isAlarmActive) }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(salawat) { prayer ->
                            val isAlarmOn = alarmsMap[prayer.type] ?: (state.notificationsEnabled && prayer.isAlarmActive)
                            PrayerTimingChip(
                                prayer = prayer,
                                isAlarmActive = isAlarmOn,
                                onToggleAlarm = { viewModel.togglePrayerAlarm(prayer) },
                                onClick = onNavigateToPrayersTiming
                            )
                        }
                    }
                }
            }

            item {
                TodaySalahTrackerCard(
                    record = state.todaySalahRecord,
                    onTogglePrayer = { prayer, offered ->
                        viewModel.togglePrayerOffered(prayer, offered)
                    },
                    onNavigateToFullTracker = onNavigateToSalahTracker
                )
            }

            item {
                QuickActionsSection(
                    onNavigateToCompass = onNavigateToCompass,
                    onNavigateToCalendar = onNavigateToCalendar,
                    onNavigateToPrayersTiming = onNavigateToPrayersTiming,
                    onNavigateToPrayerAlarm = onNavigateToPrayerAlarm
                )
            }

            item {
                DailyHadithCard(
                    hadith = state.dailyHadith,
                    onClick = {
                        if (state.dailyHadith != null) {
                            viewModel.selectHadith(state.dailyHadith)
                        }
                    }
                )
            }

            item {
                DailyDuaCard(dua = state.dailyDua)
            }
        }
    }
}
