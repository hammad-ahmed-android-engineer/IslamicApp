package com.ne.muslimprayers.qiblafinder.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

/**
 * Centralized semantic dimensions and responsive typographic size tokens across Islamic App.
 * Consistently leverages scalable `.sdp` and `.ssp` units for phone, foldable, and tablet layouts.
 */
object AppDimensions {

    // Spacing & Padding Tokens
    val paddingZero: Dp @Composable get() = 0.sdp
    val paddingHairline: Dp @Composable get() = 1.sdp
    val paddingMicro: Dp @Composable get() = 2.sdp
    val paddingTiny: Dp @Composable get() = 4.sdp
    val paddingExtraSmall: Dp @Composable get() = 6.sdp
    val paddingSmall: Dp @Composable get() = 8.sdp
    val paddingRegular: Dp @Composable get() = 10.sdp
    val paddingMediumSmall: Dp @Composable get() = 12.sdp
    val paddingMedium: Dp @Composable get() = 16.sdp
    val paddingMediumLarge: Dp @Composable get() = 20.sdp
    val paddingLarge: Dp @Composable get() = 24.sdp
    val paddingExtraLarge: Dp @Composable get() = 32.sdp
    val paddingHuge: Dp @Composable get() = 40.sdp
    val readerBottomPadding: Dp @Composable get() = 84.sdp

    // Corner Radii Tokens
    val cornerMicro: Dp @Composable get() = 4.sdp
    val cornerExtraSmall: Dp @Composable get() = 6.sdp
    val cornerSmall: Dp @Composable get() = 8.sdp
    val cornerMediumSmall: Dp @Composable get() = 10.sdp
    val cornerMedium: Dp @Composable get() = 12.sdp
    val cornerMediumLarge: Dp @Composable get() = 14.sdp
    val cornerLarge: Dp @Composable get() = 16.sdp
    val cornerExtraLarge: Dp @Composable get() = 20.sdp
    val cornerDialog: Dp @Composable get() = 24.sdp
    val cornerPill: Dp @Composable get() = 28.sdp

    val radiusSmall: Dp @Composable get() = cornerSmall
    val radiusMedium: Dp @Composable get() = cornerMedium
    val radiusLarge: Dp @Composable get() = cornerLarge
    val radiusExtraLarge: Dp @Composable get() = cornerExtraLarge
    val radiusFull: Dp @Composable get() = cornerPill

    // Icon Size Tokens
    val iconMicro: Dp @Composable get() = 10.sdp
    val iconTiny: Dp @Composable get() = 12.sdp
    val iconExtraSmall: Dp @Composable get() = 14.sdp
    val iconSmall: Dp @Composable get() = 16.sdp
    val iconRegular: Dp @Composable get() = 18.sdp
    val iconMedium: Dp @Composable get() = 20.sdp
    val iconLarge: Dp @Composable get() = 24.sdp
    val iconExtraLarge: Dp @Composable get() = 36.sdp
    val iconHuge: Dp @Composable get() = 48.sdp

    // Component Dimensions
    val buttonHeight: Dp @Composable get() = 48.sdp
    val buttonSmallHeight: Dp @Composable get() = 36.sdp
    val actionCircleSize: Dp @Composable get() = 30.sdp
    val iconButtonCompact: Dp @Composable get() = 34.sdp
    val badgeCircleSmall: Dp @Composable get() = 26.sdp
    val badgeCircleMedium: Dp @Composable get() = 38.sdp
    val badgeCircleLarge: Dp @Composable get() = 48.sdp
    val searchFieldHeight: Dp @Composable get() = 48.sdp
    val searchBarHeight: Dp @Composable get() = 48.sdp
    val dialogPadding: Dp @Composable get() = paddingLarge
    val dialogMinHeight: Dp @Composable get() = 160.sdp
    val dialogContentMaxHeightCompact: Dp @Composable get() = 220.sdp
    val dialogContentMaxHeightExpanded: Dp @Composable get() = 360.sdp
    val fabSize: Dp @Composable get() = 56.sdp
    val logoAvatarSize: Dp @Composable get() = 52.sdp
    val avatarSize: Dp @Composable get() = 40.sdp
    val splashHaloSizeCompact: Dp @Composable get() = 114.sdp
    val splashEmblemSizeCompact: Dp @Composable get() = 92.sdp
    val splashHaloSize: Dp @Composable get() = 136.sdp
    val splashEmblemSize: Dp @Composable get() = 110.sdp
    val loaderSize: Dp @Composable get() = 28.sdp
    val calendarDayChipWidth: Dp @Composable get() = 64.sdp
    val calendarDayChipHeight: Dp @Composable get() = 84.sdp
    val progressLinearHeight: Dp @Composable get() = 6.sdp
    val minNumberWidth: Dp @Composable get() = 24.sdp
    val arabicTitleMinWidth: Dp @Composable get() = 82.sdp
    val accentBarWidth: Dp @Composable get() = 3.sdp
    val dividerHairline: Dp @Composable get() = 0.8.sdp
    val progressBarHeight: Dp @Composable get() = 3.sdp
    val prayerChipWidth: Dp @Composable get() = 112.sdp
    val prayerChipHeight: Dp @Composable get() = 96.sdp
    val compassDialSize: Dp @Composable get() = 260.sdp
    val compassKaabaBadgeSize: Dp @Composable get() = 44.sdp
    val compassKaabaIconSize: Dp @Composable get() = 36.sdp
    val sliderMinThumbTrack: Dp @Composable get() = 1.sdp

    // Elevations & Borders
    val elevationNone: Dp @Composable get() = 0.sdp
    val elevationSubtle: Dp @Composable get() = 1.sdp
    val elevationLow: Dp @Composable get() = 2.sdp
    val elevationMedium: Dp @Composable get() = 4.sdp
    val elevationHigh: Dp @Composable get() = 8.sdp
    val borderWidthHairline: Dp @Composable get() = 0.8.sdp
    val borderWidthThin: Dp @Composable get() = 1.sdp
    val borderWidthMedium: Dp @Composable get() = 1.5.sdp
    val borderWidthThick: Dp @Composable get() = 2.sdp
    val borderWidthBold: Dp @Composable get() = 4.sdp

    // Responsive Text Size Tokens (SSP)
    val textMicro: TextUnit @Composable get() = 10.ssp
    val textCaption: TextUnit @Composable get() = 11.ssp
    val textBodySmall: TextUnit @Composable get() = 12.ssp
    val textBodyMedium: TextUnit @Composable get() = 14.ssp
    val textBodyLarge: TextUnit @Composable get() = 16.ssp
    val textTitleMedium: TextUnit @Composable get() = 17.ssp
    val textHeadingMedium: TextUnit @Composable get() = 20.ssp
    val textArabicPreview: TextUnit @Composable get() = 17.ssp
    val textArabicListTitle: TextUnit @Composable get() = 24.ssp
    val textArabicCardHero: TextUnit @Composable get() = 32.ssp
    val textArabicBanner: TextUnit @Composable get() = 30.ssp
    val textBismillahBanner: TextUnit @Composable get() = 20.ssp
    val textHeroCounter: TextUnit @Composable get() = 46.ssp
}
