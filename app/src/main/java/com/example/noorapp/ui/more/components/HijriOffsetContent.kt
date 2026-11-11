package com.example.noorapp.ui.more.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppRadioOptionRow

/**
 * Pure content body for adjusting the Hijri calendar offset inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun HijriOffsetContent(
    offsets: List<Int>,
    selectedOffset: Int,
    onSelect: (Int) -> Unit
) {
    offsets.forEach { offset ->
        val label = when {
            offset > 0 -> stringResource(R.string.more_hijri_offset_pos, offset)
            offset < 0 -> stringResource(R.string.more_hijri_offset_neg, offset)
            else -> stringResource(R.string.more_hijri_offset_default_option)
        }
        AppRadioOptionRow(
            title = label,
            selected = selectedOffset == offset,
            onClick = { onSelect(offset) }
        )
    }
}

