package com.example.noorapp.ui.tasbih.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.ui.quran.util.QuranTextFormatter

@Composable
fun TasbihCounterBead(
    count: Int,
    target: Int,
    arabicPhrase: String,
    scale: Float,
    onTap: () -> Unit,
    selectedQuranFont: String = ArabicFontManager.currentFontKey.value,
    size: Dp = AppDimensions.compassDialSize
) {
    val defaultPhrase = stringResource(R.string.tasbih_default_phrase_arabic)
    val resolvedPhrase = arabicPhrase.ifBlank { defaultPhrase }
    val fontConfig = remember(selectedQuranFont) {
        ArabicFontManager.getTypographyConfig(selectedQuranFont)
    }
    val formattedArabic = remember(resolvedPhrase, selectedQuranFont) {
        QuranTextFormatter.formatArabicPhraseMultiColor(
            rawArabicText = resolvedPhrase,
            isColored = false,
            fontKey = selectedQuranFont
        )
    }
    val sweepAngle = if (target > 0) (count.toFloat() / target.toFloat()) * 360f else 0f
    val strokeWidthPx = with(LocalDensity.current) { AppDimensions.borderWidthBold.toPx() }

    // 3D Pearl Sphere Gradients
    val pearlGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF7F0E6),
            Color(0xFFEADBCE),
            Color(0xFFD6C3B2)
        )
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .shadow(AppDimensions.paddingMediumLarge, CircleShape, ambientColor = Color(0x33C88A32), spotColor = Color(0x40C88A32))
            .clip(CircleShape)
            .background(pearlGradient)
            .border(AppDimensions.borderWidthBold, DesertOchre, CircleShape)
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = DesertOchre,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(AppDimensions.paddingMedium)
        ) {
            AppText.AnnotatedArabicText(
                text = formattedArabic,
                color = TextPrimary,
                fontSize = AppDimensions.textHeadingMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontKeyOverride = selectedQuranFont,
                modifier = Modifier.padding(horizontal = AppDimensions.scaledDp(fontConfig.horizontalSafePaddingDp))
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            AppText.DisplayLarge(
                text = "$count",
                fontSizeOverride = AppDimensions.textHeroCounter,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            AppText.TitleMedium(
                text = "/ $target",
                color = DesertOchre,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

            AppText.Caption(
                text = stringResource(R.string.tasbih_tap_to_count),
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
