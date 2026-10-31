package com.ne.muslimprayers.qiblafinder.ui.ibadat.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.TasbihState
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun TasbihTabContent(
    tasbih: TasbihState,
    isLandscape: Boolean,
    onTap: () -> Unit,
    onReset: () -> Unit,
    onSelectPhrase: (english: String, arabic: String, target: Int) -> Unit,
    onSetTarget: (Int) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1.0f,
        animationSpec = spring(stiffness = 300f),
        label = "tasbih_tap_anim"
    )

    val presetPhrases = listOf(
        Triple("SubhanAllah (سبحان الله)", "سُبْحَانَ اللَّهِ", 33),
        Triple("Alhamdulillah (الحمد لله)", "الْحَمْدُ لِلَّهِ", 33),
        Triple("Allahu Akbar (الله أكبر)", "اللَّهُ أَكْبَرُ", 33),
        Triple("Astaghfirullah (أستغفر الله)", "أَسْتَغْفِرُ اللَّهَ", 100),
        Triple("La ilaha illallah (لا إله إلا الله)", "لَا إِلَهَ إِلَّا اللَّهُ", 100)
    )

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.sdp)
                .padding(bottom = 8.sdp),
            horizontalArrangement = Arrangement.spacedBy(16.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TasbihCounterBead(
                    count = tasbih.count,
                    target = tasbih.target,
                    arabicPhrase = tasbih.arabicPhrase,
                    scale = scale,
                    onTap = onTap,
                    size = 200
                )
            }

            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .padding(vertical = 8.sdp),
                verticalArrangement = Arrangement.spacedBy(12.sdp)
            ) {
                TasbihHeaderControls(
                    tasbih = tasbih,
                    onReset = onReset,
                    onSetTarget = onSetTarget
                )

                AppText.HeadingMedium(
                    text = "Select Dhikr Phrase",
                    color = MaterialTheme.colorScheme.onBackground
                )

                presetPhrases.forEach { (eng, arb, tgt) ->
                    DhikrPresetChip(
                        title = eng,
                        arabic = arb,
                        target = tgt,
                        isSelected = tasbih.currentPhrase.startsWith(eng.substringBefore(" ")),
                        onClick = { onSelectPhrase(eng, arb, tgt) }
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.sdp)
                .padding(bottom = 16.sdp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TasbihHeaderControls(
                tasbih = tasbih,
                onReset = onReset,
                onSetTarget = onSetTarget
            )

            TasbihCounterBead(
                count = tasbih.count,
                target = tasbih.target,
                arabicPhrase = tasbih.arabicPhrase,
                scale = scale,
                onTap = onTap,
                size = 250
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.sdp)
            ) {
                AppText.HeadingMedium(
                    text = "Select Dhikr Phrase",
                    color = MaterialTheme.colorScheme.onBackground
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.sdp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetPhrases) { (eng, arb, tgt) ->
                        DhikrPresetChip(
                            title = eng,
                            arabic = arb,
                            target = tgt,
                            isSelected = tasbih.currentPhrase.startsWith(eng.substringBefore(" ")),
                            onClick = { onSelectPhrase(eng, arb, tgt) }
                        )
                    }
                }
            }
        }
    }
}
