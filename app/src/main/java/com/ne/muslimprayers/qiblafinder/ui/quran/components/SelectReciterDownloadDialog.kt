package com.ne.muslimprayers.qiblafinder.ui.quran.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.quran.AVAILABLE_RECITERS
import com.ne.muslimprayers.qiblafinder.ui.quran.ReciterQirahOption

@Composable
fun SelectReciterDownloadDialog(
    targetTitle: String,
    initialReciterId: String,
    reciters: List<ReciterQirahOption> = AVAILABLE_RECITERS,
    onDismiss: () -> Unit,
    onConfirmDownload: (String) -> Unit
) {
    var chosenReciterId by remember(initialReciterId) {
        mutableStateOf(
            if (reciters.any { it.id == initialReciterId }) initialReciterId
            else reciters.first().id
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppDimensions.cornerExtraLarge)),
            color = PureAlabaster,
            tonalElevation = AppDimensions.elevationMedium
        ) {
            Column(modifier = Modifier.padding(AppDimensions.paddingMedium)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.avatarSize)
                            .clip(CircleShape)
                            .background(MedinaSageLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = MedinaSage,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                    Column(modifier = Modifier.weight(1f)) {
                        AppText.TitleMedium(
                            text = stringResource(R.string.quran_select_qari_download_title),
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        AppText.Caption(
                            text = stringResource(R.string.quran_select_qari_download_subtitle, targetTitle),
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                Column(
                    modifier = Modifier
                        .heightIn(max = AppDimensions.dialogContentMaxHeightExpanded)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    reciters.forEach { reciter ->
                        val isSelected = chosenReciterId == reciter.id
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
                                .clickableDebounced { chosenReciterId = reciter.id }
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
                                    text = "${reciter.arabicName} • ${reciter.riwayah}",
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

                Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(AppDimensions.cornerMedium)
                    ) {
                        AppText.ButtonText(
                            text = stringResource(R.string.cancel),
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { onConfirmDownload(chosenReciterId) },
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(AppDimensions.cornerMedium),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MedinaSage,
                            contentColor = PureAlabaster
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                        AppText.ButtonText(
                            text = stringResource(R.string.quran_download_start),
                            fontWeight = FontWeight.Bold,
                            color = PureAlabaster
                        )
                    }
                }
            }
        }
    }
}
