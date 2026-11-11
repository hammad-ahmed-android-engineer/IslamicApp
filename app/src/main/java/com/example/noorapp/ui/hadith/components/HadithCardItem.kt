package com.example.noorapp.ui.hadith.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppButton
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.SuccessGreen
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.domain.model.Hadith

@Composable
fun HadithCardItem(
    hadith: Hadith,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    AppCard(
        backgroundColor = PureAlabaster,
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.buttonSmallHeight)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.hadith_grade_sahih),
                            tint = SuccessGreen,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }

                    Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

                    Column(modifier = Modifier.weight(1f)) {
                        AppText.TitleMedium(
                            text = hadith.narrator.ifBlank {
                                stringResource(R.string.hadith_number_fallback_format, hadith.id)
                            },
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        AppText.BodySmall(
                            text = stringResource(
                                R.string.hadith_book_chapter_format,
                                hadith.source,
                                hadith.chapter
                            ),
                            color = TextSecondary,
                            maxLines = 1
                        )
                    }
                }

                Row {
                    AppIconButton(onClick = onCopy) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy),
                            tint = TextSecondary,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                    AppIconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
                            tint = TextSecondary,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }
            }

            if (hadith.arabicText.isNotBlank()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                AppText.ArabicText(
                    text = hadith.arabicText,
                    color = TextPrimary,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            AppText.BodyMedium(
                text = "\"${hadith.englishText}\"",
                color = TextSecondary,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                AppButton(
                    onClick = onClick,
                    contentPadding = PaddingValues(
                        horizontal = AppDimensions.paddingMedium,
                        vertical = AppDimensions.paddingTiny
                    )
                ) {
                    AppText.LabelMedium(
                        text = stringResource(R.string.hadith_read_full),
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                }
            }
        }
    }
}
