package com.example.noorapp.ui.compass.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppIconButton
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.domain.model.QiblaState
import java.util.Locale

@Composable
fun LocationHeaderCard(
    state: QiblaState,
    onRefresh: () -> Unit
) {
    AppCard(
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(AppDimensions.iconLarge)
                )
                Spacer(modifier = Modifier.width(AppDimensions.paddingRegular))
                Column(modifier = Modifier.weight(1f)) {
                    AppText.TitleMedium(
                        text = when {
                            state.cityName.isNotBlank() -> state.cityName
                            !state.hasLocationPermission -> stringResource(R.string.tap_refresh_location_permission)
                            else -> stringResource(R.string.calculating_location)
                        },
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    AppText.BodySmall(
                        text = String.format(
                            Locale.US,
                            stringResource(R.string.lat_lng_format),
                            state.userLatitude,
                            state.userLongitude
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AppIconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.compass_recalibrate_desc),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
