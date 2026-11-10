package com.ne.muslimprayers.qiblafinder.ui.calendar

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.calendar.components.CalendarDayChip
import com.ne.muslimprayers.qiblafinder.ui.calendar.components.CalendarPrayerTimingRow
import com.ne.muslimprayers.qiblafinder.ui.calendar.components.CalendarSelectedDayCard
import com.ne.muslimprayers.qiblafinder.ui.calendar.components.MonthSelectorBar
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import java.text.DateFormatSymbols

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = "Islamic Calendar",
                onBackClick = onBackClick
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (state.isLoading && state.calendarDays.isEmpty()) {
            LoadingState(modifier = Modifier.padding(paddingValues), message = "Loading Islamic calendar...")
        } else if (state.errorMessage != null && state.calendarDays.isEmpty()) {
            ErrorState(
                modifier = Modifier.padding(paddingValues),
                message = state.errorMessage ?: "Failed to load calendar",
                onRetry = { viewModel.loadCalendar(state.selectedYear, state.selectedMonth) }
            )
        } else {
            val selected = state.selectedDay
            val monthName = DateFormatSymbols().months.getOrNull(state.selectedMonth - 1) ?: ""

            if (isLandscape) {
                // Landscape 2-Column Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(top = AppDimensions.paddingTiny, bottom = AppDimensions.paddingLarge),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                    ) {
                        item {
                            MonthSelectorBar(
                                monthName = monthName,
                                selectedYear = state.selectedYear,
                                selected = selected,
                                onPrevMonth = {
                                    val prevMonth = if (state.selectedMonth == 1) 12 else state.selectedMonth - 1
                                    val prevYear = if (state.selectedMonth == 1) state.selectedYear - 1 else state.selectedYear
                                    viewModel.loadCalendar(prevYear, prevMonth)
                                },
                                onNextMonth = {
                                    val nextMonth = if (state.selectedMonth == 12) 1 else state.selectedMonth + 1
                                    val nextYear = if (state.selectedMonth == 12) state.selectedYear + 1 else state.selectedYear
                                    viewModel.loadCalendar(nextYear, nextMonth)
                                }
                            )
                        }

                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(state.calendarDays) { dayInfo ->
                                    CalendarDayChip(
                                        dayInfo = dayInfo,
                                        isSelected = dayInfo.gregorianDay == selected?.gregorianDay,
                                        onClick = { viewModel.selectDay(dayInfo) }
                                    )
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(top = AppDimensions.paddingTiny, bottom = AppDimensions.paddingLarge),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                    ) {
                        selected?.let { day ->
                            item {
                                CalendarSelectedDayCard(day = day)
                            }

                            item {
                                AppText.TitleMedium(
                                    text = "Prayer Timings for Selected Date",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            items(day.prayerTimings) { prayer ->
                                CalendarPrayerTimingRow(prayer = prayer)
                            }
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
                    contentPadding = PaddingValues(top = AppDimensions.paddingTiny, bottom = AppDimensions.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                ) {
                    item {
                        MonthSelectorBar(
                            monthName = monthName,
                            selectedYear = state.selectedYear,
                            selected = selected,
                            onPrevMonth = {
                                val prevMonth = if (state.selectedMonth == 1) 12 else state.selectedMonth - 1
                                val prevYear = if (state.selectedMonth == 1) state.selectedYear - 1 else state.selectedYear
                                viewModel.loadCalendar(prevYear, prevMonth)
                            },
                            onNextMonth = {
                                val nextMonth = if (state.selectedMonth == 12) 1 else state.selectedMonth + 1
                                val nextYear = if (state.selectedMonth == 12) state.selectedYear + 1 else state.selectedYear
                                viewModel.loadCalendar(nextYear, nextMonth)
                            }
                        )
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.calendarDays) { dayInfo ->
                                CalendarDayChip(
                                    dayInfo = dayInfo,
                                    isSelected = dayInfo.gregorianDay == selected?.gregorianDay,
                                    onClick = { viewModel.selectDay(dayInfo) }
                                )
                            }
                        }
                    }

                    selected?.let { day ->
                        item {
                            CalendarSelectedDayCard(day = day)
                        }

                        item {
                            AppText.TitleMedium(
                                text = "Prayer Timings for Selected Date",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        items(day.prayerTimings) { prayer ->
                            CalendarPrayerTimingRow(prayer = prayer)
                        }
                    }
                }
            }
        }
    }
}
