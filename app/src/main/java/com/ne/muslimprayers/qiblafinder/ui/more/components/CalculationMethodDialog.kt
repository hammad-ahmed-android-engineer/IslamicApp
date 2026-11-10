package com.ne.muslimprayers.qiblafinder.ui.more.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun CalculationMethodDialog(
    methods: List<String>,
    selectedIndex: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
    isAutoDetected: Boolean = false,
    onAutoDetect: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppDimensions.cornerExtraLarge)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(AppDimensions.dialogPadding)) {
                AppText.TitleMedium(
                    text = stringResource(R.string.prayers_select_calculation_method),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                if (onAutoDetect != null) {
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                            .background(
                                if (isAutoDetected) MedinaSage.copy(alpha = 0.12f)
                                else DesertOchre.copy(alpha = 0.12f)
                            )
                            .border(
                                AppDimensions.borderWidthThin,
                                if (isAutoDetected) MedinaSage else DesertOchre.copy(alpha = 0.5f),
                                RoundedCornerShape(AppDimensions.cornerMedium)
                            )
                            .clickableDebounced { onAutoDetect() }
                            .padding(
                                horizontal = AppDimensions.paddingMedium,
                                vertical = AppDimensions.paddingSmall
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconMedium)
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                            Column {
                                AppText.BodyMedium(
                                    text = stringResource(R.string.prayers_reset_auto_detect),
                                    fontWeight = FontWeight.Bold,
                                    color = MedinaSage
                                )
                                AppText.Caption(
                                    text = if (isAutoDetected)
                                        stringResource(R.string.prayers_auto_detected_badge)
                                    else
                                        "Tap to auto-select based on your region",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (isAutoDetected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MedinaSage
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                Column(
                    modifier = Modifier
                        .heightIn(max = AppDimensions.dialogContentMaxHeightExpanded)
                        .verticalScroll(rememberScrollState())
                ) {
                    methods.forEachIndexed { index, name ->
                        val isSelected = selectedIndex == index
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .clickableDebounced { onSelect(index) }
                                .padding(
                                    vertical = AppDimensions.paddingSmall,
                                    horizontal = AppDimensions.paddingSmall
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppText.BodyMedium(
                                text = name,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f),
                                maxLines = 2
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
