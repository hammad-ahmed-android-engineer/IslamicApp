package com.example.noorapp.ui.hadith

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppButton
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppDialog
import com.example.noorapp.core.designsystem.AppErrorState
import com.example.noorapp.core.designsystem.AppLoadingState
import com.example.noorapp.core.designsystem.AppSearchBar
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppTextButton
import com.example.noorapp.core.designsystem.AppTopAppBar
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.QuranTypography
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.AppUtils
import com.example.noorapp.ui.hadith.components.HadithBookCard
import com.example.noorapp.ui.hadith.components.HadithBooksHeaderBanner
import com.example.noorapp.ui.hadith.components.HadithDetailContent
import com.example.noorapp.ui.hadith.components.HadithListPane
import com.example.noorapp.ui.hadith.components.HadithSectionBar

@Composable
fun HadithScreen(
    viewModel: HadithViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val currentBook = viewModel.collections.getOrElse(state.selectedCollectionIndex) { viewModel.collections.first() }
    val currentBookTitle = stringResource(currentBook.titleRes)
    val currentBookArabic = stringResource(currentBook.arabicTitleRes)
    val resolvedChapterTitle = state.currentChapterTitle.ifBlank {
        stringResource(R.string.hadith_chapter_format, state.currentSection)
    }
    val chapterDisplayName = stringResource(
        R.string.hadith_book_chapter_format,
        currentBookTitle,
        resolvedChapterTitle
    )

    // Full Hadith Detail Dialog using common AppDialog at Screen level
    state.selectedHadith?.let { hadith ->
        AppDialog(
            onDismissRequest = { viewModel.selectHadith(null) },
            title = hadith.source,
            headerIcon = Icons.Default.AutoStories,
            headerIconBgColor = null,
            showCloseButton = true,
            dismissButtonText = stringResource(R.string.copy),
            onDismissClick = {
                val text = context.getString(
                    R.string.hadith_copy_format,
                    hadith.englishText,
                    hadith.source,
                    hadith.chapter
                )
                AppUtils.copyToClipboard(
                    context,
                    context.getString(R.string.home_hadith_clipboard_label),
                    text,
                    context.getString(R.string.hadith_copied)
                )
            },
            confirmButtonText = stringResource(R.string.share),
            confirmButtonIcon = Icons.Default.Share,
            onConfirmClick = {
                val text = context.getString(
                    R.string.hadith_share_format,
                    hadith.englishText,
                    hadith.source,
                    hadith.chapter
                )
                AppUtils.shareText(
                    context,
                    text,
                    context.getString(R.string.share_hadith_via)
                )
            },
            scrollable = true,
            usePlatformDefaultWidth = false,
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.85f else 0.92f)
                .fillMaxHeight(0.85f)
        ) {
            HadithDetailContent(hadith = hadith)
        }
    }

    if (state.isBookOpened) {
        BackHandler {
            viewModel.closeBook()
        }

        Scaffold(
            topBar = {
                AppTopAppBar(
                    title = stringResource(
                        R.string.hadith_book_title_arabic_format,
                        currentBookTitle,
                        currentBookArabic
                    ),
                    onBackClick = viewModel::closeBook,
                    actions = {
                        AppTextButton(onClick = viewModel::closeBook) {
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
                            .weight(0.38f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = AppDimensions.paddingSmall),
                        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                    ) {
                        AppSearchBar(
                            query = state.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            placeholder = stringResource(R.string.hadith_search_placeholder),
                            modifier = Modifier.fillMaxWidth()
                        )

                        HadithSectionBar(
                            currentSection = state.currentSection,
                            maxSections = state.maxSections,
                            chapterName = chapterDisplayName,
                            onPrev = viewModel::previousSection,
                            onNext = viewModel::nextSection
                        )

                        AppCard(
                            backgroundColor = MedinaSageLight.copy(alpha = 0.45f),
                            borderColor = MedinaSage.copy(alpha = 0.25f),
                            contentPadding = AppDimensions.paddingMediumSmall
                        ) {
                            Column {
                                AppText.TitleSmall(
                                    text = currentBookTitle,
                                    fontWeight = FontWeight.Bold,
                                    color = MedinaSage
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                AppText.Caption(
                                    text = stringResource(currentBook.compilerNameRes),
                                    color = DesertOchre,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                                AppText.BodySmall(
                                    text = stringResource(currentBook.descriptionRes),
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(0.62f)) {
                        HadithListPane(
                            state = state,
                            currentBook = currentBook,
                            onRetry = {
                                viewModel.loadHadiths(
                                    currentBook.edition,
                                    state.currentSection
                                )
                            },
                            onSelectHadith = viewModel::selectHadith,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            } else {
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
                        AppSearchBar(
                            query = state.searchQuery,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            placeholder = stringResource(R.string.hadith_search_placeholder),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                        HadithSectionBar(
                            currentSection = state.currentSection,
                            maxSections = state.maxSections,
                            chapterName = chapterDisplayName,
                            onPrev = viewModel::previousSection,
                            onNext = viewModel::nextSection
                        )
                    }

                    HadithListPane(
                        state = state,
                        currentBook = currentBook,
                        onRetry = {
                            viewModel.loadHadiths(
                                currentBook.edition,
                                state.currentSection
                            )
                        },
                        onSelectHadith = viewModel::selectHadith,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = AppDimensions.paddingMedium)
                    )
                }
            }
        }
    } else {
        // Hadith Books Collection Cards Screen (No Tabs)
        Scaffold(
            topBar = {
                AppTopAppBar(
                    title = stringResource(R.string.hadith_title)
                )
            },
            containerColor = WarmParchment,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            if (isLandscape) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = AppDimensions.paddingMedium),
                    contentPadding = PaddingValues(
                        top = AppDimensions.paddingSmall,
                        bottom = AppDimensions.paddingExtraLarge
                    ),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        HadithBooksHeaderBanner()
                    }

                    itemsIndexed(viewModel.collections) { index, book ->
                        HadithBookCard(
                            bookNumber = index + 1,
                            book = book,
                            onOpenBook = { viewModel.selectCollection(index) }
                        )
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
                        bottom = AppDimensions.paddingExtraLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    item {
                        HadithBooksHeaderBanner()
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
}
