package com.example.noorapp.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.WarmParchment

/**
 * Framed Arabic script container shared across Quran verses, Duas, and Daily Remembrance cards.
 * Applies dynamic per-font safe overhang padding and renders annotated Arabic text.
 */
@Composable
fun AppArabicScriptBox(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    fontSize: TextUnit? = null,
    fontKeyOverride: String = ArabicFontManager.currentFontKey.value,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    baseTextColor: Color = TextPrimary,
    backgroundColor: Color = WarmParchment.copy(alpha = 0.55f),
    borderColor: Color = DesertOchre.copy(alpha = 0.28f),
    cornerRadius: Dp = AppDimensions.cornerMediumLarge,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = Int.MAX_VALUE
) {
    val resolvedFontSize = fontSize ?: AppDimensions.textArabicDefault
    val fontConfig = remember(fontKeyOverride) {
        ArabicFontManager.getTypographyConfig(fontKeyOverride)
    }
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = AppDimensions.borderWidthHairline,
                color = borderColor,
                shape = shape
            )
            .padding(
                horizontal = AppDimensions.scaledDp(fontConfig.horizontalSafePaddingDp) + AppDimensions.arabicBoxExtraHorizontalPadding,
                vertical = AppDimensions.scaledDp(fontConfig.verticalSafePaddingDp) + AppDimensions.arabicBoxExtraVerticalPadding
            )
    ) {
        AppText.AnnotatedArabicText(
            text = text,
            inlineContent = inlineContent,
            fontSize = resolvedFontSize,
            color = baseTextColor,
            textAlign = textAlign,
            maxLines = maxLines,
            fontKeyOverride = fontKeyOverride,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
