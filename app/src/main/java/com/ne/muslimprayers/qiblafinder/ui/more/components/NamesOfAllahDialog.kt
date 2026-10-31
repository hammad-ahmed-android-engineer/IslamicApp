package com.ne.muslimprayers.qiblafinder.ui.more.components

import androidx.compose.foundation.border
import com.ne.muslimprayers.qiblafinder.core.utils.clickableDebounced
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.more.NameOfAllah

@Composable
fun NamesOfAllahDialog(
    names: List<NameOfAllah>,
    playingNameId: Int?,
    onPlayAudio: (NameOfAllah) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.sdp)),
            color = PureAlabaster
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.sdp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = DesertOchre,
                            modifier = Modifier.size(24.sdp)
                        )
                        Spacer(modifier = Modifier.width(8.sdp))
                        AppText.HeadingLarge(
                            text = "99 Names of Allah (أسماء الله الحسنى)",
                            color = TextPrimary,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.sdp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.sdp),
                    verticalArrangement = Arrangement.spacedBy(10.sdp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(names) { item ->
                        val isPlaying = playingNameId == item.id
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.sdp))
                                .clickableDebounced { onPlayAudio(item) }
                                .border(
                                    width = 1.sdp,
                                    color = if (isPlaying) MedinaSage else DividerColor,
                                    shape = RoundedCornerShape(16.sdp)
                                ),
                            shape = RoundedCornerShape(16.sdp),
                            color = if (isPlaying) MedinaSage else PureAlabaster
                        ) {
                            Row(
                                modifier = Modifier.padding(12.sdp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    AppText.TitleMedium(
                                        text = item.arabic,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPlaying) PureAlabaster else TextPrimary,
                                        maxLines = 1
                                    )
                                    AppText.BodySmall(
                                        text = item.transliteration,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isPlaying) DesertOchre else MedinaSage,
                                        maxLines = 1
                                    )
                                    AppText.BodySmall(
                                        text = item.translation,
                                        color = if (isPlaying) MedinaSageLight else TextSecondary,
                                        maxLines = 1
                                    )
                                }
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                                    contentDescription = "Play",
                                    tint = if (isPlaying) DesertOchre else MedinaSage,
                                    modifier = Modifier.size(24.sdp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
