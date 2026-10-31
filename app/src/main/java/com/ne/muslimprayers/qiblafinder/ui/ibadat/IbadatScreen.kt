package com.ne.muslimprayers.qiblafinder.ui.ibadat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
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
                title = "Ibadat & Remembrance (الأذكار والعبادات)"
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
            // Main Top Tabs: Digital Tasbih / Daily Duas
            TabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = WarmParchment,
                contentColor = MedinaSage,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                        color = MedinaSage,
                        height = 2.5.sdp
                    )
                }
            ) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    modifier = Modifier.height(36.sdp),
                    text = {
                        Text(
                            text = "Digital Tasbih",
                            fontSize = 13.sp,
                            fontWeight = if (state.selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (state.selectedTab == 0) MedinaSage else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    modifier = Modifier.height(36.sdp),
                    text = {
                        Text(
                            text = "Fortress of the Muslim (Duas)",
                            fontSize = 13.sp,
                            fontWeight = if (state.selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (state.selectedTab == 1) MedinaSage else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                    selectedCategory = state.selectedCategory,
                    searchQuery = state.searchQuery,
                    isLoading = state.isLoading,
                    errorMessage = state.errorMessage,
                    isLandscape = isLandscape,
                    onRetry = viewModel::loadDuas,
                    onSearchChange = viewModel::onSearchQueryChanged,
                    onSelectCategory = viewModel::selectCategory,
                    onCopy = { dua ->
                        val text =
                            "${dua.arabicText}\n\n${dua.transliteration}\n\n\"${dua.translation}\"\n(${dua.reference})"
                        com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(context, "Dua", text, "Dua copied to clipboard")
                    },
                    onShare = { dua ->
                        val text =
                            "${dua.title}\n\n${dua.arabicText}\n\n${dua.transliteration}\n\n\"${dua.translation}\"\n— Source: ${dua.reference}"
                        com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(context, text, "Share Dua via")
                    }
                )
            }
        }
    }
}
