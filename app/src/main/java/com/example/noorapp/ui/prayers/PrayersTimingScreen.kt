package com.example.noorapp.ui.prayers

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppDialog
import com.example.noorapp.core.designsystem.AppErrorState
import com.example.noorapp.core.designsystem.AppLoadingState
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppTopAppBar
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.clickableDebounced
import com.example.noorapp.ui.more.components.AsrJuristicContent
import com.example.noorapp.ui.more.components.CalculationMethodContent
import com.example.noorapp.ui.more.components.HijriOffsetContent
import com.example.noorapp.ui.prayers.components.CalculationMethodSelectorCard
import com.example.noorapp.ui.prayers.components.HijriHeaderCard
import com.example.noorapp.ui.prayers.components.PrayerTimingItemCard

@Composable
fun PrayersTimingScreen(
    viewModel: PrayersViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (state.showMethodDialog) {
        AppDialog(
            onDismissRequest = { viewModel.toggleMethodDialog(false) },
            title = stringResource(R.string.prayers_select_calculation_method),
            showCloseButton = true,
            scrollable = true,
            backgroundColor = MaterialTheme.colorScheme.surface,
            usePlatformDefaultWidth = !isLandscape,
            modifier = if (isLandscape) {
                Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight(0.88f)
            } else {
                Modifier
            }
        ) {
            CalculationMethodContent(
                methods = viewModel.calculationMethodResIds,
                selectedIndex = state.selectedMethodIndex,
                onSelect = viewModel::selectCalculationMethod,
                isAutoDetected = !state.isMethodManuallySelected,
                onAutoDetect = viewModel::resetMethodToAutoDetect
            )
        }
    }

    if (state.showAsrDialog) {
        AppDialog(
            onDismissRequest = { viewModel.toggleAsrDialog(false) },
            title = stringResource(R.string.prayers_asr_method_dialog_title),
            showCloseButton = true,
            scrollable = true,
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            AsrJuristicContent(
                schools = viewModel.asrSchools,
                selectedIndex = state.selectedAsrJuristic,
                onSelect = viewModel::selectAsrJuristic
            )
        }
    }

    if (state.showHijriOffsetDialog) {
        AppDialog(
            onDismissRequest = { viewModel.toggleHijriOffsetDialog(false) },
            title = stringResource(R.string.more_hijri_adjustment_label),
            showCloseButton = true,
            scrollable = true,
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            HijriOffsetContent(
                offsets = viewModel.hijriOffsets,
                selectedOffset = state.selectedHijriOffset,
                onSelect = viewModel::selectHijriOffset
            )
        }
    }

    val methodName = stringResource(
        viewModel.calculationMethodResIds.getOrElse(state.selectedMethodIndex) {
            R.string.prayers_mwl_fallback
        }
    )
    val asrName = if (state.selectedAsrJuristic == 1) {
        stringResource(R.string.prayers_asr_hanafi_short)
    } else {
        stringResource(R.string.prayers_asr_standard_short)
    }
    val hijriOffsetLabel = when {
        state.selectedHijriOffset > 0 -> stringResource(R.string.more_hijri_offset_pos, state.selectedHijriOffset)
        state.selectedHijriOffset < 0 -> stringResource(R.string.more_hijri_offset_neg, state.selectedHijriOffset)
        else -> stringResource(R.string.more_hijri_offset_zero)
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.prayers_screen_title),
                onBackClick = onBackClick
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (state.isLoading && state.schedule == null) {
            AppLoadingState(modifier = Modifier.padding(paddingValues))
        } else if (state.errorMessage != null && state.schedule == null) {
            AppErrorState(
                modifier = Modifier.padding(paddingValues),
                message = state.errorMessage ?: stringResource(R.string.failed_to_load_timings),
                onRetry = { viewModel.loadData() }
            )
        } else {
            val schedule = state.schedule
            val alarmsMap = state.alarms.associate { it.prayerType to (state.notificationsEnabled && it.isAlarmActive) }

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
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        HijriHeaderCard(
                            hijriDate = schedule?.dateHijri ?: stringResource(R.string.prayers_hijri_fallback),
                            gregorianDate = schedule?.dateGregorian ?: stringResource(R.string.home_today_fallback),
                            hijriOffsetLabel = hijriOffsetLabel,
                            onClick = { viewModel.toggleHijriOffsetDialog(true) }
                        )

                        CalculationMethodSelectorCard(
                            methodName = methodName,
                            asrName = asrName,
                            isAutoDetected = !state.isMethodManuallySelected,
                            onChangeMethod = { viewModel.toggleMethodDialog(true) },
                            onChangeAsr = { viewModel.toggleAsrDialog(true) }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(
                            top = AppDimensions.paddingTiny,
                            bottom = AppDimensions.paddingLarge
                        ),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
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
                        .padding(horizontal = AppDimensions.paddingMedium),
                    contentPadding = PaddingValues(
                        top = AppDimensions.paddingTiny,
                        bottom = AppDimensions.paddingLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    item {
                        HijriHeaderCard(
                            hijriDate = schedule?.dateHijri ?: stringResource(R.string.prayers_hijri_fallback),
                            gregorianDate = schedule?.dateGregorian ?: stringResource(R.string.home_today_fallback),
                            hijriOffsetLabel = hijriOffsetLabel,
                            onClick = { viewModel.toggleHijriOffsetDialog(true) }
                        )
                    }

                    item {
                        CalculationMethodSelectorCard(
                            methodName = methodName,
                            asrName = asrName,
                            isAutoDetected = !state.isMethodManuallySelected,
                            onChangeMethod = { viewModel.toggleMethodDialog(true) },
                            onChangeAsr = { viewModel.toggleAsrDialog(true) }
                        )
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
