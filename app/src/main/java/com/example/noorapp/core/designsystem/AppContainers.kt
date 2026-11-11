package com.example.noorapp.core.designsystem

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.noorapp.R
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.core.theme.TextPrimary
import com.example.noorapp.core.theme.TextSecondary
import com.example.noorapp.core.theme.WarmParchment

/**
 * Standardized screen TopAppBar component.
 */
@Composable
fun AppTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    Surface(
        color = WarmParchment,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingTiny),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                AppIconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(AppDimensions.buttonSmallHeight)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = TextPrimary,
                        modifier = Modifier.size(AppDimensions.iconMedium)
                    )
                }
                Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
            }

            AppText.TitleMedium(
                text = title,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            actions()
        }
    }
}

/**
 * Backward-compatible alias delegating to [AppTopAppBar].
 */
@Composable
fun IslamicTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    AppTopAppBar(
        title = title,
        modifier = modifier,
        onBackClick = onBackClick,
        actions = actions
    )
}

/**
 * Shared modal header used by both [AppDialog] and [AppBottomSheet].
 */
@Composable
private fun AppModalHeader(
    title: String?,
    subtitle: String?,
    titleBadge: String?,
    titleColor: Color,
    titleMaxLines: Int,
    headerIcon: ImageVector?,
    headerIconTint: Color,
    headerIconBgColor: Color?,
    showCloseButton: Boolean,
    useCompactTitle: Boolean,
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
            modifier = Modifier.weight(1f)
        ) {
            if (headerIcon != null) {
                if (headerIconBgColor != null) {
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.buttonSmallHeight)
                            .clip(CircleShape)
                            .background(headerIconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = headerIcon,
                            contentDescription = null,
                            tint = headerIconTint,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                } else {
                    Icon(
                        imageVector = headerIcon,
                        contentDescription = null,
                        tint = headerIconTint,
                        modifier = Modifier.size(AppDimensions.iconLarge)
                    )
                }
            }
            if (title != null || subtitle != null) {
                Column(modifier = Modifier.weight(1f)) {
                    if (title != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                        ) {
                            if (useCompactTitle) {
                                AppText.TitleMedium(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor,
                                    maxLines = titleMaxLines,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            } else {
                                AppText.HeadingMedium(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor,
                                    maxLines = titleMaxLines,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                            if (titleBadge != null) {
                                AppCard(
                                    backgroundColor = MedinaSageLight,
                                    borderColor = MedinaSageLight,
                                    borderWidth = AppDimensions.paddingZero,
                                    cornerRadius = AppDimensions.cornerExtraSmall,
                                    contentPadding = AppDimensions.paddingZero,
                                    elevation = AppDimensions.elevationNone
                                ) {
                                    AppText.Caption(
                                        text = titleBadge,
                                        fontWeight = FontWeight.Bold,
                                        color = MedinaSage,
                                        modifier = Modifier.padding(
                                            horizontal = AppDimensions.paddingExtraSmall,
                                            vertical = AppDimensions.paddingMicro
                                        )
                                    )
                                }
                            }
                        }
                    }
                    if (subtitle != null) {
                        AppText.Caption(
                            text = subtitle,
                            color = TextSecondary,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        if (showCloseButton) {
            AppIconButton(
                onClick = onDismissRequest,
                modifier = Modifier.size(AppDimensions.iconButtonCompact)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = TextSecondary,
                    modifier = Modifier.size(AppDimensions.iconMedium)
                )
            }
        }
    }
}

/**
 * Shared modal action footer used by both [AppDialog] and [AppBottomSheet].
 */
@Composable
private fun AppModalFooter(
    confirmButtonText: String?,
    onConfirmClick: (() -> Unit)?,
    confirmButtonEnabled: Boolean,
    confirmButtonIcon: ImageVector?,
    confirmButtonContainerColor: Color,
    confirmButtonContentColor: Color,
    confirmButtonWeight: Float,
    dismissButtonText: String?,
    onDismissClick: (() -> Unit)?,
    dismissButtonEnabled: Boolean,
    dismissButtonWeight: Float,
    footerContent: (@Composable RowScope.() -> Unit)?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (footerContent != null) {
            footerContent()
        } else {
            if (dismissButtonText != null && onDismissClick != null) {
                AppOutlinedButton(
                    onClick = onDismissClick,
                    enabled = dismissButtonEnabled,
                    modifier = Modifier.weight(dismissButtonWeight)
                ) {
                    AppText.ButtonText(
                        text = dismissButtonText,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
            if (confirmButtonText != null && onConfirmClick != null) {
                AppButton(
                    onClick = onConfirmClick,
                    enabled = confirmButtonEnabled,
                    modifier = Modifier.weight(confirmButtonWeight),
                    containerColor = confirmButtonContainerColor,
                    contentColor = confirmButtonContentColor
                ) {
                    if (confirmButtonIcon != null) {
                        Icon(
                            imageVector = confirmButtonIcon,
                            contentDescription = null,
                            modifier = Modifier.size(AppDimensions.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(AppDimensions.paddingTiny))
                    }
                    AppText.ButtonText(
                        text = confirmButtonText,
                        fontWeight = FontWeight.Bold,
                        color = confirmButtonContentColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Common application modal dialog container with full header, optional scrollable body, and footer action bar.
 * Screens invoke [AppDialog] directly and supply pure inner content composables instead of wrapping [AppDialog]
 * inside feature-specific dialog classes.
 */
@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    titleBadge: String? = null,
    titleColor: Color = TextPrimary,
    titleMaxLines: Int = 1,
    headerIcon: ImageVector? = null,
    headerIconTint: Color = MedinaSage,
    headerIconBgColor: Color? = MedinaSageLight,
    showCloseButton: Boolean = false,
    headerContent: (@Composable () -> Unit)? = null,
    showDividerAfterHeader: Boolean = false,
    description: String? = null,
    confirmButtonText: String? = null,
    onConfirmClick: (() -> Unit)? = null,
    confirmButtonEnabled: Boolean = true,
    confirmButtonIcon: ImageVector? = null,
    confirmButtonContainerColor: Color = MedinaSage,
    confirmButtonContentColor: Color = PureAlabaster,
    confirmButtonWeight: Float = 1f,
    dismissButtonText: String? = null,
    onDismissClick: (() -> Unit)? = onDismissRequest,
    dismissButtonEnabled: Boolean = true,
    dismissButtonWeight: Float = 1f,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    usePlatformDefaultWidth: Boolean = true,
    cornerRadius: Dp = AppDimensions.cornerDialog,
    backgroundColor: Color = PureAlabaster,
    borderColor: Color? = null,
    tonalElevation: Dp = AppDimensions.elevationMedium,
    contentPadding: Dp = AppDimensions.dialogPadding,
    maxContentHeight: Dp? = null,
    scrollable: Boolean = false,
    footerContent: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val screenMaxDialogHeight = AppDimensions.scaledDp(configuration.screenHeightDp * if (isLandscape) 0.90f else 0.88f)

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
            usePlatformDefaultWidth = usePlatformDefaultWidth
        )
    ) {
        val shape = RoundedCornerShape(cornerRadius)
        Surface(
            modifier = modifier
                .heightIn(max = screenMaxDialogHeight)
                .imePadding()
                .clip(shape),
            shape = shape,
            color = backgroundColor,
            border = borderColor?.let { BorderStroke(AppDimensions.borderWidthThin, it) },
            tonalElevation = tonalElevation
        ) {
            Column(modifier = Modifier.padding(contentPadding)) {
                val hasHeader = headerContent != null || title != null || subtitle != null || headerIcon != null || showCloseButton
                if (hasHeader) {
                    if (headerContent != null) {
                        headerContent()
                    } else {
                        AppModalHeader(
                            title = title,
                            subtitle = subtitle,
                            titleBadge = titleBadge,
                            titleColor = titleColor,
                            titleMaxLines = titleMaxLines,
                            headerIcon = headerIcon,
                            headerIconTint = headerIconTint,
                            headerIconBgColor = headerIconBgColor,
                            showCloseButton = showCloseButton,
                            useCompactTitle = true,
                            onDismissRequest = onDismissRequest
                        )
                    }

                    if (showDividerAfterHeader) {
                        Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                        AppDivider()
                    }
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                }

                if (description != null) {
                    AppText.BodySmall(
                        text = description,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                }

                val bodyModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .let { if (maxContentHeight != null) it.heightIn(max = maxContentHeight) else it }
                    .let { if (scrollable) it.verticalScroll(rememberScrollState()) else it }

                Column(modifier = bodyModifier, content = content)

                val hasFooter = footerContent != null || confirmButtonText != null || dismissButtonText != null
                if (hasFooter) {
                    Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
                    AppModalFooter(
                        confirmButtonText = confirmButtonText,
                        onConfirmClick = onConfirmClick,
                        confirmButtonEnabled = confirmButtonEnabled,
                        confirmButtonIcon = confirmButtonIcon,
                        confirmButtonContainerColor = confirmButtonContainerColor,
                        confirmButtonContentColor = confirmButtonContentColor,
                        confirmButtonWeight = confirmButtonWeight,
                        dismissButtonText = dismissButtonText,
                        onDismissClick = onDismissClick,
                        dismissButtonEnabled = dismissButtonEnabled,
                        dismissButtonWeight = dismissButtonWeight,
                        footerContent = footerContent
                    )
                }
            }
        }
    }
}

/**
 * Common application modal bottom sheet container with standardized header, optional scrollable body, and footer controls.
 * Screens invoke [AppBottomSheet] directly and pass pure sheet body composables inside.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    titleBadge: String? = null,
    titleColor: Color = TextPrimary,
    titleMaxLines: Int = 1,
    headerIcon: ImageVector? = null,
    headerIconTint: Color = MedinaSage,
    headerIconBgColor: Color? = null,
    showCloseButton: Boolean = true,
    headerContent: (@Composable () -> Unit)? = null,
    showDividerAfterHeader: Boolean = false,
    description: String? = null,
    confirmButtonText: String? = null,
    onConfirmClick: (() -> Unit)? = null,
    confirmButtonEnabled: Boolean = true,
    confirmButtonIcon: ImageVector? = null,
    confirmButtonContainerColor: Color = MedinaSage,
    confirmButtonContentColor: Color = PureAlabaster,
    confirmButtonWeight: Float = 1f,
    dismissButtonText: String? = null,
    onDismissClick: (() -> Unit)? = onDismissRequest,
    dismissButtonEnabled: Boolean = true,
    dismissButtonWeight: Float = 1f,
    footerContent: (@Composable RowScope.() -> Unit)? = null,
    scrollable: Boolean = false,
    maxContentHeight: Dp? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor: Color = WarmParchment,
    shape: Shape = RoundedCornerShape(
        topStart = AppDimensions.cornerExtraLarge,
        topEnd = AppDimensions.cornerExtraLarge
    ),
    dragHandle: (@Composable () -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentPaddingHorizontal: Dp = AppDimensions.paddingMediumLarge,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = shape,
        containerColor = containerColor,
        dragHandle = dragHandle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = contentPaddingHorizontal)
        ) {
            val hasHeader = headerContent != null || title != null || subtitle != null || headerIcon != null || showCloseButton
            if (hasHeader) {
                if (headerContent != null) {
                    headerContent()
                } else {
                    AppModalHeader(
                        title = title,
                        subtitle = subtitle,
                        titleBadge = titleBadge,
                        titleColor = titleColor,
                        titleMaxLines = titleMaxLines,
                        headerIcon = headerIcon,
                        headerIconTint = headerIconTint,
                        headerIconBgColor = headerIconBgColor,
                        showCloseButton = showCloseButton,
                        useCompactTitle = false,
                        onDismissRequest = onDismissRequest
                    )
                }

                if (showDividerAfterHeader) {
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    AppDivider()
                }
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
            }

            if (description != null) {
                AppText.BodySmall(
                    text = description,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
            }

            val bodyModifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .let { if (maxContentHeight != null) it.heightIn(max = maxContentHeight) else it }
                .let { if (scrollable) it.verticalScroll(rememberScrollState()) else it }

            Column(modifier = bodyModifier, content = content)

            val hasFooter = footerContent != null || confirmButtonText != null || dismissButtonText != null
            if (hasFooter) {
                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
                AppModalFooter(
                    confirmButtonText = confirmButtonText,
                    onConfirmClick = onConfirmClick,
                    confirmButtonEnabled = confirmButtonEnabled,
                    confirmButtonIcon = confirmButtonIcon,
                    confirmButtonContainerColor = confirmButtonContainerColor,
                    confirmButtonContentColor = confirmButtonContentColor,
                    confirmButtonWeight = confirmButtonWeight,
                    dismissButtonText = dismissButtonText,
                    onDismissClick = onDismissClick,
                    dismissButtonEnabled = dismissButtonEnabled,
                    dismissButtonWeight = dismissButtonWeight,
                    footerContent = footerContent
                )
                Spacer(modifier = Modifier.height(AppDimensions.paddingMedium))
            }
        }
    }
}

/**
 * Standardized fixed-width TabRow styled with [MedinaSage] and [WarmParchment].
 */
@Composable
fun AppTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = WarmParchment,
    contentColor: Color = MedinaSage,
    tabs: @Composable () -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = contentColor
            )
        },
        tabs = tabs
    )
}

/**
 * Standardized horizontally scrollable TabRow styled with [MedinaSage] and [WarmParchment].
 */
@Composable
fun AppScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = WarmParchment,
    contentColor: Color = MedinaSage,
    edgePadding: Dp = AppDimensions.paddingZero,
    tabs: @Composable () -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        edgePadding = edgePadding,
        divider = {},
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = contentColor
            )
        },
        tabs = tabs
    )
}

/**
 * Standardized bottom NavigationBar container.
 */
@Composable
fun AppNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = PureAlabaster,
    tonalElevation: Dp = AppDimensions.elevationNone,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        tonalElevation = tonalElevation,
        content = content
    )
}

