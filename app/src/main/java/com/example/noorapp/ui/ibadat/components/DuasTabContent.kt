package com.example.noorapp.ui.ibadat.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppErrorState
import com.example.noorapp.core.designsystem.AppLoadingState
import com.example.noorapp.core.designsystem.AppSearchBar
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.QuranTypography
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.domain.model.DuaItem
import com.example.noorapp.ui.quran.util.QuranTextFormatter

@Composable
fun DuasTabContent(
    duas: List<DuaItem>,
    categories: List<String>,
    categoryCounts: Map<String, Int>,
    selectedCategory: String,
    isCategoryOpened: Boolean,
    searchQuery: String,
    isLoading: Boolean,
    errorMessage: String?,
    isLandscape: Boolean,
    selectedQuranFont: String = ArabicFontManager.currentFontKey.value,
    arabicFontSize: Float = 26f,
    translationFontSize: Float = 15f,
    onRetry: () -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onCloseCategory: () -> Unit,
    onCopy: (DuaItem) -> Unit,
    onShare: (DuaItem) -> Unit
) {
    if (isCategoryOpened) {
        BackHandler {
            onCloseCategory()
        }

        val meta = getDuaCategoryMeta(selectedCategory)
        val displayTitle = meta.englishTitle
        val categoryAccent = remember(selectedCategory) { duaCategoryAccent(selectedCategory) }
        val formattedHeaderArabic = remember(meta.arabicTitle, selectedQuranFont) {
            QuranTextFormatter.formatDuaArabicText(
                rawArabicText = meta.arabicTitle,
                fontKey = selectedQuranFont
            )
        }

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimensions.paddingMedium)
                    .padding(top = AppDimensions.paddingSmall, bottom = AppDimensions.paddingSmall),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium)
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.38f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    CategoryHeaderBar(
                        displayTitle = displayTitle,
                        arabicTitle = meta.arabicTitle,
                        duasCount = duas.size,
                        accent = categoryAccent,
                        onCloseCategory = onCloseCategory
                    )

                    AppSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchChange,
                        placeholder = stringResource(R.string.duas_search_placeholder),
                        modifier = Modifier.fillMaxWidth()
                    )

                    AppCard(
                        backgroundColor = categoryAccent.container.copy(alpha = 0.55f),
                        borderColor = categoryAccent.primary.copy(alpha = 0.3f),
                        contentPadding = AppDimensions.paddingMediumSmall
                    ) {
                        Column {
                            AppText.AnnotatedArabicText(
                                text = formattedHeaderArabic,
                                fontSize = QuranTypography.ArabicSmall.fontSize,
                                color = TextPrimary,
                                fontKeyOverride = selectedQuranFont
                            )
                            Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                            AppText.BodySmall(
                                text = meta.description,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(0.62f)) {
                    DuasListPane(
                        duas = duas,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        selectedQuranFont = selectedQuranFont,
                        arabicFontSize = arabicFontSize,
                        translationFontSize = translationFontSize,
                        onRetry = onRetry,
                        onCopy = onCopy,
                        onShare = onShare,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppDimensions.paddingMedium)
                    .padding(top = AppDimensions.paddingSmall, bottom = AppDimensions.paddingSmall)
            ) {
                CategoryHeaderBar(
                    displayTitle = displayTitle,
                    arabicTitle = meta.arabicTitle,
                    duasCount = duas.size,
                    accent = categoryAccent,
                    onCloseCategory = onCloseCategory
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchChange,
                    placeholder = stringResource(R.string.duas_search_placeholder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                DuasListPane(
                    duas = duas,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    selectedQuranFont = selectedQuranFont,
                    arabicFontSize = arabicFontSize,
                    translationFontSize = translationFontSize,
                    onRetry = onRetry,
                    onCopy = onCopy,
                    onShare = onShare,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.paddingMedium)
                .padding(top = AppDimensions.paddingSmall, bottom = AppDimensions.paddingSmall)
        ) {
            if (isLoading && categories.isEmpty()) {
                AppLoadingState(message = stringResource(R.string.duas_loading))
            } else if (errorMessage != null && categories.isEmpty()) {
                AppErrorState(
                    message = errorMessage.ifBlank { stringResource(R.string.duas_failed_load) },
                    onRetry = onRetry
                )
            } else if (isLandscape) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        FortressHeaderBanner()
                    }

                    items(categories, key = { it }) { category ->
                        val meta = getDuaCategoryMeta(category)
                        val count = categoryCounts[category] ?: 0
                        val accent = duaCategoryAccent(category)
                        DuaCategoryCard(
                            title = meta.englishTitle,
                            arabicTitle = meta.arabicTitle,
                            description = meta.description,
                            duaCount = count,
                            accent = accent,
                            selectedQuranFont = selectedQuranFont,
                            onOpenCategory = { onSelectCategory(category) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    item {
                        FortressHeaderBanner()
                    }

                    items(categories, key = { it }) { category ->
                        val meta = getDuaCategoryMeta(category)
                        val count = categoryCounts[category] ?: 0
                        val accent = duaCategoryAccent(category)
                        DuaCategoryCard(
                            title = meta.englishTitle,
                            arabicTitle = meta.arabicTitle,
                            description = meta.description,
                            duaCount = count,
                            accent = accent,
                            selectedQuranFont = selectedQuranFont,
                            onOpenCategory = { onSelectCategory(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DuasListPane(
    duas: List<DuaItem>,
    isLoading: Boolean,
    errorMessage: String?,
    selectedQuranFont: String,
    arabicFontSize: Float,
    translationFontSize: Float,
    onRetry: () -> Unit,
    onCopy: (DuaItem) -> Unit,
    onShare: (DuaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading && duas.isEmpty()) {
        AppLoadingState(
            message = stringResource(R.string.duas_loading),
            modifier = modifier
        )
    } else if (errorMessage != null && duas.isEmpty()) {
        AppErrorState(
            message = errorMessage.ifBlank { stringResource(R.string.duas_failed_load) },
            onRetry = onRetry,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
        ) {
            items(duas, key = { it.id }) { item ->
                DuaItemCard(
                    dua = item,
                    selectedQuranFont = selectedQuranFont,
                    arabicFontSize = arabicFontSize,
                    translationFontSize = translationFontSize,
                    onCopy = { onCopy(item) },
                    onShare = { onShare(item) }
                )
            }
        }
    }
}
