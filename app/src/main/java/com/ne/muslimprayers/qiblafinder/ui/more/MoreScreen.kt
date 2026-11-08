package com.ne.muslimprayers.qiblafinder.ui.more

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.utils.ShareUtils
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.more.components.AsrJuristicDialog
import com.ne.muslimprayers.qiblafinder.ui.more.components.BrandingCard
import com.ne.muslimprayers.qiblafinder.ui.more.components.CalculationMethodDialog
import com.ne.muslimprayers.qiblafinder.ui.more.components.HijriOffsetDialog
import com.ne.muslimprayers.qiblafinder.ui.more.components.MoreSettingItem
import com.ne.muslimprayers.qiblafinder.ui.more.components.MoreToggleItem
import com.ne.muslimprayers.qiblafinder.ui.more.components.NamesOfAllahDialog
import com.ne.muslimprayers.qiblafinder.ui.more.components.ZakatCalculatorDialog

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = hiltViewModel(),
    onNavigateToSalahTracker: (() -> Unit)? = null,
    onNavigateToPrayerAlarm: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Dialogs
    if (state.showMethodDialog) {
        CalculationMethodDialog(
            methods = viewModel.calculationMethods,
            selectedIndex = state.selectedMethodIndex,
            onDismiss = { viewModel.toggleMethodDialog(false) },
            onSelect = viewModel::selectMethod,
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

    if (state.showZakatDialog) {
        ZakatCalculatorDialog(
            cashSavings = state.cashSavings,
            goldValue = state.goldValue,
            silverValue = state.silverValue,
            investments = state.investments,
            liabilities = state.liabilities,
            zakatResult = state.zakatResult,
            onInputChange = { cash, gold, silver, investments, liabilities ->
                viewModel.updateZakatInputs(cash, gold, silver, investments, liabilities)
            },
            onCalculate = viewModel::calculateZakat,
            onDismiss = { viewModel.toggleZakatDialog(false) }
        )
    }

    if (state.showNamesDialog) {
        NamesOfAllahDialog(
            names = state.namesOfAllah,
            playingNameId = state.playingNameId,
            onPlayAudio = viewModel::playNameAudio,
            onDismiss = { viewModel.toggleNamesDialog(false) }
        )
    }

    val currentMethodName = viewModel.calculationMethods.getOrElse(state.selectedMethodIndex) {
        "Muslim World League"
    }
    val methodBadge = if (state.isMethodManuallySelected) "(Custom)" else "(Auto-detected)"

    Scaffold(
        topBar = {
            IslamicTopAppBar(title = stringResource(R.string.more_screen_title))
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
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
                BrandingCard()
            }

            item {
                SectionHeader(stringResource(R.string.more_prayer_settings_section).uppercase())
            }

            item {
                MoreSettingItem(
                    title = "${stringResource(R.string.prayers_calculation_method)} $methodBadge",
                    subtitle = currentMethodName,
                    icon = Icons.Default.Tune,
                    onClick = { viewModel.toggleMethodDialog(true) }
                )
            }

            item {
                val asrBadge = if (state.isAsrManuallySelected) "(Custom)" else "(Default)"
                MoreSettingItem(
                    title = "${stringResource(R.string.prayers_asr_juristic)} $asrBadge",
                    subtitle = viewModel.asrSchools.getOrElse(state.selectedAsrJuristic) { viewModel.asrSchools[0] }.first,
                    icon = Icons.Default.Scale,
                    onClick = { viewModel.toggleAsrDialog(true) }
                )
            }

            item {
                MoreSettingItem(
                    title = stringResource(R.string.more_hijri_adjustment_label),
                    subtitle = when {
                        state.selectedHijriOffset > 0 -> "+${state.selectedHijriOffset} Days (Saved)"
                        state.selectedHijriOffset < 0 -> "${state.selectedHijriOffset} Days (Saved)"
                        else -> "0 Days (Calculated)"
                    },
                    icon = Icons.Default.CalendarMonth,
                    onClick = { viewModel.toggleHijriOffsetDialog(true) }
                )
            }

            item {
                MoreToggleItem(
                    title = stringResource(R.string.more_notifications_label),
                    subtitle = "Master switch for daily prayer & Adhan notifications",
                    icon = Icons.Default.Notifications,
                    isChecked = state.notificationsEnabled,
                    onCheckedChange = viewModel::toggleNotifications
                )
            }

            if (onNavigateToPrayerAlarm != null) {
                item {
                    MoreSettingItem(
                        title = "Per-Prayer Notifications & Adhan",
                        subtitle = "Enable/disable specific prayer alarms & choose Adhan or tone",
                        icon = Icons.Default.NotificationsActive,
                        onClick = onNavigateToPrayerAlarm
                    )
                }
            }

            item {
                SectionHeader("ISLAMIC UTILITIES & CONTENT")
            }

            item {
                MoreSettingItem(
                    title = "99 Names of Allah (أسماء الله الحسنى)",
                    subtitle = "Listen to audio recitations and meanings",
                    icon = Icons.Default.AutoAwesome,
                    onClick = { viewModel.toggleNamesDialog(true) }
                )
            }

            if (onNavigateToSalahTracker != null) {
                item {
                    MoreSettingItem(
                        title = "Salah Tracker (متابعة الصلاة)",
                        subtitle = "Track daily prayers, checklists, and weekly consistency",
                        icon = Icons.Default.Checklist,
                        onClick = onNavigateToSalahTracker
                    )
                }
            }

            item {
                MoreSettingItem(
                    title = "Zakat Calculator",
                    subtitle = "Calculate 2.5% annual Zakat on wealth and assets",
                    icon = Icons.Default.Calculate,
                    onClick = { viewModel.toggleZakatDialog(true) }
                )
            }

            item {
                SectionHeader("ABOUT & SUPPORT")
            }

            item {
                MoreSettingItem(
                    title = "Share Islamic App",
                    subtitle = "Invite family and friends to install Islamic App",
                    icon = Icons.Default.Share,
                    onClick = {
                        ShareUtils.shareText(
                            context,
                            "Check out Islamic App for accurate prayer times, Qibla compass, Holy Quran, and authentic Hadiths!",
                            "Share Islamic App via"
                        )
                    }
                )
            }

            item {
                MoreSettingItem(
                    title = "Privacy Policy",
                    subtitle = "Read data privacy & security terms",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { openPrivacyPolicy(context) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    AppText.SectionHeader(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(
            top = AppDimensions.paddingSmall,
            bottom = AppDimensions.paddingMicro
        )
    )
}

private fun openPrivacyPolicy(context: Context) {
    Toast.makeText(
        context,
        "Privacy Policy: All user location data is processed locally on device.",
        Toast.LENGTH_LONG
    ).show()
}
