package com.example.noorapp.ui.more

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppDialog
import com.example.noorapp.core.designsystem.AppDivider
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppTopAppBar
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.ShareUtils
import com.example.noorapp.ui.more.components.AsrJuristicContent
import com.example.noorapp.ui.more.components.BrandingCard
import com.example.noorapp.ui.more.components.CalculationMethodContent
import com.example.noorapp.ui.more.components.HijriOffsetContent
import com.example.noorapp.ui.more.components.MoreSettingItem
import com.example.noorapp.ui.more.components.MoreToggleItem
import com.example.noorapp.ui.more.components.NamesOfAllahContent
import com.example.noorapp.ui.more.components.ZakatCalculatorContent

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = hiltViewModel(),
    onNavigateToSalahTracker: (() -> Unit)? = null,
    onNavigateToPrayerAlarm: (() -> Unit)? = null,
    onNavigateToTasbih: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Dialogs hosted directly with common AppDialog at Screen level
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
                onSelect = viewModel::selectMethod,
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

    if (state.showZakatDialog) {
        AppDialog(
            onDismissRequest = { viewModel.toggleZakatDialog(false) },
            title = stringResource(R.string.zakat_dialog_title),
            headerIcon = Icons.Default.Calculate,
            headerIconBgColor = null,
            showCloseButton = true,
            scrollable = true,
            usePlatformDefaultWidth = false,
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.85f else 0.92f)
                .fillMaxHeight(0.85f)
        ) {
            ZakatCalculatorContent(
                cashSavings = state.cashSavings,
                goldValue = state.goldValue,
                silverValue = state.silverValue,
                investments = state.investments,
                liabilities = state.liabilities,
                zakatResult = state.zakatResult,
                isLandscape = isLandscape,
                onInputChange = { cash, gold, silver, investments, liabilities ->
                    viewModel.updateZakatInputs(cash, gold, silver, investments, liabilities)
                },
                onCalculate = viewModel::calculateZakat
            )
        }
    }

    if (state.showNamesDialog) {
        AppDialog(
            onDismissRequest = { viewModel.toggleNamesDialog(false) },
            title = stringResource(R.string.more_names_of_allah_title),
            headerIcon = Icons.Default.AutoAwesome,
            headerIconTint = DesertOchre,
            headerIconBgColor = null,
            showCloseButton = true,
            scrollable = false,
            usePlatformDefaultWidth = false,
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.88f else 0.92f)
                .fillMaxHeight(0.88f)
        ) {
            NamesOfAllahContent(
                names = state.namesOfAllah,
                playingNameId = state.playingNameId,
                isLandscape = isLandscape,
                onPlayAudio = viewModel::playNameAudio
            )
        }
    }

    val currentMethodName = stringResource(
        viewModel.calculationMethodResIds.getOrElse(state.selectedMethodIndex) {
            R.string.prayers_mwl_short
        }
    )
    val methodBadge = if (state.isMethodManuallySelected) {
        stringResource(R.string.prayers_badge_custom)
    } else {
        stringResource(R.string.prayers_badge_auto_detected)
    }
    val asrBadge = if (state.isAsrManuallySelected) {
        stringResource(R.string.prayers_badge_custom)
    } else {
        stringResource(R.string.prayers_badge_default)
    }
    val selectedAsrOption = viewModel.asrSchools.getOrElse(state.selectedAsrJuristic) { viewModel.asrSchools[0] }
    val selectedAsrName = stringResource(selectedAsrOption.titleRes)
    val hijriSubtitle = when {
        state.selectedHijriOffset > 0 -> stringResource(R.string.more_hijri_offset_pos_saved, state.selectedHijriOffset)
        state.selectedHijriOffset < 0 -> stringResource(R.string.more_hijri_offset_neg_saved, state.selectedHijriOffset)
        else -> stringResource(R.string.more_hijri_offset_zero_calc)
    }

    Scaffold(
        topBar = {
            AppTopAppBar(title = stringResource(R.string.more_screen_title))
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = AppDimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
            ) {
                // Left column: Noor Branding Hero & Prayer Settings Card
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        top = AppDimensions.paddingSmall,
                        bottom = AppDimensions.paddingLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                ) {
                    item { BrandingCard() }

                    item {
                        PrayerSettingsGroup(
                            currentMethodName = currentMethodName,
                            methodBadge = methodBadge,
                            selectedAsrName = selectedAsrName,
                            asrBadge = asrBadge,
                            hijriSubtitle = hijriSubtitle,
                            notificationsEnabled = state.notificationsEnabled,
                            onMethodClick = { viewModel.toggleMethodDialog(true) },
                            onAsrClick = { viewModel.toggleAsrDialog(true) },
                            onHijriClick = { viewModel.toggleHijriOffsetDialog(true) },
                            onToggleNotifications = viewModel::toggleNotifications,
                            onNavigateToPrayerAlarm = onNavigateToPrayerAlarm
                        )
                    }
                }

                // Right column: Islamic Utilities & About/Support Cards
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        top = AppDimensions.paddingSmall,
                        bottom = AppDimensions.paddingLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
                ) {
                    item {
                        IslamicUtilitiesGroup(
                            onNamesClick = { viewModel.toggleNamesDialog(true) },
                            onNavigateToTasbih = onNavigateToTasbih,
                            onNavigateToSalahTracker = onNavigateToSalahTracker,
                            onZakatClick = { viewModel.toggleZakatDialog(true) }
                        )
                    }

                    item {
                        AboutSupportGroup(context = context)
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
                    top = AppDimensions.paddingSmall,
                    bottom = AppDimensions.paddingLarge
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingMediumSmall)
            ) {
                item { BrandingCard() }

                item {
                    PrayerSettingsGroup(
                        currentMethodName = currentMethodName,
                        methodBadge = methodBadge,
                        selectedAsrName = selectedAsrName,
                        asrBadge = asrBadge,
                        hijriSubtitle = hijriSubtitle,
                        notificationsEnabled = state.notificationsEnabled,
                        onMethodClick = { viewModel.toggleMethodDialog(true) },
                        onAsrClick = { viewModel.toggleAsrDialog(true) },
                        onHijriClick = { viewModel.toggleHijriOffsetDialog(true) },
                        onToggleNotifications = viewModel::toggleNotifications,
                        onNavigateToPrayerAlarm = onNavigateToPrayerAlarm
                    )
                }

                item {
                    IslamicUtilitiesGroup(
                        onNamesClick = { viewModel.toggleNamesDialog(true) },
                        onNavigateToTasbih = onNavigateToTasbih,
                        onNavigateToSalahTracker = onNavigateToSalahTracker,
                        onZakatClick = { viewModel.toggleZakatDialog(true) }
                    )
                }

                item {
                    AboutSupportGroup(context = context)
                }
            }
        }
    }
}

