package com.ne.muslimprayers.qiblafinder.ui.ibadat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.domain.model.TasbihState
import com.ne.muslimprayers.qiblafinder.ui.components.AppText

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
                text = "Laps Completed: ${tasbih.totalLaps}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            AppText.BodySmall(
                text = "Target Goal: ${tasbih.target}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = { onSetTarget(if (tasbih.target == 33) 100 else 33) },
                shape = RoundedCornerShape(12.sdp),
                contentPadding = PaddingValues(horizontal = 10.sdp, vertical = 4.sdp)
            ) {
                Text(text = "Target: ${if (tasbih.target == 33) "100" else "33"}")
            }

            Spacer(modifier = Modifier.width(8.sdp))

            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Reset Counter",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
