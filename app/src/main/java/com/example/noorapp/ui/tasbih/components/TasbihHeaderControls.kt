package com.example.noorapp.ui.tasbih.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppOutlinedButton
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.domain.model.TasbihState

@Composable
fun TasbihHeaderControls(
    tasbih: TasbihState,
    onReset: () -> Unit,
    onSetTarget: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            AppText.TitleMedium(
                text = stringResource(R.string.tasbih_laps_completed_format, tasbih.totalLaps),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            AppText.BodySmall(
                text = stringResource(R.string.tasbih_target_goal_format, tasbih.target),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            AppOutlinedButton(
                onClick = { onSetTarget(if (tasbih.target == 33) 100 else 33) },
                contentPadding = PaddingValues(
                    horizontal = AppDimensions.paddingRegular,
                    vertical = AppDimensions.paddingTiny
                )
            ) {
                AppText.LabelMedium(
                    text = stringResource(
                        R.string.tasbih_target,
                        if (tasbih.target == 33) 100 else 33
                    )
                )
            }

            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

            AppIconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = stringResource(R.string.tasbih_reset_desc),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
