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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.UrduTranslationFontFamily
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
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
    val targetArabicText = if (isIndoPakEffective) {
        ayah.indopakText
    } else {
        ayah.arabicText
    }

    val scaledFontSize = arabicFontSize * fontConfig.fontSizeScale
    val scaledLineHeight = scaledFontSize * fontConfig.lineHeightMultiplier

    // Formatted annotated string with Tajweed colors, Waqf marks, and inline Ayah End placeholder
    val formattedArabicText = remember(
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
            waqfHighlightColor = DesertOchre,
            fontKey = selectedQuranFont
        )
    }

    val rosetteSize = (arabicFontSize * 1.18f).coerceIn(24f, 40f)
    val inlineContent = remember(
        ayah.numberInSurah,
        rosetteSize,
        selectedFontFamily,
        isIndoPakEffective,
        isColoredQuran
    ) {
        mapOf(
            QuranTextFormatter.INLINE_AYAH_END_ID to InlineTextContent(
                Placeholder(
                    width = rosetteSize.sp,
                    height = rosetteSize.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                QuranAyatEndRosette(
                    ayahNumber = ayah.numberInSurah,
                    size = rosetteSize.dp,
                    rosetteColor = if (isColoredQuran) DesertOchre else TextPrimary,
                    textColor = if (isColoredQuran) DesertOchre else TextPrimary,
                    fontFamily = selectedFontFamily,
                    useIndoPakDigits = isIndoPakEffective
                )
            }
        )
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
                    .clip(RoundedCornerShape(12.sdp))
                    .background(
                        if (isPlaying) PureAlabaster.copy(alpha = 0.85f)
                        else WarmParchment.copy(alpha = 0.65f)
                    )
                    .border(
                        width = 0.8.dp,
                        color = if (isPlaying) MedinaSage.copy(alpha = 0.35f) else DividerColor.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.sdp)
                    )
                    .padding(horizontal = 8.sdp, vertical = 6.sdp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Surah:Ayah Badge & Juz Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.sdp)
                ) {
                    Surface(
                        color = MedinaSage,
                        shape = RoundedCornerShape(8.sdp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.sdp, vertical = 4.sdp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.sdp)
                        ) {
                            Text(
                                text = "$effectiveSurahNumber:${ayah.numberInSurah}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureAlabaster
                            )
                        }
                    }

                    Surface(
                        color = DesertOchreLight.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(6.sdp),
                        border = BorderStroke(0.6.dp, DesertOchre.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Juz ${ayah.juz}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DesertOchre,
                            modifier = Modifier.padding(horizontal = 7.sdp, vertical = 3.sdp)
                        )
                    }
                }

                // Right: Compact Circular Action Buttons (Play, Tafseer, Bookmark, Copy, Share)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.sdp)
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

            Spacer(modifier = Modifier.height(10.sdp))

            // 2. Sacred Quranic Arabic Verse Container with safe per-font overhang padding & RTL layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.sdp))
                    .background(
                        if (isPlaying) PureAlabaster.copy(alpha = 0.9f)
                        else WarmParchment.copy(alpha = 0.45f)
                    )
                    .border(
                        width = 0.9.dp,
                        color = if (isPlaying) MedinaSage.copy(alpha = 0.4f) else DesertOchre.copy(alpha = 0.28f),
                        shape = RoundedCornerShape(14.sdp)
                    )
                    .padding(
                        horizontal = (fontConfig.horizontalSafePaddingDp + 2f).dp,
                        vertical = (fontConfig.verticalSafePaddingDp + 6f).dp
                    )
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = formattedArabicText,
                        inlineContent = inlineContent,
                        style = TextStyle(
                            fontFamily = selectedFontFamily,
                            fontSize = scaledFontSize.sp,
                            lineHeight = scaledLineHeight.sp,
                            color = TextPrimary,
                            textAlign = TextAlign.Start,
                            textDirection = TextDirection.Rtl,
                            lineBreak = LineBreak.Simple,
                            platformStyle = PlatformTextStyle(includeFontPadding = true),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            )
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 3. Translations & Transliteration Section
            if (hasAnyTranslationVisible) {
                Spacer(modifier = Modifier.height(10.sdp))
                HorizontalDivider(
                    color = DividerColor.copy(alpha = 0.55f),
                    thickness = 0.8.dp
                )
                Spacer(modifier = Modifier.height(10.sdp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.sdp)
                ) {
                    // Transliteration (Phonetic Latin)
                    if (showTransliteration && ayah.transliteration.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.sdp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(2.sdp))
                                    .background(DesertOchre.copy(alpha = 0.65f))
                            )
                            Spacer(modifier = Modifier.width(8.sdp))
                            Text(
                                text = ayah.transliteration,
                                fontSize = (translationFontSize * 0.92f).sp,
                                lineHeight = (translationFontSize * 1.4f).sp,
                                fontStyle = FontStyle.Italic,
                                color = MedinaSage,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Urdu Translation (Nastaleeq script) scaled dynamically with translationFontSize
                    if (showUrduTranslation && ayah.urduText.isNotBlank()) {
                        val urduSize = (translationFontSize * 1.2f).coerceIn(14f, 32f)
                        val urduLineHeight = (urduSize * 1.95f)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.sdp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(2.sdp))
                                        .background(DesertOchre.copy(alpha = 0.55f))
                                )
                                Spacer(modifier = Modifier.width(8.sdp))
                                Text(
                                    text = ayah.urduText,
                                    style = TextStyle(
                                        fontFamily = UrduTranslationFontFamily,
                                        fontSize = urduSize.sp,
                                        lineHeight = urduLineHeight.sp,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Start,
                                        textDirection = TextDirection.Rtl,
                                        platformStyle = PlatformTextStyle(includeFontPadding = true),
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.None
                                        )
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 2.sdp)
                                )
                            }
                        }
                    }

                    // English Translation (Saheeh International) scaled dynamically with translationFontSize
                    if (showTranslation && ayah.englishText.isNotBlank()) {
                        val engSize = translationFontSize.coerceIn(12f, 26f)
                        val engLineHeight = (engSize * 1.48f)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.sdp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(2.sdp))
                                    .background(MedinaSage.copy(alpha = 0.55f))
                            )
                            Spacer(modifier = Modifier.width(8.sdp))
                            Text(
                                text = ayah.englishText,
                                fontSize = engSize.sp,
                                lineHeight = engLineHeight.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 1.sdp)
                            )
                        }
                    }
                }
            }
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
            .size(30.sdp)
            .clip(CircleShape)
            .background(containerColor)
            .border(0.6.dp, DividerColor.copy(alpha = 0.6f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(16.sdp)
        )
    }
}
