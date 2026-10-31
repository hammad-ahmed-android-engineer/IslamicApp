package com.ne.muslimprayers.qiblafinder.ui.alarm.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.sdp
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard

@Composable
fun AdhanAudioInfoCard() {
    IslamicCard(
        backgroundColor = PureAlabaster
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MedinaSage,
                    modifier = Modifier.size(20.sdp)
                )
                Spacer(modifier = Modifier.width(8.sdp))
                AppText.TitleMedium(
                    text = "Adhan Audio Settings",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.sdp))
            AppText.BodySmall(
                text = "Fajr prayer uses Adhan Fajr with 'As-Salatu Khairum Minan-Naum'. Other daily prayers play Adhan Makkah Al-Mukarramah.",
                color = TextSecondary
            )
        }
    }
}