@Composable
private fun PrayerSettingsGroup(
    currentMethodName: String,
    methodBadge: String,
    selectedAsrName: String,
    asrBadge: String,
    hijriSubtitle: String,
    notificationsEnabled: Boolean,
    onMethodClick: () -> Unit,
    onAsrClick: () -> Unit,
    onHijriClick: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onNavigateToPrayerAlarm: (() -> Unit)?
) {
    SettingsSectionCard(title = stringResource(R.string.more_prayer_settings_section).uppercase()) {
        MoreSettingItem(
            title = stringResource(R.string.prayers_calculation_method),
            badgeText = methodBadge,
            subtitle = currentMethodName,
            icon = Icons.Default.Tune,
            iconTint = MedinaSage,
            iconBgColor = MedinaSageLight,
            onClick = onMethodClick
        )
        SettingsGroupDivider()
        MoreSettingItem(
            title = stringResource(R.string.prayers_asr_juristic),
            badgeText = asrBadge,
            subtitle = selectedAsrName,
            icon = Icons.Default.Scale,
            iconTint = DesertOchre,
            iconBgColor = DesertOchreLight,
            onClick = onAsrClick
        )
        SettingsGroupDivider()
        MoreSettingItem(
            title = stringResource(R.string.more_hijri_adjustment_label),
            subtitle = hijriSubtitle,
            icon = Icons.Default.CalendarMonth,
            iconTint = Color(0xFF1565C0),
            iconBgColor = Color(0xFFE8F1FB),
            onClick = onHijriClick
        )
        SettingsGroupDivider()
        MoreToggleItem(
            title = stringResource(R.string.more_notifications_label),
            subtitle = stringResource(R.string.more_notifications_subtitle),
            icon = Icons.Default.Notifications,
            iconTint = Color(0xFF00796B),
            iconBgColor = Color(0xFFE4F4F2),
            isChecked = notificationsEnabled,
            onCheckedChange = onToggleNotifications
        )
        if (onNavigateToPrayerAlarm != null) {
            SettingsGroupDivider()
            MoreSettingItem(
                title = stringResource(R.string.more_per_prayer_alarms_title),
                subtitle = stringResource(R.string.more_per_prayer_alarms_subtitle),
                icon = Icons.Default.NotificationsActive,
                iconTint = Color(0xFFD84315),
                iconBgColor = Color(0xFFFBECE4),
                onClick = onNavigateToPrayerAlarm
            )
        }
    }
}

