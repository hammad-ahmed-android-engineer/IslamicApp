package com.example.noorapp.ui.tasbih.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.domain.model.TasbihState

@Composable
fun TasbihTabContent(
    tasbih: TasbihState,
    isLandscape: Boolean,
    selectedQuranFont: String = ArabicFontManager.currentFontKey.value,
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
        Triple(stringResource(R.string.tasbih_preset_subhanallah), stringResource(R.string.tasbih_arabic_subhanallah), 33),
        Triple(stringResource(R.string.tasbih_preset_alhamdulillah), stringResource(R.string.tasbih_arabic_alhamdulillah), 33),
        Triple(stringResource(R.string.tasbih_preset_allahuakbar), stringResource(R.string.tasbih_arabic_allahuakbar), 33),
        Triple(stringResource(R.string.tasbih_preset_astaghfirullah), stringResource(R.string.tasbih_arabic_astaghfirullah), 100),
        Triple(stringResource(R.string.tasbih_preset_lailaha), stringResource(R.string.tasbih_arabic_lailaha), 100)
    )

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.paddingMedium)
                .padding(bottom = AppDimensions.paddingSmall),
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                val beadSize = minOf(maxWidth, maxHeight, AppDimensions.dialogContentMaxHeightCompact)
                TasbihCounterBead(
                    count = tasbih.count,
                    target = tasbih.target,
                    arabicPhrase = tasbih.arabicPhrase,
                    scale = scale,
                    onTap = onTap,
                    selectedQuranFont = selectedQuranFont,
                    size = beadSize
                )
            }

            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = AppDimensions.paddingSmall),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                TasbihHeaderControls(
                    tasbih = tasbih,
                    onReset = onReset,
                    onSetTarget = onSetTarget
                )

                AppText.HeadingMedium(
                    text = stringResource(R.string.tasbih_presets),
                    color = MaterialTheme.colorScheme.onBackground
                )

                presetPhrases.forEach { (eng, arb, tgt) ->
                    DhikrPresetChip(
                        title = eng,
                        arabic = arb,
                        target = tgt,
                        isSelected = tasbih.currentPhrase.startsWith(eng.substringBefore(" ")),
                        selectedQuranFont = selectedQuranFont,
                        onClick = { onSelectPhrase(eng, arb, tgt) }
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.paddingMedium)
                .padding(bottom = AppDimensions.paddingMedium),
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
                selectedQuranFont = selectedQuranFont,
                size = AppDimensions.compassDialSize
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
            ) {
                AppText.HeadingMedium(
                    text = stringResource(R.string.tasbih_presets),
                    color = MaterialTheme.colorScheme.onBackground
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetPhrases) { (eng, arb, tgt) ->
                        DhikrPresetChip(
                            title = eng,
                            arabic = arb,
                            target = tgt,
                            isSelected = tasbih.currentPhrase.startsWith(eng.substringBefore(" ")),
                            selectedQuranFont = selectedQuranFont,
                            onClick = { onSelectPhrase(eng, arb, tgt) }
                        )
                    }
                }
            }
        }
    }
}
