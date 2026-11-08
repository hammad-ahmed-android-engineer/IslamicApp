package com.ne.muslimprayers.qiblafinder.ui.ibadat.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.AppTypography
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.domain.model.DuaItem
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.ErrorState
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicSearchBar
import com.ne.muslimprayers.qiblafinder.ui.components.LoadingState

private data class DuaCategoryMeta(
    val arabicTitle: String,
    val description: String
)

private fun getDuaCategoryMeta(category: String): DuaCategoryMeta {
    return when (category) {
        "Quranic (Rabbana)" -> DuaCategoryMeta(
            arabicTitle = "أدعية قرآنية (ربنا)",
            description = "Comprehensive supplications revealed in the Holy Quran"
        )
        "Morning & Evening" -> DuaCategoryMeta(
            arabicTitle = "أذكار الصباح والمساء",
            description = "Daily Prophetic remembrances for morning, evening, and sleep"
        )
        "Prayer & Mosque" -> DuaCategoryMeta(
            arabicTitle = "أدعية الصلاة والمسجد",
            description = "Supplications for Wudu, Adhan, entering the Masjid, and Salah"
        )
        "Forgiveness & Mercy" -> DuaCategoryMeta(
            arabicTitle = "الاستغفار والرحمة",
            description = "Sayyidul Istighfar and authentic Duas seeking Allah's pardon"
        )
        "Protection & Hardship" -> DuaCategoryMeta(
            arabicTitle = "التحصين وتفريج الكرب",
            description = "Refuge from anxiety, distress, debt, and trials"
        )
        "Daily Life & Home" -> DuaCategoryMeta(
            arabicTitle = "أدعية المنزل والحياة اليومية",
            description = "Entering/leaving home, dressing, and everyday blessings"
        )
        "Food & Drink" -> DuaCategoryMeta(
            arabicTitle = "أدعية الطعام والشراب",
            description = "Supplications before and after meals, fasting, and hospitality"
        )
        "Travel & Journey" -> DuaCategoryMeta(
            arabicTitle = "أدعية السفر والركوب",
            description = "Protection and blessings when embarking on a journey"
        )
        "Illness & Healing" -> DuaCategoryMeta(
            arabicTitle = "أدعية الشفاء وعيادة المريض",
            description = "Prophetic Ruqyah and supplications for health and recovery"
        )
        "Praise & Remembrance" -> DuaCategoryMeta(
            arabicTitle = "الحمد والتسبيح والذكر",
            description = "Glorifying Allah and expressing gratitude for His favors"
        )
        "Family & Society" -> DuaCategoryMeta(
            arabicTitle = "أدعية الأسرة والأبناء",
            description = "Supplications for parents, spouse, righteous offspring, and brotherhood"
        )
        "Nature & Weather" -> DuaCategoryMeta(
            arabicTitle = "أدعية المطر والظواهر",
            description = "Duas upon seeing the crescent moon, rain, wind, and thunder"
        )
        "Death & Afterlife" -> DuaCategoryMeta(
            arabicTitle = "أدعية الجنازة والآخرة",
            description = "Supplications for the deceased, visiting graves, and steadfastness"
        )
        "All" -> DuaCategoryMeta(
            arabicTitle = "حصن المسلم كاملاً",
            description = "Browse and search the complete Fortress of the Muslim collection"
        )
        else -> DuaCategoryMeta(
            arabicTitle = "أدعية مأثورة",
            description = "Authentic Prophetic supplications and remembrances for $category"
        )
    }
}

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
        val displayTitle = if (selectedCategory == "All") {
            stringResource(R.string.duas_all_categories_card_title)
        } else {
            selectedCategory
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.paddingMedium)
                .padding(top = AppDimensions.paddingSmall, bottom = AppDimensions.paddingSmall)
        ) {
            // Opened Category Header Bar with Back Button
            IslamicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PureAlabaster,
                borderColor = DividerColor,
                cornerRadius = AppDimensions.cornerLarge,
                contentPadding = AppDimensions.paddingSmall
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onCloseCategory,
                            modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconMedium)
                            )
                        }
                        Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                        Column(modifier = Modifier.weight(1f)) {
                            AppText.TitleMedium(
                                text = displayTitle,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            AppText.Caption(
                                text = "${duas.size} Duas • ${meta.arabicTitle}",
                                color = MedinaSage,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }

                    TextButton(onClick = onCloseCategory) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = stringResource(R.string.duas_all_categories),
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                        AppText.LabelMedium(
                            text = stringResource(R.string.duas_all_categories),
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            IslamicSearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = stringResource(R.string.duas_search_placeholder),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            if (isLoading && duas.isEmpty()) {
                LoadingState(message = stringResource(R.string.duas_loading))
            } else if (errorMessage != null && duas.isEmpty()) {
                ErrorState(message = errorMessage, onRetry = onRetry)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    items(duas, key = { it.id }) { item ->
                        DuaItemCard(
                            dua = item,
                            onCopy = { onCopy(item) },
                            onShare = { onShare(item) }
                        )
                    }
                }
            }
        }
    } else {
        // Dua Category Cards Screen (No Horizontal Tabs)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.paddingMedium)
                .padding(top = AppDimensions.paddingSmall, bottom = AppDimensions.paddingSmall)
        ) {
            if (isLoading && categories.isEmpty()) {
                LoadingState(message = stringResource(R.string.duas_loading))
            } else if (errorMessage != null && categories.isEmpty()) {
                ErrorState(message = errorMessage, onRetry = onRetry)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = AppDimensions.paddingExtraLarge),
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
                                        text = stringResource(R.string.duas_select_category_title),
                                        fontWeight = FontWeight.Bold,
                                        color = MedinaSage
                                    )
                                    Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                                    AppText.BodySmall(
                                        text = stringResource(R.string.duas_select_category_subtitle),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    items(categories, key = { it }) { category ->
                        val meta = getDuaCategoryMeta(category)
                        val count = categoryCounts[category] ?: 0
                        val displayTitle = if (category == "All") {
                            stringResource(R.string.duas_all_categories_card_title)
                        } else {
                            category
                        }
                        DuaCategoryCard(
                            title = displayTitle,
                            arabicTitle = meta.arabicTitle,
                            description = meta.description,
                            duaCount = count,
                            onOpenCategory = { onSelectCategory(category) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DuaCategoryCard(
    title: String,
    arabicTitle: String,
    description: String,
    duaCount: Int,
    onOpenCategory: () -> Unit
) {
    IslamicCard(
        backgroundColor = PureAlabaster,
        onClick = onOpenCategory
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
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                        AppText.ArabicText(
                            text = arabicTitle,
                            fontSize = AppTypography.QuranArabicSmall.fontSize,
                            color = MedinaSage,
                            applySafePadding = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            AppText.BodySmall(
                text = description,
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
                        text = stringResource(R.string.duas_count_badge, duaCount),
                        fontWeight = FontWeight.SemiBold,
                        color = DesertOchre,
                        modifier = Modifier.padding(
                            horizontal = AppDimensions.paddingSmall,
                            vertical = AppDimensions.paddingTiny
                        )
                    )
                }

                Button(
                    onClick = onOpenCategory,
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
                        text = stringResource(R.string.duas_open_category),
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
