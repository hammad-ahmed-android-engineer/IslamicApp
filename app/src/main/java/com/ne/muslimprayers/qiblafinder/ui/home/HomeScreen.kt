package com.ne.muslimprayers.qiblafinder.ui.home

import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.Hadith
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.hadith.components.HadithDetailDialog
import com.ne.muslimprayers.qiblafinder.ui.home.components.DailyDuaCard
import com.ne.muslimprayers.qiblafinder.ui.home.components.DailyHadithCard
import com.ne.muslimprayers.qiblafinder.ui.home.components.HeroNextPrayerBanner
import com.ne.muslimprayers.qiblafinder.ui.home.components.HomeScreenHeader
import com.ne.muslimprayers.qiblafinder.ui.home.components.PrayerTimingChip
import com.ne.muslimprayers.qiblafinder.ui.home.components.QuickActionsSection
import com.ne.muslimprayers.qiblafinder.ui.home.components.TodaySalahTrackerCard

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
    var selectedHadith by remember { mutableStateOf<Hadith?>(null) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.refreshLocationAndSchedule()
        }
    }

    if (selectedHadith != null) {
        HadithDetailDialog(
            hadith = selectedHadith!!,
            onDismiss = { selectedHadith = null },
            onCopy = {
                val text = "${selectedHadith!!.englishText}\n\n[${selectedHadith!!.source} • ${selectedHadith!!.chapter}]"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(context, "Hadith", text, "Hadith copied to clipboard")
            },
            onShare = {
                val text = "${selectedHadith!!.englishText}\n\n— ${selectedHadith!!.source} (${selectedHadith!!.chapter})"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(context, text, "Share Hadith via")
            }
        )
    }

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmParchment)
                .statusBarsPadding()
                .padding(horizontal = 16.sdp),
            horizontalArrangement = Arrangement.spacedBy(16.sdp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(top = 10.sdp, bottom = 32.sdp),
                verticalArrangement = Arrangement.spacedBy(14.sdp)
            ) {
                item {
                    HomeScreenHeader(
                        locationName = state.locationName,
                        onRefreshLocation = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                            viewModel.refreshLocationAndSchedule()
                        }
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
                        text = "Today's Prayer Times",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.sdp)
                    )

                    if (state.isLoading && state.schedule == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.sdp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MedinaSage)
                        }
                    } else if (state.errorMessage != null && state.schedule == null) {
                        ErrorState(message = state.errorMessage ?: "Error", onRetry = { viewModel.refreshLocationAndSchedule() })
                    } else {
                        val salawat = state.schedule?.mainSalawat.orEmpty()
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.sdp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(salawat) { prayer ->
                                PrayerTimingChip(
                                    prayer = prayer,
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
                contentPadding = PaddingValues(top = 10.sdp, bottom = 32.sdp),
                verticalArrangement = Arrangement.spacedBy(14.sdp)
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
                                selectedHadith = state.dailyHadith
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
                .padding(horizontal = 16.sdp),
            contentPadding = PaddingValues(top = 10.sdp, bottom = 32.sdp),
            verticalArrangement = Arrangement.spacedBy(14.sdp)
        ) {
            item {
                HomeScreenHeader(
                    locationName = state.locationName,
                    onRefreshLocation = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                        viewModel.refreshLocationAndSchedule()
                    }
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
                    text = "Today's Prayer Times",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.sdp))

                if (state.isLoading && state.schedule == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.sdp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MedinaSage)
                    }
                } else if (state.errorMessage != null && state.schedule == null) {
                    ErrorState(message = state.errorMessage ?: "Error", onRetry = { viewModel.refreshLocationAndSchedule() })
                } else {
                    val salawat = state.schedule?.mainSalawat.orEmpty()
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.sdp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(salawat) { prayer ->
                            PrayerTimingChip(
                                prayer = prayer,
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
                            selectedHadith = state.dailyHadith
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
