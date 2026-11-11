package com.example.noorapp.ui.ibadat.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppButton
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppIconBadge
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppTextButton
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.QuranTypography
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.ui.quran.util.QuranTextFormatter

data class DuaCategoryMeta(
    val englishTitle: String,
    val arabicTitle: String,
    val description: String
)

@Composable
fun getDuaCategoryMeta(category: String): DuaCategoryMeta {
    return when (category) {
        "Quranic (Rabbana)" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_quranic_title),
            arabicTitle = stringResource(R.string.dua_cat_quranic_arabic),
            description = stringResource(R.string.dua_cat_quranic_desc)
        )
        "Morning & Evening" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_morning_title),
            arabicTitle = stringResource(R.string.dua_cat_morning_arabic),
            description = stringResource(R.string.dua_cat_morning_desc)
        )
        "Prayer & Mosque" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_prayer_title),
            arabicTitle = stringResource(R.string.dua_cat_prayer_arabic),
            description = stringResource(R.string.dua_cat_prayer_desc)
        )
        "Forgiveness & Mercy" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_forgiveness_title),
            arabicTitle = stringResource(R.string.dua_cat_forgiveness_arabic),
            description = stringResource(R.string.dua_cat_forgiveness_desc)
        )
        "Protection & Hardship" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_protection_title),
            arabicTitle = stringResource(R.string.dua_cat_protection_arabic),
            description = stringResource(R.string.dua_cat_protection_desc)
        )
        "Daily Life & Home" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_daily_title),
            arabicTitle = stringResource(R.string.dua_cat_daily_arabic),
            description = stringResource(R.string.dua_cat_daily_desc)
        )
        "Food & Drink" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_food_title),
            arabicTitle = stringResource(R.string.dua_cat_food_arabic),
            description = stringResource(R.string.dua_cat_food_desc)
        )
        "Travel & Journey" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_travel_title),
            arabicTitle = stringResource(R.string.dua_cat_travel_arabic),
            description = stringResource(R.string.dua_cat_travel_desc)
        )
        "Illness & Healing" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_illness_title),
            arabicTitle = stringResource(R.string.dua_cat_illness_arabic),
            description = stringResource(R.string.dua_cat_illness_desc)
        )
        "Praise & Remembrance" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_praise_title),
            arabicTitle = stringResource(R.string.dua_cat_praise_arabic),
            description = stringResource(R.string.dua_cat_praise_desc)
        )
        "Family & Society" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_family_title),
            arabicTitle = stringResource(R.string.dua_cat_family_arabic),
            description = stringResource(R.string.dua_cat_family_desc)
        )
        "Nature & Weather" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_nature_title),
            arabicTitle = stringResource(R.string.dua_cat_nature_arabic),
            description = stringResource(R.string.dua_cat_nature_desc)
        )
        "Death & Afterlife" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.dua_cat_death_title),
            arabicTitle = stringResource(R.string.dua_cat_death_arabic),
            description = stringResource(R.string.dua_cat_death_desc)
        )
        "All" -> DuaCategoryMeta(
            englishTitle = stringResource(R.string.duas_all_categories_card_title),
            arabicTitle = stringResource(R.string.dua_cat_all_arabic),
            description = stringResource(R.string.dua_cat_all_desc)
        )
        else -> DuaCategoryMeta(
            englishTitle = category,
            arabicTitle = stringResource(R.string.dua_cat_default_arabic),
            description = stringResource(R.string.dua_cat_default_desc, category)
        )
    }
}

@Composable
fun CategoryHeaderBar(
    displayTitle: String,
    arabicTitle: String,
    duasCount: Int,
    accent: DuaAccentColors,
    onCloseCategory: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PureAlabaster,
        borderColor = accent.primary.copy(alpha = 0.28f),
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
                AppIconButton(
                    onClick = onCloseCategory,
                    modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = accent.primary,
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
                        text = stringResource(R.string.duas_category_header_format, duasCount, arabicTitle),
                        color = accent.primary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }

            AppTextButton(onClick = onCloseCategory) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = stringResource(R.string.duas_all_categories),
                    tint = accent.primary,
                    modifier = Modifier.size(AppDimensions.iconSmall)
                )
                Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                AppText.LabelMedium(
                    text = stringResource(R.string.duas_all_categories),
                    fontWeight = FontWeight.Bold,
                    color = accent.primary
                )
            }
        }
    }
}

@Composable
fun FortressHeaderBanner() {
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

@Composable
fun DuaCategoryCard(
    title: String,
    arabicTitle: String,
    description: String,
    duaCount: Int,
    accent: DuaAccentColors,
    selectedQuranFont: String,
    onOpenCategory: () -> Unit
) {
    val fontConfig = remember(selectedQuranFont) {
        ArabicFontManager.getTypographyConfig(selectedQuranFont)
    }
    val formattedArabicTitle = remember(arabicTitle, selectedQuranFont) {
        QuranTextFormatter.formatDuaArabicText(
            rawArabicText = arabicTitle,
            fontKey = selectedQuranFont
        )
    }

    AppCard(
        backgroundColor = PureAlabaster,
        borderColor = accent.primary.copy(alpha = 0.25f),
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
                    AppIconBadge(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        tint = accent.primary,
                        backgroundColor = accent.container,
                        borderColor = accent.primary.copy(alpha = 0.35f),
                        borderWidth = AppDimensions.borderWidthThin
                    )

                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                    Column(modifier = Modifier.weight(1f)) {
                        AppText.TitleMedium(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(AppDimensions.paddingMicro))
                        AppText.AnnotatedArabicText(
                            text = formattedArabicTitle,
                            fontSize = QuranTypography.ArabicSmall.fontSize,
                            color = accent.primary,
                            fontKeyOverride = selectedQuranFont,
                            modifier = Modifier.padding(vertical = AppDimensions.scaledDp(fontConfig.verticalSafePaddingDp / 2f))
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
                AppStatusPill(
                    text = stringResource(R.string.duas_count_badge, duaCount),
                    textColor = accent.primary,
                    backgroundColor = accent.container,
                    borderColor = accent.primary.copy(alpha = 0.35f),
                    fontWeight = FontWeight.SemiBold,
                    contentPadding = PaddingValues(
                        horizontal = AppDimensions.paddingSmall,
                        vertical = AppDimensions.paddingTiny
                    )
                )

                AppButton(
                    onClick = onOpenCategory,
                    containerColor = accent.primary,
                    contentColor = PureAlabaster,
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

