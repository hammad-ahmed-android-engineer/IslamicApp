package com.example.noorapp.ui.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.clickableDebounced
import com.example.noorapp.domain.model.AVAILABLE_RECITERS
import com.example.noorapp.domain.model.ReciterQirahOption

/**
 * Pure body composable for picking a Qari reciter inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun SelectReciterDownloadContent(
    selectedReciterId: String,
    reciters: List<ReciterQirahOption> = AVAILABLE_RECITERS,
    onSelectReciter: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
    ) {
        reciters.forEach { reciter ->
            val isSelected = selectedReciterId == reciter.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerMedium))
                    .background(
                        if (isSelected) MedinaSageLight.copy(alpha = 0.65f)
                        else WarmParchment.copy(alpha = 0.6f)
                    )
                    .border(
                        width = if (isSelected) AppDimensions.borderWidthThick else AppDimensions.borderWidthThin,
                        color = if (isSelected) MedinaSage else DividerColor,
                        shape = RoundedCornerShape(AppDimensions.cornerMedium)
                    )
                    .clickableDebounced { onSelectReciter(reciter.id) }
                    .padding(
                        horizontal = AppDimensions.paddingMedium,
                        vertical = AppDimensions.paddingSmall
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppText.BodyMedium(
                        text = reciter.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) MedinaSage else TextPrimary,
                        maxLines = 1
                    )
                    AppText.Caption(
                        text = stringResource(
                            R.string.quran_reciter_riwayah_format,
                            reciter.arabicName,
                            reciter.riwayah
                        ),
                        color = if (isSelected) DesertOchre else TextSecondary,
                        maxLines = 1
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
            }
        }
    }
}

