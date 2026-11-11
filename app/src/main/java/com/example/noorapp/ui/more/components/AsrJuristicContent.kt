package com.example.noorapp.ui.more.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.noorapp.core.designsystem.AppRadioOptionRow

data class AsrSchoolOption(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int
)

/**
 * Pure content body for selecting the Asr Juristic calculation school inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun AsrJuristicContent(
    schools: List<AsrSchoolOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    schools.forEachIndexed { index, option ->
        AppRadioOptionRow(
            title = stringResource(option.titleRes),
            subtitle = stringResource(option.descRes),
            selected = selectedIndex == index,
            onClick = { onSelect(index) }
        )
    }
}

