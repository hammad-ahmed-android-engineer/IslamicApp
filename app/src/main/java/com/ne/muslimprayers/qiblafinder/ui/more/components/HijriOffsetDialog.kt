package com.ne.muslimprayers.qiblafinder.ui.more.components

import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

@Composable
fun HijriOffsetDialog(
    offsets: List<Int>,
    selectedOffset: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppDimensions.cornerExtraLarge)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(AppDimensions.paddingMediumLarge)) {
                AppText.TitleMedium(
                    text = "Hijri Calendar Adjustment",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
                offsets.forEach { offset ->
                    val label = when {
                        offset > 0 -> "+$offset Days"
                        offset < 0 -> "$offset Days"
                        else -> "0 (Default - Calculated)"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AppDimensions.cornerMediumSmall))
                            .clickableDebounced { onSelect(offset) }
                            .padding(vertical = AppDimensions.paddingRegular, horizontal = AppDimensions.paddingSmall),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText.BodyMedium(
                            text = label,
                            color = if (selectedOffset == offset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (selectedOffset == offset) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                        if (selectedOffset == offset) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
