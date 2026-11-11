package com.example.noorapp.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.noorapp.R
import com.example.noorapp.core.theme.AlertRed
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.TextSecondary

/**
 * Standardized full-screen loading indicator state.
 */
@Composable
fun AppLoadingState(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.loading_prayer_timings)
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppCircularProgress(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = AppDimensions.borderWidthThick,
                modifier = Modifier.size(AppDimensions.avatarSize)
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
            AppText.BodyMedium(
                text = message,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.loading_prayer_timings)
) {
    AppLoadingState(modifier = modifier, message = message)
}

/**
 * Standardized full-screen error and retry state.
 */
@Composable
fun AppErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(AppDimensions.paddingLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = AlertRed,
                modifier = Modifier.size(AppDimensions.iconHuge)
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
            AppText.BodyMedium(
                text = message,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
            AppButton(
                onClick = onRetry,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
                Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))
                AppText.ButtonText(
                    text = stringResource(R.string.retry),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ErrorState(modifier: Modifier = Modifier, message: String, onRetry: () -> Unit) {
    AppErrorState(modifier = modifier, message = message, onRetry = onRetry)
}

/**
 * Standardized empty state display for lists, search results, favorites, or bookmarks.
 */
@Composable
fun AppEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(AppDimensions.paddingLarge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MedinaSage.copy(alpha = 0.65f),
                modifier = Modifier.size(AppDimensions.iconHuge)
            )
            Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
            AppText.TitleMedium(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                AppText.BodySmall(
                    text = subtitle,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

