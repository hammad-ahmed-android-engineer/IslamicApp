package com.ne.muslimprayers.qiblafinder.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Centralized semantic dimensions system across Islamic App.
 * Consistently leverages responsive scalable SDP units for phone, foldable, and tablet layouts.
 */
object AppDimensions {

    // Spacing & Padding Tokens
    val paddingZero: Dp @Composable get() = 0.sdp
    val paddingMicro: Dp @Composable get() = 2.sdp
    val paddingTiny: Dp @Composable get() = 4.sdp
    val paddingSmall: Dp @Composable get() = 8.sdp
    val paddingMedium: Dp @Composable get() = 16.sdp
    val paddingLarge: Dp @Composable get() = 24.sdp
    val paddingExtraLarge: Dp @Composable get() = 32.sdp

    // Corner Radii Tokens
    val cornerMicro: Dp @Composable get() = 4.sdp
    val cornerSmall: Dp @Composable get() = 8.sdp
    val cornerMedium: Dp @Composable get() = 12.sdp
    val cornerLarge: Dp @Composable get() = 16.sdp
    val cornerExtraLarge: Dp @Composable get() = 20.sdp
    val cornerPill: Dp @Composable get() = 28.sdp

    val radiusSmall: Dp @Composable get() = cornerSmall
    val radiusMedium: Dp @Composable get() = cornerMedium
    val radiusLarge: Dp @Composable get() = cornerLarge
    val radiusExtraLarge: Dp @Composable get() = cornerExtraLarge
    val radiusFull: Dp @Composable get() = cornerPill

    // Icon Size Tokens
    val iconTiny: Dp @Composable get() = 12.sdp
    val iconSmall: Dp @Composable get() = 16.sdp
    val iconMedium: Dp @Composable get() = 20.sdp
    val iconLarge: Dp @Composable get() = 24.sdp
    val iconExtraLarge: Dp @Composable get() = 36.sdp
    val iconHuge: Dp @Composable get() = 48.sdp

    // Component Dimensions
    val buttonHeight: Dp @Composable get() = 48.sdp
    val buttonSmallHeight: Dp @Composable get() = 36.sdp
    val searchFieldHeight: Dp @Composable get() = 48.sdp
    val searchBarHeight: Dp @Composable get() = 48.sdp
    val dialogPadding: Dp @Composable get() = paddingLarge
    val fabSize: Dp @Composable get() = 56.sdp
    val avatarSize: Dp @Composable get() = 40.sdp
    val prayerChipWidth: Dp @Composable get() = 112.sdp
    val prayerChipHeight: Dp @Composable get() = 96.sdp
    val compassDialSize: Dp @Composable get() = 260.sdp

    // Elevations & Borders
    val elevationNone: Dp @Composable get() = 0.sdp
    val elevationLow: Dp @Composable get() = 2.sdp
    val elevationMedium: Dp @Composable get() = 4.sdp
    val elevationHigh: Dp @Composable get() = 8.sdp
    val borderWidthThin: Dp @Composable get() = 1.sdp
    val borderWidthThick: Dp @Composable get() = 2.sdp
}
