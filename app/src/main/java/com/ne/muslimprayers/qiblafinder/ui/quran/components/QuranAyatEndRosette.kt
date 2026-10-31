package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.QuranFontFamily
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter
import kotlin.math.cos
import kotlin.math.sin

/**
 * Authentic Quranic Ayah End Rosette (آیت کا گول دائرہ و نشان)
 * Renders the ornate Islamic circular rosette with the verse number
 * centered precisely INSIDE the circle.
 */
@Composable
fun QuranAyatEndRosette(
    ayahNumber: Int,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    rosetteColor: Color = DesertOchre,
    textColor: Color = DesertOchre,
    fontFamily: FontFamily = QuranFontFamily,
    useIndoPakDigits: Boolean = true
) {
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
        ayahNumber < 10 -> (size.value * 0.46f).sp
        ayahNumber < 100 -> (size.value * 0.40f).sp
        else -> (size.value * 0.32f).sp
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Draw the ornate 8-point Islamic rosette and inner circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            val outerRadius = this.size.minDimension * 0.47f
            val innerRingRadius = this.size.minDimension * 0.38f
            val strokeWidth = 1.2.dp.toPx()

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
                style = Stroke(width = strokeWidth)
            )

            // 2. Draw inner circle ring
            drawCircle(
                color = rosetteColor.copy(alpha = 0.85f),
                radius = innerRingRadius,
                center = c,
                style = Stroke(width = strokeWidth * 0.85f)
            )
        }

        // Verse number precisely centered INSIDE the rosette circle
        Text(
            text = digits,
            fontSize = numberFontSize,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center
        )
    }
}
