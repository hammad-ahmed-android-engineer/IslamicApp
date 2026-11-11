package com.example.noorapp.ui.hadith.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppButton
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppErrorState
import com.example.noorapp.core.designsystem.AppIconBadge
import com.example.noorapp.core.designsystem.AppLoadingState
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.QuranTypography
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.utils.AppUtils
import com.example.noorapp.domain.model.Hadith
import com.example.noorapp.ui.hadith.HadithCollectionTab
import com.example.noorapp.ui.hadith.HadithUiState

@Composable
fun HadithListPane(
    state: HadithUiState,
    currentBook: HadithCollectionTab,
    onRetry: () -> Unit,
    onSelectHadith: (Hadith) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (state.isLoading && state.hadiths.isEmpty()) {
        AppLoadingState(
            message = stringResource(R.string.hadith_loading),
            modifier = modifier
        )
    } else if (state.errorMessage != null && state.hadiths.isEmpty()) {
        AppErrorState(
            message = state.errorMessage.ifBlank { stringResource(R.string.hadith_failed_load) },
            onRetry = onRetry,
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                top = AppDimensions.paddingTiny,
                bottom = AppDimensions.paddingExtraLarge
            ),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
        ) {
            items(state.filteredHadiths) { item ->
                HadithCardItem(
                    hadith = item,
                    onClick = { onSelectHadith(item) },
                    onCopy = {
                        val text = context.getString(
                            R.string.hadith_copy_format,
                            item.englishText,
                            item.source,
                            item.chapter
                        )
                        AppUtils.copyToClipboard(
                            context,
                            context.getString(R.string.home_hadith_clipboard_label),
                            text,
                            context.getString(R.string.hadith_copied)
                        )
                    },
                    onShare = {
                        val text = context.getString(
                            R.string.hadith_share_format,
                            item.englishText,
                            item.source,
                            item.chapter
                        )
                        AppUtils.shareText(
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

@Composable
fun HadithBooksHeaderBanner() {
    AppCard(
        backgroundColor = MedinaSageLight.copy(alpha = 0.65f),
        borderColor = MedinaSage.copy(alpha = 0.25f),
        cornerRadius = AppDimensions.cornerLarge,
        contentPadding = AppDimensions.paddingMedium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconBadge(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                tint = MedinaSage,
                backgroundColor = MedinaSage.copy(alpha = 0.15f)
            )
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

@Composable
fun HadithBookCard(
    bookNumber: Int,
    book: HadithCollectionTab,
    onOpenBook: () -> Unit
) {
    AppCard(
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
                    AppStatusPill(
                        text = bookNumber.toString(),
                        textColor = MedinaSage,
                        backgroundColor = MedinaSageLight,
                        borderColor = MedinaSage.copy(alpha = 0.35f),
                        cornerRadius = AppDimensions.cornerLarge,
                        contentPadding = PaddingValues(
                            horizontal = AppDimensions.paddingMediumSmall,
                            vertical = AppDimensions.paddingSmall
                        )
                    )

                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                    Column(modifier = Modifier.weight(1f)) {
                        AppText.TitleMedium(
                            text = stringResource(book.titleRes),
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                        AppText.Caption(
                            text = stringResource(book.compilerNameRes),
                            color = DesertOchre,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                AppText.ArabicText(
                    text = stringResource(book.arabicTitleRes),
                    fontSize = QuranTypography.ArabicSmall.fontSize,
                    color = MedinaSage,
                    applySafePadding = false
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            AppText.BodySmall(
                text = stringResource(book.descriptionRes),
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppStatusPill(
                    text = stringResource(R.string.hadith_chapters_count, book.maxSections),
                    textColor = DesertOchre,
                    backgroundColor = DesertOchreLight.copy(alpha = 0.55f),
                    borderColor = DesertOchre.copy(alpha = 0.3f),
                    fontWeight = FontWeight.SemiBold,
                    contentPadding = PaddingValues(
                        horizontal = AppDimensions.paddingSmall,
                        vertical = AppDimensions.paddingTiny
                    )
                )

                AppButton(
                    onClick = onOpenBook,
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

