package com.ne.muslimprayers.qiblafinder.ui.ibadat

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.ibadat.components.DuasTabContent
import com.ne.muslimprayers.qiblafinder.ui.ibadat.components.TasbihTabContent

@Composable
fun IbadatScreen(
    viewModel: IbadatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = stringResource(R.string.ibadat_title)
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main Top Tabs: Digital Tasbih / Fortress of the Muslim (Duas)
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = WarmParchment,
                contentColor = MedinaSage,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                        color = MedinaSage,
                        height = AppDimensions.borderWidthThick
                    )
                }
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    modifier = Modifier.height(AppDimensions.buttonSmallHeight),
                    text = {
                        AppText.BodyMedium(
                            text = stringResource(R.string.ibadat_tab_tasbih),
                            fontWeight = if (state.selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (state.selectedTab == 0) MedinaSage else TextSecondary,
                            maxLines = 1
                        )
                    }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    modifier = Modifier.height(AppDimensions.buttonSmallHeight),
                    text = {
                        AppText.BodyMedium(
                            text = stringResource(R.string.ibadat_tab_duas),
                            fontWeight = if (state.selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (state.selectedTab == 1) MedinaSage else TextSecondary,
                            maxLines = 1
                        )
                    }
                )
            }

            if (state.selectedTab == 0) {
                TasbihTabContent(
                    tasbih = state.tasbih,
                    isLandscape = isLandscape,
                    onTap = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.incrementTasbih()
                    },
                    onReset = viewModel::resetTasbih,
                    onSelectPhrase = viewModel::setPhrase,
                    onSetTarget = viewModel::setTarget
                )
            } else {
                DuasTabContent(
                    duas = state.filteredDuas,
                    categories = state.categories,
                    categoryCounts = state.categoryCounts,
                    selectedCategory = state.selectedCategory,
                    isCategoryOpened = state.isCategoryOpened,
                    searchQuery = state.searchQuery,
                    isLoading = state.isLoading,
                    errorMessage = state.errorMessage,
                    isLandscape = isLandscape,
                    onRetry = viewModel::loadDuas,
                    onSearchChange = viewModel::onSearchQueryChanged,
                    onSelectCategory = viewModel::selectCategory,
                    onCloseCategory = viewModel::closeCategory,
                    onCopy = { dua ->
                        val text =
                            "${dua.arabicText}\n\n${dua.transliteration}\n\n\"${dua.translation}\"\n(${dua.reference})"
                        com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(
                            context,
                            "Dua",
                            text,
                            context.getString(R.string.dua_copied)
                        )
                    },
                    onShare = { dua ->
                        val text =
                            "${dua.title}\n\n${dua.arabicText}\n\n${dua.transliteration}\n\n\"${dua.translation}\"\n— Source: ${dua.reference}"
                        com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(
                            context,
                            text,
                            context.getString(R.string.share_dua_via)
                        )
                    }
                )
            }
        }
    }
}
