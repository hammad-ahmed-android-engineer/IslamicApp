package com.ne.muslimprayers.qiblafinder.ui.hadith

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicSearchBar
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicTopAppBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState
import com.ne.muslimprayers.qiblafinder.ui.hadith.components.HadithCardItem
import com.ne.muslimprayers.qiblafinder.ui.hadith.components.HadithDetailDialog
import com.ne.muslimprayers.qiblafinder.ui.hadith.components.HadithSectionBar

@Composable
fun HadithScreen(
    viewModel: HadithViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Full Screen Hadith Detail Dialog
    state.selectedHadith?.let { hadith ->
        HadithDetailDialog(
            hadith = hadith,
            onDismiss = { viewModel.selectHadith(null) },
            onCopy = {
                val text = "${hadith.englishText}\n\n[${hadith.source} • ${hadith.chapter}]"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(context, "Hadith", text, "Hadith copied to clipboard")
            },
            onShare = {
                val text = "${hadith.englishText}\n\n— ${hadith.source} (${hadith.chapter})"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(context, text, "Share Hadith via")
            }
        )
    }

    Scaffold(
        topBar = {
            IslamicTopAppBar(
                title = "Authentic Hadiths (الحديث النبوي)"
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
            // Collection Tabs
            ScrollableTabRow(
                selectedTabIndex = state.selectedCollectionIndex,
                containerColor = WarmParchment,
                contentColor = MedinaSage,
                edgePadding = 16.sdp,
                divider = {},
                indicator = { tabPositions ->
                    if (state.selectedCollectionIndex in tabPositions.indices) {
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedCollectionIndex]),
                            color = MedinaSage,
                            height = 2.5.sdp
                        )
                    }
                }
            ) {
                viewModel.collections.forEachIndexed { index, tab ->
                    Tab(
                        selected = state.selectedCollectionIndex == index,
                        onClick = { viewModel.selectCollection(index) },
                        modifier = Modifier.height(36.sdp),
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = 13.sp,
                                fontWeight = if (state.selectedCollectionIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (state.selectedCollectionIndex == index) MedinaSage else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }

            // Compact Search Bar & Chapter Navigation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.sdp)
                    .padding(top = 4.sdp, bottom = 8.sdp)
            ) {
                IslamicSearchBar(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    placeholder = "Search Hadith text, chapter, or narrator...",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.sdp))

                HadithSectionBar(
                    currentSection = state.currentSection,
                    maxSections = state.maxSections,
                    chapterName = state.currentChapterName,
                    onPrev = viewModel::previousSection,
                    onNext = viewModel::nextSection
                )
            }

            if (state.isLoading && state.hadiths.isEmpty()) {
                LoadingState(message = "Fetching authentic Hadith collection...")
            } else if (state.errorMessage != null && state.hadiths.isEmpty()) {
                ErrorState(
                    message = state.errorMessage ?: "Failed to fetch Hadiths",
                    onRetry = {
                        viewModel.loadHadiths(
                            viewModel.collections[state.selectedCollectionIndex].edition,
                            state.currentSection
                        )
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.sdp),
                    contentPadding = PaddingValues(bottom = 32.sdp),
                    verticalArrangement = Arrangement.spacedBy(12.sdp)
                ) {
                    items(state.filteredHadiths) { item ->
                        HadithCardItem(
                            hadith = item,
                            onClick = { viewModel.selectHadith(item) },
                            onCopy = {
                                val text = "${item.englishText}\n\n[${item.source} • ${item.chapter}]"
                                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(context, "Hadith", text, "Hadith copied to clipboard")
                            },
                            onShare = {
                                val text = "${item.englishText}\n\n— ${item.source} (${item.chapter})"
                                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(context, text, "Share Hadith via")
                            }
                        )
                    }
                }
            }
        }
    }
}