@Composable
private fun IslamicUtilitiesGroup(
    onNamesClick: () -> Unit,
    onNavigateToTasbih: (() -> Unit)?,
    onNavigateToSalahTracker: (() -> Unit)?,
    onZakatClick: () -> Unit
) {
    SettingsSectionCard(title = stringResource(R.string.more_islamic_utilities_section).uppercase()) {
        if (onNavigateToTasbih != null) {
            MoreSettingItem(
                title = stringResource(R.string.more_tasbih_title),
                subtitle = stringResource(R.string.more_tasbih_subtitle),
                icon = Icons.Default.SelfImprovement,
                iconTint = Color(0xFF00796B),
                iconBgColor = Color(0xFFE4F4F2),
                onClick = onNavigateToTasbih
            )
            SettingsGroupDivider()
        }
        MoreSettingItem(
            title = stringResource(R.string.more_names_of_allah_title),
            subtitle = stringResource(R.string.more_names_of_allah_subtitle),
            icon = Icons.Default.AutoAwesome,
            iconTint = DesertOchre,
            iconBgColor = DesertOchreLight,
            onClick = onNamesClick
        )
        if (onNavigateToSalahTracker != null) {
            SettingsGroupDivider()
            MoreSettingItem(
                title = stringResource(R.string.more_salah_tracker_title),
                subtitle = stringResource(R.string.more_salah_tracker_subtitle),
                icon = Icons.Default.Checklist,
                iconTint = MedinaSage,
                iconBgColor = MedinaSageLight,
                onClick = onNavigateToSalahTracker
            )
        }
        SettingsGroupDivider()
        MoreSettingItem(
            title = stringResource(R.string.more_zakat_calculator),
            subtitle = stringResource(R.string.more_zakat_calculator_subtitle),
            icon = Icons.Default.Calculate,
            iconTint = Color(0xFF7B1FA2),
            iconBgColor = Color(0xFFF3E8FA),
            onClick = onZakatClick
        )
    }
}

@Composable
private fun AboutSupportGroup(context: Context) {
    SettingsSectionCard(title = stringResource(R.string.more_about_support_section).uppercase()) {
        MoreSettingItem(
            title = stringResource(R.string.more_share_app_title),
            subtitle = stringResource(R.string.more_share_app_subtitle),
            icon = Icons.Default.Share,
            iconTint = Color(0xFF1565C0),
            iconBgColor = Color(0xFFE8F1FB),
            onClick = {
                ShareUtils.shareText(
                    context,
                    context.getString(R.string.more_share_app_body),
                    context.getString(R.string.more_share_app_chooser)
                )
            }
        )
        SettingsGroupDivider()
        MoreSettingItem(
            title = stringResource(R.string.more_privacy_policy_title),
            subtitle = stringResource(R.string.more_privacy_policy_subtitle),
            icon = Icons.Default.PrivacyTip,
            iconTint = Color(0xFF00796B),
            iconBgColor = Color(0xFFE4F4F2),
            onClick = { openPrivacyPolicy(context) }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppText.SectionHeader(
            text = title,
            color = MedinaSage,
            modifier = Modifier.padding(
                start = AppDimensions.paddingTiny,
                top = AppDimensions.paddingMicro,
                bottom = AppDimensions.paddingExtraSmall
            )
        )
        AppCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PureAlabaster,
            borderColor = DividerColor,
            cornerRadius = AppDimensions.cornerExtraLarge,
            elevation = AppDimensions.elevationSubtle,
            contentPadding = AppDimensions.paddingZero
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsGroupDivider() {
    AppDivider(
        color = DividerColor.copy(alpha = 0.7f),
        thickness = AppDimensions.dividerHairline,
        modifier = Modifier.padding(horizontal = AppDimensions.paddingMedium)
    )
}

private fun openPrivacyPolicy(context: Context) {
    Toast.makeText(
        context,
        context.getString(R.string.more_privacy_policy_toast),
        Toast.LENGTH_LONG
    ).show()
}
