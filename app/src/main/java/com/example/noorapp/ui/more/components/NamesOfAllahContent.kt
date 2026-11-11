package com.example.noorapp.ui.more.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppSelectableCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.ui.more.NameOfAllah

/**
 * Pure grid content for 99 Names of Allah displayed inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun ColumnScope.NamesOfAllahContent(
    names: List<NameOfAllah>,
    playingNameId: Int?,
    isLandscape: Boolean = false,
    onPlayAudio: (NameOfAllah) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isLandscape) 3 else 2),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular),
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        items(names) { item ->
            val isPlaying = playingNameId == item.id
            AppSelectableCard(
                isSelected = isPlaying,
                onClick = { onPlayAudio(item) },
                selectedBackgroundColor = MedinaSage,
                unselectedBackgroundColor = PureAlabaster,
                selectedBorderColor = MedinaSage,
                unselectedBorderColor = DividerColor,
                borderWidth = AppDimensions.borderWidthThin,
                cornerRadius = AppDimensions.cornerLarge,
                contentPadding = AppDimensions.paddingMediumSmall,
                elevation = AppDimensions.elevationNone
            ) {
                Row(
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
                            text = stringResource(item.translationRes),
                            color = if (isPlaying) MedinaSageLight else TextSecondary,
                            maxLines = 1
                        )
                    }
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                        contentDescription = stringResource(R.string.names_allah_play_desc),
                        tint = if (isPlaying) DesertOchre else MedinaSage,
                        modifier = Modifier.size(AppDimensions.iconLarge)
                    )
                }
            }
        }
    }
}

