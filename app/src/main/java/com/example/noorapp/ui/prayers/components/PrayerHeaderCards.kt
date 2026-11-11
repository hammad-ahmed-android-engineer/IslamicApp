package com.example.noorapp.ui.prayers.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.utils.clickableDebounced

@Composable
fun HijriHeaderCard(
    hijriDate: String,
    gregorianDate: String,
    hijriOffsetLabel: String,
    onClick: () -> Unit
) {
    AppCard(
        backgroundColor = MedinaSage,
        borderColor = DesertOchre.copy(alpha = 0.4f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppText.TitleMedium(
                    text = hijriDate,
                    fontWeight = FontWeight.Bold,
                    color = PureAlabaster,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                AppText.BodyMedium(
                    text = stringResource(R.string.prayers_hijri_adj_subtitle_format, gregorianDate, hijriOffsetLabel),
                    color = MedinaSageLight,
                    maxLines = 1
                )
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.prayers_adjust_hijri_desc),
                tint = PureAlabaster,
                modifier = Modifier.size(AppDimensions.iconSmall)
            )
        }
    }
}

@Composable
fun CalculationMethodSelectorCard(
    methodName: String,
    asrName: String,
    isAutoDetected: Boolean,
    onChangeMethod: () -> Unit,
    onChangeAsr: () -> Unit
) {
    AppCard(
        backgroundColor = PureAlabaster
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableDebounced { onChangeMethod() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppText.Caption(
                                text = stringResource(R.string.prayers_calculation_method),
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                            AppStatusPill(
                                text = if (isAutoDetected) {
                                    stringResource(R.string.prayers_auto_detected_short)
                                } else {
                                    stringResource(R.string.prayers_custom_short)
                                },
                                textColor = if (isAutoDetected) MedinaSage else DesertOchre,
                                backgroundColor = if (isAutoDetected) {
                                    MedinaSage.copy(alpha = 0.12f)
                                } else {
                                    DesertOchre.copy(alpha = 0.18f)
                                },
                                cornerRadius = AppDimensions.cornerMicro
                            )
                        }
                        AppText.BodyMedium(
                            text = methodName,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.prayers_change_method),
                    tint = MedinaSage,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerSmall))
                    .border(
                        AppDimensions.borderWidthThin,
                        MedinaSage.copy(alpha = 0.2f),
                        RoundedCornerShape(AppDimensions.cornerSmall)
                    )
                    .clickableDebounced { onChangeAsr() }
                    .padding(
                        horizontal = AppDimensions.paddingSmall,
                        vertical = AppDimensions.paddingTiny
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = null,
                        tint = DesertOchre,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                    AppText.BodySmall(
                        text = stringResource(R.string.prayers_asr_juristic_label_format, asrName),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
                AppText.Caption(
                    text = stringResource(R.string.prayers_change_method),
                    color = MedinaSage,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

