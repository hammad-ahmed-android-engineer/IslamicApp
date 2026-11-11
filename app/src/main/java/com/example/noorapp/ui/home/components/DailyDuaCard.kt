package com.example.noorapp.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppArabicScriptBox
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppCardHeaderRow
import com.example.noorapp.core.designsystem.AppCopyShareActions
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.ArabicFontManager
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.utils.AppUtils
import com.example.noorapp.domain.model.DuaItem
import com.example.noorapp.ui.quran.util.QuranTextFormatter

@Composable
fun DailyDuaCard(
    dua: DuaItem?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AppCard(
        backgroundColor = PureAlabaster,
        modifier = modifier
    ) {
        Column {
            AppCardHeaderRow(
                title = stringResource(R.string.home_featured_daily_dua),
                subtitle = stringResource(R.string.home_featured_daily_dua_arabic),
                icon = Icons.Default.Favorite,
                iconTint = DesertOchre,
                iconBackgroundColor = DesertOchreLight,
                trailingContent = if (dua != null) {
                    {
                        AppCopyShareActions(
                            copyDescription = stringResource(R.string.home_copy_dua_desc),
                            shareDescription = stringResource(R.string.home_share_dua_desc),
                            onCopy = {
                                val text = "${dua.arabicText}\n\n\"${dua.translation}\""
                                AppUtils.copyToClipboard(
                                    context,
                                    context.getString(R.string.home_dua_clipboard_label),
                                    text,
                                    context.getString(R.string.home_dua_copied_toast)
                                )
                            },
                            onShare = {
                                val text = "${dua.arabicText}\n\n\"${dua.translation}\""
                                AppUtils.shareText(context, text, context.getString(R.string.share_dua_via))
                            }
                        )
                    }
                } else null
            )

            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

            if (dua != null) {
                val activeFontKey by ArabicFontManager.currentFontKey.collectAsState()
                val formattedDuaArabic = remember(dua.arabicText, activeFontKey) {
                    QuranTextFormatter.formatDuaArabicText(
                        rawArabicText = dua.arabicText,
                        fontKey = activeFontKey
                    )
                }
                val formattedTranslation = remember(dua.translation) {
                    QuranTextFormatter.formatDuaTranslationText(dua.translation)
                }
                AppArabicScriptBox(
                    text = formattedDuaArabic,
                    fontSize = AppDimensions.textArabicListTitle,
                    fontKeyOverride = activeFontKey,
                    borderColor = DesertOchre.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))
                AppText.BodyMedium(
                    text = formattedTranslation,
                    color = TextPrimary,
                    maxLines = 4
                )
            } else {
                AppText.BodyMedium(
                    text = stringResource(R.string.home_loading_daily_dua),
                    color = TextSecondary
                )
            }
        }
    }
}
