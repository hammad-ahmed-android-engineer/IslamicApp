package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter

@Composable
fun AyahCardItem(
    ayah: Ayah,
    surahNumber: Int = 1,
    arabicFontSize: Float,
    translationFontSize: Float = 15f,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    showUrduTranslation: Boolean = true,
    selectedQuranFont: String = "muhammadi",
    useIndoPakScript: Boolean = true,
    isColoredQuran: Boolean = true,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onPlay: () -> Unit,
    onBookmark: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onOpenTafseer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val effectiveSurahNumber = if (ayah.surahNumber > 0) ayah.surahNumber else surahNumber
    val fontConfig = remember(selectedQuranFont) {
        ArabicFontManager.getTypographyConfig(selectedQuranFont)
    }
    val selectedFontFamily = remember(selectedQuranFont) {
        ArabicFontManager.getFontFamily(selectedQuranFont)
    }

    val isIndoPakEffective = fontConfig.prefersIndoPakScript && ayah.indopakText.isNotBlank()
    val targetArabicText = if (isIndoPakEffective) ayah.indopakText else ayah.arabicText

    val formattedAyahResult = remember(
        targetArabicText,
        ayah.numberInSurah,
        effectiveSurahNumber,
        isColoredQuran,
        selectedQuranFont
    ) {
        QuranTextFormatter.formatAyahWithInlineEndSign(
            rawArabicText = targetArabicText,
            ayahNumberInSurah = ayah.numberInSurah,
            surahNumber = effectiveSurahNumber,
            isColored = isColoredQuran,
            waqfHighlightColor = QuranTextFormatter.TAJWEED_WAQF_COLOR,
            fontKey = selectedQuranFont
        )
    }

    val formattedArabicText = formattedAyahResult.annotatedString
    val endingSpecialMark = formattedAyahResult.endingSpecialMark

    val density = LocalDensity.current
    val scaledArabicSize = arabicFontSize * fontConfig.fontSizeScale
    val rosetteSize = (scaledArabicSize * 1.05f).coerceIn(24f, 40f)
    val rosetteWidth = if (!endingSpecialMark.isNullOrBlank() && endingSpecialMark.length > 1) {
        rosetteSize * 1.24f
    } else {
        rosetteSize * 1.08f
    }
    val inlinePlaceholderHeight = if (!endingSpecialMark.isNullOrBlank()) rosetteSize * 1.72f else rosetteSize
    val rosetteWidthSp = with(density) { rosetteWidth.dp.toSp() }
    val rosetteHeightSp = with(density) { inlinePlaceholderHeight.dp.toSp() }
    val waqfBadgeColor = if (isColoredQuran) QuranTextFormatter.TAJWEED_WAQF_COLOR else TextPrimary

    val inlineContent = remember(
        ayah.numberInSurah,
        rosetteSize,
        rosetteWidthSp,
        rosetteHeightSp,
        arabicFontSize,
        selectedFontFamily,
        isIndoPakEffective,
        isColoredQuran,
        endingSpecialMark,
        waqfBadgeColor
    ) {
        buildMap {
            put(
                QuranTextFormatter.INLINE_AYAH_END_ID,
                InlineTextContent(
                    Placeholder(
                        width = rosetteWidthSp,
                        height = rosetteHeightSp,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        QuranAyatEndRosette(
                            ayahNumber = ayah.numberInSurah,
                            size = rosetteSize.dp,
                            rosetteColor = if (isColoredQuran) DesertOchre else TextPrimary,
                            textColor = if (isColoredQuran) DesertOchre else TextPrimary,
                            endingSpecialMark = endingSpecialMark,
                            fontFamily = selectedFontFamily,
                            useIndoPakDigits = isIndoPakEffective
                        )
                    }
                }
            )

            QuranTextFormatter.WAQF_INLINE_ENTRIES.forEach { (id, label) ->
                val widthFactor = if (label.length > 1) 1.05f else 0.72f
                put(
                    id,
                    InlineTextContent(
                        Placeholder(
                            width = (arabicFontSize * widthFactor).sp,
                            height = (arabicFontSize * 1.10f).sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(bottom = AppDimensions.paddingTiny),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            AppText.SpecialMarkBadge(
                                text = label,
                                fontSize = (arabicFontSize * 0.44f).coerceIn(11f, 18f).sp,
                                color = waqfBadgeColor,
                                fontFamily = selectedFontFamily
                            )
                        }
                    }
                )
            }
        }
    }

    val hasAnyTranslationVisible =
        (showTransliteration && ayah.transliteration.isNotBlank()) ||
                (showUrduTranslation && ayah.urduText.isNotBlank()) ||
                (showTranslation && ayah.englishText.isNotBlank())

    IslamicCard(
        backgroundColor = if (isPlaying) MedinaSageLight.copy(alpha = 0.55f) else PureAlabaster,
        borderColor = if (isPlaying) MedinaSage else DividerColor,
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Top Verse Header Strip: Medallion Badge + Juz Pill + Circular Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                    .background(
                        if (isPlaying) PureAlabaster.copy(alpha = 0.85f)
                        else WarmParchment.copy(alpha = 0.65f)
                    )
                    .border(
                        width = AppDimensions.borderWidthHairline,
                        color = if (isPlaying) MedinaSage.copy(alpha = 0.35f) else DividerColor.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(AppDimensions.cornerMedium)
                    )
                    .padding(
                        horizontal = AppDimensions.paddingSmall,
                        vertical = AppDimensions.paddingExtraSmall
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Surah:Ayah Badge & Juz Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                ) {
                    Surface(
                        color = MedinaSage,
                        shape = RoundedCornerShape(AppDimensions.cornerSmall)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = AppDimensions.paddingRegular,
                                vertical = AppDimensions.paddingTiny
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.accentBarWidth)
                        ) {
                            AppText.BodySmall(
                                text = "$effectiveSurahNumber:${ayah.numberInSurah}",
                                fontWeight = FontWeight.Bold,
                                color = PureAlabaster
                            )
                        }
                    }

                    if (ayah.juz > 0) {
                        Surface(
                            color = DesertOchreLight.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(AppDimensions.cornerExtraSmall),
                            border = BorderStroke(
                                AppDimensions.borderWidthHairline,
                                DesertOchre.copy(alpha = 0.3f)
                            )
                        ) {
                            AppText.Caption(
                                text = "Juz ${ayah.juz}",
                                fontWeight = FontWeight.SemiBold,
                                color = DesertOchre,
                                modifier = Modifier.padding(
                                    horizontal = AppDimensions.paddingSmall,
                                    vertical = AppDimensions.paddingTiny
                                )
                            )
                        }
                    }
                }

                // Right: Compact Circular Action Buttons (Play, Tafseer, Bookmark, Copy, Share)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
                ) {
                    AyahActionCircleButton(
                        icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play Audio",
                        tint = if (isPlaying) PureAlabaster else MedinaSage,
                        containerColor = if (isPlaying) MedinaSage else MedinaSageLight,
                        onClick = onPlay
                    )
                    AyahActionCircleButton(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Tafseer & Explanation",
                        tint = DesertOchre,
                        containerColor = DesertOchreLight.copy(alpha = 0.65f),
                        onClick = onOpenTafseer
                    )
                    AyahActionCircleButton(
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) DesertOchre else TextSecondary,
                        containerColor = if (isBookmarked) DesertOchreLight.copy(alpha = 0.75f) else PureAlabaster,
                        onClick = onBookmark
                    )
                    AyahActionCircleButton(
                        icon = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = TextSecondary,
                        containerColor = PureAlabaster,
                        onClick = onCopy
                    )
                    AyahActionCircleButton(
                        icon = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextSecondary,
                        containerColor = PureAlabaster,
                        onClick = onShare
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

            // 2. Sacred Quranic Arabic Verse Container with safe per-font overhang padding & RTL layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerMediumLarge))
                    .background(
                        if (isPlaying) PureAlabaster.copy(alpha = 0.9f)
                        else WarmParchment.copy(alpha = 0.45f)
                    )
                    .border(
                        width = AppDimensions.borderWidthHairline,
                        color = if (isPlaying) MedinaSage.copy(alpha = 0.4f) else DesertOchre.copy(alpha = 0.28f),
                        shape = RoundedCornerShape(AppDimensions.cornerMediumLarge)
                    )
                    .padding(
                        horizontal = (fontConfig.horizontalSafePaddingDp + 2f).dp,
                        vertical = (fontConfig.verticalSafePaddingDp + 6f).dp
                    )
            ) {
                AppText.AnnotatedArabicText(
                    text = formattedArabicText,
                    inlineContent = inlineContent,
                    fontSize = arabicFontSize.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Start,
                    fontKeyOverride = selectedQuranFont,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Translations & Transliteration Section
            if (hasAnyTranslationVisible) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))
                HorizontalDivider(
                    color = DividerColor.copy(alpha = 0.55f),
                    thickness = AppDimensions.dividerHairline
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                ) {
                    // Transliteration (Phonetic Latin)
                    if (showTransliteration && ayah.transliteration.isNotBlank()) {
                        AyahAccentQuoteRow(accentColor = DesertOchre.copy(alpha = 0.65f)) {
                            AppText.BodySmall(
                                text = ayah.transliteration,
                                fontSizeOverride = (translationFontSize * 0.92f).sp,
                                lineHeightOverride = (translationFontSize * 1.4f).sp,
                                fontStyle = FontStyle.Italic,
                                color = MedinaSage,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Urdu Translation (Nastaleeq script) scaled dynamically with translationFontSize
                    if (showUrduTranslation && ayah.urduText.isNotBlank()) {
                        val urduSize = (translationFontSize * 1.2f).coerceIn(14f, 32f)
                        val urduLineHeight = urduSize * 1.95f
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            AyahAccentQuoteRow(accentColor = DesertOchre.copy(alpha = 0.55f)) {
                                AppText.UrduText(
                                    text = ayah.urduText,
                                    fontSize = urduSize.sp,
                                    lineHeight = urduLineHeight.sp,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = AppDimensions.paddingMicro)
                                )
                            }
                        }
                    }

                    // English Translation (Saheeh International) scaled dynamically with translationFontSize
                    if (showTranslation && ayah.englishText.isNotBlank()) {
                        val engSize = translationFontSize.coerceIn(12f, 26f)
                        val engLineHeight = engSize * 1.48f
                        AyahAccentQuoteRow(accentColor = MedinaSage.copy(alpha = 0.55f)) {
                            AppText.BodyMedium(
                                text = ayah.englishText,
                                fontSizeOverride = engSize.sp,
                                lineHeightOverride = engLineHeight.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = AppDimensions.paddingHairline)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AyahAccentQuoteRow(
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .width(AppDimensions.accentBarWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(AppDimensions.cornerMicro))
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
private fun AyahActionCircleButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(AppDimensions.actionCircleSize)
            .clip(CircleShape)
            .background(containerColor)
            .border(AppDimensions.borderWidthHairline, DividerColor.copy(alpha = 0.6f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(AppDimensions.iconSmall)
        )
    }
}
