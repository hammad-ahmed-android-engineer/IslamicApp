package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.IndoPakQuranFontFamily
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PdmsSaleemFontFamily
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.QuranFontFamily
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.UrduTranslationFontFamily
import com.ne.muslimprayers.qiblafinder.core.theme.UthmaniQuranFontFamily
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter

@Composable
fun AyahCardItem(
    ayah: Ayah,
    surahNumber: Int = 1,
    arabicFontSize: Float,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    showUrduTranslation: Boolean = true,
    selectedQuranFont: String = "indopak", // "indopak", "uthmani", "pdms"
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
    val isIndoPakEffective = useIndoPakScript || selectedQuranFont == "indopak" || selectedQuranFont == "pdms"
    val targetArabicText = if (isIndoPakEffective && ayah.indopakText.isNotBlank()) {
        ayah.indopakText
    } else {
        ayah.arabicText
    }

    val selectedFontFamily = remember(selectedQuranFont) {
        com.ne.muslimprayers.qiblafinder.core.theme.ArabicFontManager.getFontFamily(selectedQuranFont)
    }

    // Formatted annotated string with Tajweed Waqf marks and inline Ayah End placeholder
    val formattedArabicText = remember(targetArabicText, ayah.numberInSurah, surahNumber, isColoredQuran) {
        QuranTextFormatter.formatAyahWithInlineEndSign(
            rawArabicText = targetArabicText,
            ayahNumberInSurah = ayah.numberInSurah,
            surahNumber = surahNumber,
            isColored = isColoredQuran,
            waqfHighlightColor = DesertOchre
        )
    }

    val inlineContent = remember(ayah.numberInSurah, arabicFontSize, selectedFontFamily, isIndoPakEffective, isColoredQuran) {
        mapOf(
            QuranTextFormatter.INLINE_AYAH_END_ID to InlineTextContent(
                Placeholder(
                    width = (arabicFontSize * 1.48f).sp,
                    height = (arabicFontSize * 1.48f).sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                QuranAyatEndRosette(
                    ayahNumber = ayah.numberInSurah,
                    size = (arabicFontSize * 1.48f).dp,
                    rosetteColor = if (isColoredQuran) DesertOchre else TextPrimary,
                    textColor = if (isColoredQuran) DesertOchre else TextPrimary,
                    fontFamily = selectedFontFamily,
                    useIndoPakDigits = isIndoPakEffective
                )
            }
        )
    }

    IslamicCard(
        backgroundColor = if (isPlaying) MedinaSageLight else PureAlabaster,
        borderColor = if (isPlaying) MedinaSage else DividerColor,
        modifier = modifier
    ) {
        Column {
            // Verse Header: Index Badge & Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image 4 Reference: Angular Bracket Verse Identifier ‹ Surah.Ayah ›
                Surface(
                    color = MedinaSageLight.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(8.sdp),
                    border = BorderStroke(1.sdp, MedinaSage.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.sdp, vertical = 4.sdp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.sdp)
                    ) {
                        Text(
                            text = "‹",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "$surahNumber.${ayah.numberInSurah}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedinaSage
                        )
                        Text(
                            text = "›",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }

                // Action Icons (Tafseer, Play, Bookmark, Copy, Share)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenTafseer) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Tafseer & Explanation",
                            tint = MedinaSage,
                            modifier = Modifier.size(20.sdp)
                        )
                    }
                    IconButton(onClick = onPlay) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Play Audio",
                            tint = MedinaSage,
                            modifier = Modifier.size(24.sdp)
                        )
                    }
                    IconButton(onClick = onBookmark) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) DesertOchre else TextSecondary
                        )
                    }
                    IconButton(onClick = onCopy) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.sdp)
                        )
                    }
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.sdp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.sdp))

            // Sacred Quranic Arabic Text with the chosen Authentic Quran Font, Waqf Marks & Rosette with Number INSIDE Circle
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = formattedArabicText,
                    inlineContent = inlineContent,
                    fontFamily = selectedFontFamily,
                    fontSize = arabicFontSize.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Start,
                    lineHeight = (arabicFontSize * 1.95f).sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Urdu Translation & Tafseer (Tafheem-ul-Quran) rendered with authentic Nastaleeq Font
            if (showUrduTranslation && ayah.urduText.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.sdp))
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = ayah.urduText,
                        fontFamily = UrduTranslationFontFamily,
                        fontSize = 17.sp,
                        lineHeight = 36.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.sdp)
                    )
                }
            }

            // English Translation (Saheeh International)
            if (showTranslation && ayah.englishText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.sdp))
                AppText.BodyMedium(
                    text = ayah.englishText,
                    color = TextSecondary
                )
            }
        }
    }
}
