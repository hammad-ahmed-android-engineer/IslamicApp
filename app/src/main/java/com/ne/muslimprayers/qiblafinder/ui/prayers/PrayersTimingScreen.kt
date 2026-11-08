package com.ne.muslimprayers.qiblafinder.ui.prayers

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import com.ne.muslimprayers.qiblafinder.ui.more.components.AsrJuristicDialog
import com.ne.muslimprayers.qiblafinder.ui.more.components.CalculationMethodDialog
import com.ne.muslimprayers.qiblafinder.ui.more.components.HijriOffsetDialog
import com.ne.muslimprayers.qiblafinder.ui.prayers.components.PrayerTimingItemCard

@Composable
fun PrayersTimingScreen(
    viewModel: PrayersViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (state.showMethodDialog) {
        CalculationMethodDialog(
            methods = viewModel.calculationMethods,
            selectedIndex = state.selectedMethodIndex,
            onDismiss = { viewModel.toggleMethodDialog(false) },
            onSelect = viewModel::selectCalculationMethod,
            isAutoDetected = !state.isMethodManuallySelected,
            onAutoDetect = viewModel::resetMethodToAutoDetect
        )
    }

    if (state.showAsrDialog) {
        AsrJuristicDialog(
            schools = viewModel.asrSchools,
            selectedIndex = state.selectedAsrJuristic,
            onDismiss = { viewModel.toggleAsrDialog(false) },
            onSelect = viewModel::selectAsrJuristic
        )
    }

    if (state.showHijriOffsetDialog) {
        HijriOffsetDialog(
            offsets = viewModel.hijriOffsets,
            selectedOffset = state.selectedHijriOffset,
            onDismiss = { viewModel.toggleHijriOffsetDialog(false) },
            onSelect = viewModel::selectHijriOffset
        )
    }

    val methodName = viewModel.calculationMethods.getOrElse(state.selectedMethodIndex) {
        "Muslim World League (MWL)"
    }
    val asrName = if (state.selectedAsrJuristic == 1) "Hanafi (2x shadow)" else "Standard (Shafi'i 1x)"
    val hijriOffsetLabel = when {
        state.selectedHijriOffset > 0 -> "+${state.selectedHijriOffset} Days"
        state.selectedHijriOffset < 0 -> "${state.selectedHijriOffset} Days"
        else -> "0 Days"
    }

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = stringResource(R.string.prayers_screen_title),
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
                            .padding(top = AppDimensions.paddingTiny),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        IslamicCard(
                            backgroundColor = MedinaSage,
                            borderColor = DesertOchre.copy(alpha = 0.4f),
                            onClick = { viewModel.toggleHijriOffsetDialog(true) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    AppText.TitleMedium(
                                        text = schedule?.dateHijri ?: "1447 AH",
                                        fontWeight = FontWeight.Bold,
                                        color = PureAlabaster,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                                    AppText.BodyMedium(
                                        text = "${schedule?.dateGregorian ?: "Today"} • Hijri Adj: $hijriOffsetLabel",
                                        color = MedinaSageLight,
                                        maxLines = 1
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Adjust Hijri Date",
                                    tint = PureAlabaster,
                                    modifier = Modifier.size(AppDimensions.iconSmall)
                                )
                            }
                        }

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
                        IslamicCard(
                            backgroundColor = MedinaSage,
                            borderColor = DesertOchre.copy(alpha = 0.4f),
                            onClick = { viewModel.toggleHijriOffsetDialog(true) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    AppText.TitleMedium(
                                        text = schedule?.dateHijri ?: "1447 AH",
                                        fontWeight = FontWeight.Bold,
                                        color = PureAlabaster,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                                    AppText.BodyMedium(
                                        text = "${schedule?.dateGregorian ?: "Today"} • Hijri Adj: $hijriOffsetLabel",
                                        color = MedinaSageLight,
                                        maxLines = 1
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Adjust Hijri Date",
                                    tint = PureAlabaster,
                                    modifier = Modifier.size(AppDimensions.iconSmall)
                                )
                            }
                        }
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

@Composable
private fun CalculationMethodSelectorCard(
    methodName: String,
    asrName: String,
    isAutoDetected: Boolean,
    onChangeMethod: () -> Unit,
    onChangeAsr: () -> Unit
) {
    IslamicCard(
        backgroundColor = PureAlabaster
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableDebounced { onChangeMethod() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppText.Caption(
                                text = stringResource(R.string.prayers_calculation_method),
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(AppDimensions.cornerMicro))
                                    .background(
                                        if (isAutoDetected) MedinaSage.copy(alpha = 0.12f)
                                        else DesertOchre.copy(alpha = 0.18f)
                                    )
                                    .padding(
                                        horizontal = AppDimensions.paddingSmall,
                                        vertical = AppDimensions.paddingMicro
                                    )
                            ) {
                                AppText.Caption(
                                    text = if (isAutoDetected) "Auto-detected" else "Custom",
                                    color = if (isAutoDetected) MedinaSage else DesertOchre,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        AppText.BodyMedium(
                            text = methodName,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.prayers_change_method),
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                    .border(
                        AppDimensions.borderWidthThin,
                        MedinaSage.copy(alpha = 0.2f),
                        RoundedCornerShape(AppDimensions.cornerSmall)
                    )
                    .clickableDebounced { onChangeAsr() }
                    .padding(
                        horizontal = AppDimensions.paddingSmall,
                        vertical = AppDimensions.paddingTiny
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = null,
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                    AppText.BodySmall(
                        text = "Asr Juristic: $asrName",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
                AppText.Caption(
                    text = stringResource(R.string.prayers_change_method),
                    color = MedinaSage,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
