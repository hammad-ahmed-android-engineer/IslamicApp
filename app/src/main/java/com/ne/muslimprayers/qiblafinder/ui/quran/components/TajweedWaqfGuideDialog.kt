package com.ne.muslimprayers.qiblafinder.ui.quran.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchreLight
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.QuranFontFamily
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.core.theme.ssp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.quran.util.QuranTextFormatter

@Composable
fun TajweedWaqfGuideDialog(
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.85f else 0.94f)
                .clip(RoundedCornerShape(24.sdp))
                .border(1.sdp, DividerColor, RoundedCornerShape(24.sdp)),
            color = WarmParchment,
            shape = RoundedCornerShape(24.sdp),
            tonalElevation = 6.sdp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isLandscape) 14.sdp else 18.sdp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.sdp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.sdp)
                                .clip(CircleShape)
                                .background(MedinaSageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MedinaSage,
                                modifier = Modifier.size(20.sdp)
                            )
                        }

                        Column {
                            AppText.TitleMedium(
                                text = "Tajweed Pause Marks",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            AppText.Caption(
                                text = "علامات الوقف في القرآن الكريم",
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.sdp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.sdp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.sdp))

                AppText.BodySmall(
                    text = "Tajweed pause symbols are placed above words to guide the reciter where to stop or continue, ensuring the sacred meaning remains preserved.",
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.sdp))

                // Responsive List of Waqf Signs
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = if (isLandscape) 220.sdp else 380.sdp),
                    contentPadding = PaddingValues(vertical = 4.sdp),
                    verticalArrangement = Arrangement.spacedBy(10.sdp)
                ) {
                    items(QuranTextFormatter.WAQF_SIGNS_CATALOG) { item ->
                        WaqfSignCard(item)
                    }
                }

                Spacer(modifier = Modifier.height(12.sdp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MedinaSage),
                    shape = RoundedCornerShape(14.sdp)
                ) {
                    Text(
                        text = "Understood",
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                }
            }
        }
    }
}

@Composable
private fun WaqfSignCard(item: QuranTextFormatter.WaqfSignInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.sdp, DividerColor, RoundedCornerShape(16.sdp)),
        colors = CardDefaults.cardColors(containerColor = PureAlabaster),
        shape = RoundedCornerShape(16.sdp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.sdp)
        ) {
            // Header Row: Symbol Circle + Title & Arabic Name on left, Rule pill on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.sdp)
                ) {
                    // Symbol Badge
                    Box(
                        modifier = Modifier
                            .size(42.sdp)
                            .clip(CircleShape)
                            .background(DesertOchreLight)
                            .border(1.2.sdp, DesertOchre.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.symbol,
                            fontSize = 20.ssp,
                            fontWeight = FontWeight.Bold,
                            color = DesertOchre,
                            fontFamily = QuranFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column {
                        AppText.BodyMedium(
                            text = item.englishName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        AppText.Caption(
                            text = item.arabicName,
                            color = MedinaSage,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.sdp))

                // Rule pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.sdp))
                        .background(MedinaSageLight)
                        .padding(horizontal = 8.sdp, vertical = 4.sdp)
                ) {
                    AppText.Caption(
                        text = item.rule,
                        fontWeight = FontWeight.Bold,
                        color = MedinaSage
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.sdp))

            // Description text
            AppText.BodySmall(
                text = item.description,
                color = TextSecondary
            )
        }
    }
}
