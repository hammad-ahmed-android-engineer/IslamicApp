package com.ne.muslimprayers.qiblafinder.ui.hadith

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.AppTypography
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicSearchBar
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
    val currentBook = viewModel.collections.getOrElse(state.selectedCollectionIndex) { viewModel.collections.first() }

    // Full Screen Hadith Detail Dialog
    state.selectedHadith?.let { hadith ->
        HadithDetailDialog(
            hadith = hadith,
            onDismiss = { viewModel.selectHadith(null) },
            onCopy = {
                val text = "${hadith.englishText}\n\n[${hadith.source} • ${hadith.chapter}]"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(
                    context,
                    "Hadith",
                    text,
                    context.getString(R.string.hadith_copied)
                )
            },
            onShare = {
                val text = "${hadith.englishText}\n\n— ${hadith.source} (${hadith.chapter})"
                com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(
                    context,
                    text,
                    context.getString(R.string.share_hadith_via)
                )
            }
        )
    }

    if (state.isBookOpened) {
        BackHandler {
            viewModel.closeBook()
        }

        Scaffold(
            topBar = {
                IslamicTopAppBar(
                    title = "${currentBook.title} (${currentBook.arabicTitle})",
                    onBackClick = viewModel::closeBook,
                    actions = {
                        TextButton(onClick = viewModel::closeBook) {
                            Icon(
                                imageVector = Icons.Default.LibraryBooks,
                                contentDescription = stringResource(R.string.hadith_all_books),
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconSmall)
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                            AppText.LabelMedium(
                                text = stringResource(R.string.hadith_all_books),
                                fontWeight = FontWeight.Bold,
                                color = MedinaSage
                            )
                        }
                    }
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
                // Compact Search Bar & Chapter Navigation
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimensions.paddingMedium)
                        .padding(top = AppDimensions.paddingTiny, bottom = AppDimensions.paddingSmall)
                ) {
                    IslamicSearchBar(
                        query = state.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        placeholder = stringResource(R.string.hadith_search_placeholder),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                    HadithSectionBar(
                        currentSection = state.currentSection,
                        maxSections = state.maxSections,
                        chapterName = state.currentChapterName,
                        onPrev = viewModel::previousSection,
                        onNext = viewModel::nextSection
                    )
                }

                if (state.isLoading && state.hadiths.isEmpty()) {
                    LoadingState(message = stringResource(R.string.hadith_loading))
                } else if (state.errorMessage != null && state.hadiths.isEmpty()) {
                    ErrorState(
                        message = state.errorMessage ?: stringResource(R.string.hadith_failed_load),
                        onRetry = {
                            viewModel.loadHadiths(
                                currentBook.edition,
                                state.currentSection
                            )
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = AppDimensions.paddingMedium),
                        contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        items(state.filteredHadiths) { item ->
                            HadithCardItem(
                                hadith = item,
                                onClick = { viewModel.selectHadith(item) },
                                onCopy = {
                                    val text = "${item.englishText}\n\n[${item.source} • ${item.chapter}]"
                                    com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.copyToClipboard(
                                        context,
                                        "Hadith",
                                        text,
                                        context.getString(R.string.hadith_copied)
                                    )
                                },
                                onShare = {
                                    val text = "${item.englishText}\n\n— ${item.source} (${item.chapter})"
                                    com.ne.muslimprayers.qiblafinder.core.utils.AppUtils.shareText(
                                        context,
                                        text,
                                        context.getString(R.string.share_hadith_via)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Hadith Books Collection Cards Screen (No Tabs)
        Scaffold(
            topBar = {
                IslamicTopAppBar(
                    title = stringResource(R.string.hadith_title)
                )
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
                    top = AppDimensions.paddingSmall,
                    bottom = AppDimensions.paddingExtraLarge
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                item {
                    IslamicCard(
                        backgroundColor = MedinaSageLight.copy(alpha = 0.65f),
                        borderColor = MedinaSage.copy(alpha = 0.25f),
                        cornerRadius = AppDimensions.cornerLarge,
                        contentPadding = AppDimensions.paddingMedium
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(AppDimensions.avatarSize)
                                    .clip(CircleShape)
                                    .background(MedinaSage.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = MedinaSage,
                                    modifier = Modifier.size(AppDimensions.iconMedium)
                                )
                            }
                            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                            Column(modifier = Modifier.weight(1f)) {
                                AppText.TitleMedium(
                                    text = stringResource(R.string.hadith_select_book_title),
                                    fontWeight = FontWeight.Bold,
                                    color = MedinaSage
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                AppText.BodySmall(
                                    text = stringResource(R.string.hadith_select_book_subtitle),
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                itemsIndexed(viewModel.collections) { index, book ->
                    HadithBookCard(
                        bookNumber = index + 1,
                        book = book,
                        onOpenBook = { viewModel.selectCollection(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HadithBookCard(
    bookNumber: Int,
    book: HadithCollectionTab,
    onOpenBook: () -> Unit
) {
    IslamicCard(
        backgroundColor = PureAlabaster,
        onClick = onOpenBook
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.avatarSize)
                            .clip(CircleShape)
                            .background(MedinaSageLight)
                            .border(AppDimensions.borderWidthThin, MedinaSage.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText.TitleSmall(
                            text = bookNumber.toString(),
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage
                        )
                    }

                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                    Column(modifier = Modifier.weight(1f)) {
                        AppText.TitleMedium(
                            text = book.title,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                        AppText.Caption(
                            text = book.compilerName,
                            color = DesertOchre,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                AppText.ArabicText(
                    text = book.arabicTitle,
                    fontSize = AppTypography.QuranArabicSmall.fontSize,
                    color = MedinaSage,
                    applySafePadding = false
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            AppText.BodySmall(
                text = book.description,
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = DesertOchreLight.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(AppDimensions.cornerSmall),
                    modifier = Modifier.border(
                        width = AppDimensions.borderWidthThin,
                        color = DesertOchre.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(AppDimensions.cornerSmall)
                    )
                ) {
                    AppText.LabelSmall(
                        text = stringResource(R.string.hadith_chapters_count, book.maxSections),
                        fontWeight = FontWeight.SemiBold,
                        color = DesertOchre,
                        modifier = Modifier.padding(
                            horizontal = AppDimensions.paddingSmall,
                            vertical = AppDimensions.paddingTiny
                        )
                    )
                }

                Button(
                    onClick = onOpenBook,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedinaSage,
                        contentColor = PureAlabaster
                    ),
                    shape = RoundedCornerShape(AppDimensions.cornerMedium),
                    contentPadding = PaddingValues(
                        horizontal = AppDimensions.paddingMedium,
                        vertical = AppDimensions.paddingTiny
                    )
                ) {
                    AppText.LabelMedium(
                        text = stringResource(R.string.hadith_open_book),
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                }
            }
        }
    }
}
