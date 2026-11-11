package com.example.noorapp.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// DESIGN MOCKUP PALETTE (Alabaster Parchment & Warm Sage Mode)
// =========================================================================
val WarmParchment = Color(0xFFFBF9F5)
val PureAlabaster = Color(0xFFFFFFFF)
val MedinaSage = Color(0xFF1F6E58)
val MedinaSageDark = Color(0xFF175342)
val MedinaSageLight = Color(0xFFE8F2EE)
val DesertOchre = Color(0xFFC88A32)
val DesertOchreLight = Color(0xFFF9F3E8)
val TextPrimary = Color(0xFF1C2421)
val TextSecondary = Color(0xFF5F6D66)
val MutedText = Color(0xFF8C9B93)
val DividerColor = Color(0xFFE5E7E2)

// Dark Theme Variants
val DarkObsidian = Color(0xFF0F1714)
val DarkSurfaceCard = Color(0xFF172420)
val DarkElevatedCard = Color(0xFF1F302A)

// Backward-compatible Legacy Palette Aliases
val DeepEmerald = MedinaSage
val ForestGreen = MedinaSageDark
val IslamicGreen = MedinaSage
val SoftSage = Color(0xFFA1C5B8)
val PaleSageBackground = WarmParchment

val MutedGold = DesertOchre
val WarmGold = DesertOchre
val SoftSand = DesertOchreLight
val Ivory = PureAlabaster
val WarmWhite = PureAlabaster

val PrimaryTextLight = TextPrimary
val SecondaryTextLight = TextSecondary
val MutedTextLight = MutedText
val DividerLight = DividerColor
val SubtleSurfaceLight = MedinaSageLight

val DarkBackground = DarkObsidian
val DarkSurface = DarkSurfaceCard
val DarkElevatedSurface = DarkElevatedCard
val PrimaryGreenDark = Color(0xFF72B095)
val SecondaryGreenDark = Color(0xFF98BEAB)
val GoldAccentDark = DesertOchre
val PrimaryTextDark = Color(0xFFF1F5F3)
val SecondaryTextDark = Color(0xFFADBCAF)
val DividerDark = Color(0xFF2B3C35)

val GoldPrimary = DesertOchre
val GoldBright = DesertOchre
val GoldLight = DesertOchreLight
val GoldDark = Color(0xFF9E6B20)

val RoyalEmerald950 = DarkBackground
val RoyalEmerald900 = Color(0xFF121E1A)
val RoyalEmerald850 = DarkSurface
val RoyalEmerald800 = DarkElevatedSurface
val RoyalEmerald700 = MedinaSageDark
val RoyalEmerald600 = MedinaSage
val RoyalEmerald500 = MedinaSage
val RoyalEmerald400 = PrimaryGreenDark
val RoyalEmerald300 = SecondaryGreenDark
val RoyalEmerald200 = SoftSage
val RoyalEmerald100 = MedinaSageLight
val RoyalEmerald50  = WarmParchment

val Emerald950 = RoyalEmerald950
val Emerald900 = RoyalEmerald900
val Emerald850 = RoyalEmerald850
val Emerald800 = RoyalEmerald800
val Emerald700 = RoyalEmerald700
val Emerald600 = RoyalEmerald600
val Emerald500 = RoyalEmerald500
val Emerald400 = RoyalEmerald400
val Emerald300 = RoyalEmerald300
val Emerald200 = RoyalEmerald200
val Emerald100 = RoyalEmerald100
val Emerald50  = RoyalEmerald50

val Sapphire950 = RoyalEmerald950
val Sapphire900 = RoyalEmerald900
val Sapphire800 = RoyalEmerald800
val Sapphire700 = RoyalEmerald700
val Sapphire600 = RoyalEmerald600
val Sapphire500 = RoyalEmerald500
val Sapphire400 = RoyalEmerald400
val Sapphire300 = RoyalEmerald300
val Sapphire100 = RoyalEmerald100
val Sapphire50  = RoyalEmerald50

val SuccessGreen = Color(0xFF2E8B57)
val AlertRed = Color(0xFFD32F2F)
val InfoBlue = Color(0xFF2B6CB0)

val TextPrimaryDark = PrimaryTextDark
val TextSecondaryDark = SecondaryTextDark

// Gradients
val HeroBannerGradient = Brush.verticalGradient(
    colors = listOf(
        MedinaSage,
        MedinaSageDark
    )
)

val HeroBannerGradientDark = Brush.verticalGradient(
    colors = listOf(
        DarkElevatedSurface,
        DarkSurface
    )
)

val ArchBorderGradient = Brush.linearGradient(
    colors = listOf(
        DesertOchre.copy(alpha = 0.5f),
        MedinaSage.copy(alpha = 0.25f),
        DesertOchre.copy(alpha = 0.35f)
    )
)

val GoldBorderGradient = ArchBorderGradient
val EmeraldHeroGradient = HeroBannerGradient
val EmeraldCardGradient = Brush.verticalGradient(
    colors = listOf(DarkElevatedSurface, DarkSurface)
)
val SapphireLinearGradient = EmeraldCardGradient
val EmeraldLinearGradient = EmeraldCardGradient
val DarkCardGradient = EmeraldCardGradient

val GoldGlowColor = Color(0x33C88A32)
val EmeraldGlowColor = Color(0x261F6E58)
val SapphireGlowColor = EmeraldGlowColor
