package com.ne.muslimprayers.qiblafinder.ui.salahtracker

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.salahtracker.components.PrayerChecklistSection
import com.ne.muslimprayers.qiblafinder.ui.salahtracker.components.WeeklyDayStrip
import com.ne.muslimprayers.qiblafinder.ui.salahtracker.components.WeeklyNavigationHeader
import com.ne.muslimprayers.qiblafinder.ui.salahtracker.components.WeeklyProgressCard

@Composable
fun SalahTrackerScreen(
    viewModel: SalahTrackerViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = "Salah Tracker (متابعة الصلاة)",
                onBackClick = onBackClick
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (isLandscape) {
            // Landscape 2-Column Dashboard Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingSmall),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
            ) {
                // Left Column: Weekly Navigation, Day Strip & Progress Summary
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = AppDimensions.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                ) {
                    item {
                        WeeklyNavigationHeader(
                            weekRangeText = state.weekRangeFormatted,
                            weekOffset = state.weekOffset,
                            onPreviousWeek = { viewModel.changeWeek(-1) },
                            onNextWeek = { viewModel.changeWeek(1) },
                            onJumpToToday = viewModel::jumpToToday
                        )
                    }

                    item {
                        WeeklyDayStrip(
                            days = state.weekDays,
                            onDaySelected = viewModel::selectDate
                        )
                    }

                    item {
                        WeeklyProgressCard(
                            completedCount = state.weeklyCompletedCount,
                            totalCount = state.weeklyTotalCount,
                            percentage = state.weeklyPercentage,
                            breakdown = state.prayerWeeklyBreakdown
                        )
                    }
                }

                // Right Column: Prayers Checklist with checkboxes
                LazyColumn(
                    modifier = Modifier.weight(1.1f),
                    contentPadding = PaddingValues(bottom = AppDimensions.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                ) {
                    item {
                        PrayerChecklistSection(
                            formattedDate = state.selectedDateFormatted,
                            isToday = state.isToday,
                            completedCount = state.currentDayRecord.completedCount,
                            prayers = state.prayersList,
                            onTogglePrayer = viewModel::togglePrayer,
                            onMarkAll = viewModel::markAllPrayers
                        )
                    }
                }
            }
        } else {
            // Portrait Layout
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppDimensions.paddingMedium),
                contentPadding = PaddingValues(top = AppDimensions.paddingSmall, bottom = AppDimensions.paddingExtraLarge),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
            ) {
                item {
                    WeeklyNavigationHeader(
                        weekRangeText = state.weekRangeFormatted,
                        weekOffset = state.weekOffset,
                        onPreviousWeek = { viewModel.changeWeek(-1) },
                        onNextWeek = { viewModel.changeWeek(1) },
                        onJumpToToday = viewModel::jumpToToday
                    )
                }

                item {
                    WeeklyDayStrip(
                        days = state.weekDays,
                        onDaySelected = viewModel::selectDate
                    )
                }

                item {
                    WeeklyProgressCard(
                        completedCount = state.weeklyCompletedCount,
                        totalCount = state.weeklyTotalCount,
                        percentage = state.weeklyPercentage,
                        breakdown = state.prayerWeeklyBreakdown
                    )
                }

                item {
                    PrayerChecklistSection(
                        formattedDate = state.selectedDateFormatted,
                        isToday = state.isToday,
                        completedCount = state.currentDayRecord.completedCount,
                        prayers = state.prayersList,
                        onTogglePrayer = viewModel::togglePrayer,
                        onMarkAll = viewModel::markAllPrayers
                    )
                }
            }
        }
    }
}
