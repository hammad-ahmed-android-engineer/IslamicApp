package com.example.noorapp.ui.quran.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.QuranFontFamily
import com.example.noorapp.ui.quran.util.QuranTextFormatter
import kotlin.math.cos
import kotlin.math.sin

/**
 * Authentic Quranic Ayah End Rosette (آیت کا گول دائرہ و نشان)
 * Renders the ornate Islamic circular rosette with the verse number
 * centered precisely INSIDE the circle regardless of selected calligraphic font metrics.
 *
 * Supports displaying an end-of-verse special character or Waqf mark
 * (such as ۘ, ۚ, ۖ, ۗ, ۙ, ؕ, ۩, مـ, ج) floating directly ABOVE the rosette icon.
 */
@Composable
fun QuranAyatEndRosette(
    ayahNumber: Int,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    rosetteColor: Color = DesertOchre,
    textColor: Color = DesertOchre,
    endingSpecialMark: String? = null,
    fontFamily: FontFamily = QuranFontFamily,
    useIndoPakDigits: Boolean = true
) {
    val resolvedSize = size ?: AppDimensions.cornerPill
    val digits = remember(ayahNumber, useIndoPakDigits) {
        if (useIndoPakDigits) {
            val indoPakDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
            ayahNumber.toString().map { char ->
                if (char.isDigit()) indoPakDigits[char - '0'] else char
            }.joinToString("")
        } else {
            QuranTextFormatter.run { ayahNumber.toArabicDigits() }
        }
    }

    // Dynamic font size depending on digit count so 1, 2, or 3 digits fit inside the ring
    val numberFontSize = when {
        ayahNumber < 10 -> AppDimensions.scaledSp(resolvedSize.value * 0.42f)
        ayahNumber < 100 -> AppDimensions.scaledSp(resolvedSize.value * 0.36f)
        else -> AppDimensions.scaledSp(resolvedSize.value * 0.30f)
    }

    val ringStrokeWidth = AppDimensions.borderWidthThin
    val hasSpecialMark = !endingSpecialMark.isNullOrBlank()
    val containerHeight = if (hasSpecialMark) resolvedSize * 1.72f else resolvedSize

    // Symmetric height around the Rosette center guarantees PlaceholderVerticalAlign.TextCenter
    // always aligns the ornamental Rosette circle identically with the Arabic verse baseline/x-height
    // while keeping the optional overhead pause/Sajdah symbol cleanly centered in the top headroom.
    Box(
        modifier = modifier.height(containerHeight),
        contentAlignment = Alignment.Center
    ) {
        if (hasSpecialMark) {
            AppText.SpecialMarkBadge(
                text = endingSpecialMark!!,
                fontSize = AppDimensions.scaledSp(resolvedSize.value * 0.36f),
                color = rosetteColor,
                fontFamily = fontFamily,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        Box(
            modifier = Modifier
                .size(resolvedSize)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // Draw the ornate 8-point Islamic rosette and inner circle
            Canvas(modifier = Modifier.fillMaxSize()) {
                val c = Offset(this.size.width / 2f, this.size.height / 2f)
                val outerRadius = this.size.minDimension * 0.47f
                val innerRingRadius = this.size.minDimension * 0.38f
                val strokePx = ringStrokeWidth.toPx()

                // 1. Draw outer 8-crested ornamental rosette petal border
                val path = Path()
                val points = 16
                for (i in 0 until points) {
                    val angle = (i * 360f / points) * (Math.PI / 180f)
                    val r = if (i % 2 == 0) outerRadius * 1.04f else outerRadius * 0.88f
                    val px = c.x + (r * cos(angle)).toFloat()
                    val py = c.y + (r * sin(angle)).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                drawPath(
                    path = path,
                    color = rosetteColor,
                    style = Stroke(width = strokePx)
                )

                // 2. Draw inner circle ring
                drawCircle(
                    color = rosetteColor.copy(alpha = 0.85f),
                    radius = innerRingRadius,
                    center = c,
                    style = Stroke(width = strokePx * 0.85f)
                )
            }

            // Verse number precisely centered INSIDE the rosette circle using AppText custom view
            AppText.RosetteNumeral(
                text = digits,
                fontSize = numberFontSize,
                color = textColor
            )
        }
    }
}
