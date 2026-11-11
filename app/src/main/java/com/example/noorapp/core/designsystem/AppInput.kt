package com.example.noorapp.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.noorapp.R
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.AppTypography
import com.example.noorapp.core.theme.DividerColor
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary

/**
 * Standardized outlined input text field styled with [AppTypography], [AppDimensions], and [MedinaSage].
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { AppText.BodySmall(text = label) },
        placeholder = placeholder?.let { { AppText.BodyMedium(text = it, color = TextSecondary) } },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        enabled = enabled,
        textStyle = AppTypography.BodyMedium.copy(color = TextPrimary),
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(AppDimensions.cornerMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MedinaSage,
            unfocusedBorderColor = DividerColor,
            focusedLabelColor = MedinaSage,
            unfocusedLabelColor = TextSecondary,
            cursorColor = MedinaSage,
            focusedContainerColor = PureAlabaster,
            unfocusedContainerColor = PureAlabaster
        )
    )
}

/**
 * Compact, unified search bar component using [AppDimensions] and [AppTypography].
 */
@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Default.Search,
    onClear: () -> Unit = { onQueryChange("") }
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimensions.buttonSmallHeight),
        shape = RoundedCornerShape(AppDimensions.cornerMedium),
        color = PureAlabaster,
        border = BorderStroke(AppDimensions.borderWidthThin, DividerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppDimensions.paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = stringResource(R.string.search),
                tint = MedinaSage,
                modifier = Modifier.size(AppDimensions.iconMedium)
            )

            Spacer(modifier = Modifier.width(AppDimensions.paddingSmall))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    AppText.BodySmall(
                        text = placeholder,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = AppTypography.BodyMedium.copy(
                        color = TextPrimary
                    ),
                    cursorBrush = SolidColor(MedinaSage)
                )
            }

            if (query.isNotEmpty()) {
                AppIconButton(
                    onClick = onClear,
                    modifier = Modifier.size(AppDimensions.iconLarge)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear_search),
                        tint = TextSecondary,
                        modifier = Modifier.size(AppDimensions.iconSmall)
                    )
                }
            }
        }
    }
}

/**
 * Backward-compatible alias delegating to [AppSearchBar].
 */
@Composable
fun IslamicSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Default.Search,
    onClear: () -> Unit = { onQueryChange("") }
) {
    AppSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        placeholder = placeholder,
        modifier = modifier,
        leadingIcon = leadingIcon,
        onClear = onClear
    )
}

