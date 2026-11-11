package com.example.noorapp.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCardHeaderRow
import com.example.noorapp.core.designsystem.AppCopyShareActions
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.utils.AppUtils
import com.example.noorapp.domain.model.Hadith

@Composable
fun DailyHadithCard(
    hadith: Hadith?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AppCard(
        backgroundColor = PureAlabaster,
        onClick = onClick,
        modifier = modifier
    ) {
        Column {
            AppCardHeaderRow(
                title = stringResource(R.string.home_hadith_of_the_day),
                subtitle = stringResource(R.string.home_daily_hadith_arabic),
                icon = Icons.Default.AutoStories,
                iconTint = MedinaSage,
                iconBackgroundColor = MedinaSageLight,
                trailingContent = if (hadith != null) {
                    {
                        AppCopyShareActions(
                            copyDescription = stringResource(R.string.home_copy_hadith_desc),
                            shareDescription = stringResource(R.string.home_share_hadith_desc),
                            onCopy = {
                                val text = context.getString(
                                    R.string.hadith_share_format,
                                    hadith.englishText,
                                    hadith.source,
                                    hadith.chapter
                                )
                                AppUtils.copyToClipboard(
                                    context,
                                    context.getString(R.string.home_hadith_clipboard_label),
                                    text,
                                    context.getString(R.string.home_hadith_copied_toast)
                                )
                            },
                            onShare = {
                                val text = context.getString(
                                    R.string.hadith_share_format,
                                    hadith.englishText,
                                    hadith.source,
                                    hadith.chapter
                                )
                                AppUtils.shareText(context, text, context.getString(R.string.share_hadith_via))
                            }
                        )
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            if (hadith != null) {
                AppText.BodyMedium(
                    text = "\"${hadith.englishText}\"",
                    color = TextPrimary,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText.BodySmall(
                        text = stringResource(R.string.hadith_book_title_arabic_format, hadith.source, hadith.chapter),
                        fontWeight = FontWeight.Bold,
                        color = MedinaSage,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    AppText.Caption(
                        text = stringResource(R.string.home_tap_full_view),
                        color = DesertOchre,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                AppText.BodyMedium(
                    text = stringResource(R.string.home_loading_daily_hadith),
                    color = TextSecondary
                )
            }
        }
    }
}
