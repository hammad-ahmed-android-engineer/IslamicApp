package com.example.noorapp.ui.more.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppStatusPill
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.HeroBannerGradient
import com.example.noorapp.core.theme.MedinaSageDark
import com.example.noorapp.core.theme.PureAlabaster

@Composable
fun BrandingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(AppDimensions.elevationMedium, RoundedCornerShape(AppDimensions.cornerExtraLarge))
            .clip(RoundedCornerShape(AppDimensions.cornerExtraLarge))
            .background(HeroBannerGradient)
            .border(
                width = AppDimensions.borderWidthThin,
                color = DesertOchre.copy(alpha = 0.45f),
                shape = RoundedCornerShape(AppDimensions.cornerExtraLarge)
            )
            .padding(AppDimensions.paddingMedium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.noor_splash_icon),
                contentDescription = stringResource(R.string.app_icon_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(AppDimensions.logoAvatarSize)
                    .clip(CircleShape)
                    .border(AppDimensions.borderWidthMedium, DesertOchre, CircleShape)
            )

            Spacer(modifier = Modifier.width(AppDimensions.paddingMediumSmall))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingTiny)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    AppText.TitleMedium(
                        text = stringResource(R.string.more_branding_card_title),
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    AppStatusPill(
                        text = stringResource(R.string.app_version_badge),
                        textColor = MedinaSageDark,
                        backgroundColor = DesertOchreLight
                    )
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingZero))

                AppText.BodySmall(
                    text = stringResource(R.string.more_branding_subtitle),
                    color = PureAlabaster.copy(alpha = 0.85f),
                    maxLines = 2
                )
            }
        }
    }
}
