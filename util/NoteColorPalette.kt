package com.example.notora.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.notora.common.util.AppUtils

data class NoteColor(
    val hex: String,
    val name: String,
    val lightBg: Color,
    val darkBg: Color
)

object NoteColorPalette {
    // Soft, clean, bright pastel paper colors (NO dark background colors)
    val Default = NoteColor("#FFFFFF", "Pure White", Color(0xFFFFFFFF), Color(0xFFFFFFFF))
    val WarmVanilla = NoteColor("#FFF8E7", "Warm Vanilla", Color(0xFFFFF9EB), Color(0xFFFFF9EB))
    val Buttercup = NoteColor("#FFF9C4", "Buttercup Yellow", Color(0xFFFEF9C3), Color(0xFFFEF9C3))
    val SoftPeach = NoteColor("#FFE0B2", "Soft Peach", Color(0xFFFFEDD5), Color(0xFFFFEDD5))
    val CoralBlossom = NoteColor("#FFCDD2", "Coral Blossom", Color(0xFFFFE4E6), Color(0xFFFFE4E6))
    val SakuraPink = NoteColor("#F8BBD0", "Sakura Pink", Color(0xFFFCE7F3), Color(0xFFFCE7F3))
    val LilacMist = NoteColor("#E1BEE7", "Lilac Mist", Color(0xFFF3E8FF), Color(0xFFF3E8FF))
    val Periwinkle = NoteColor("#D1C4E9", "Periwinkle", Color(0xFFEDE9FE), Color(0xFFEDE9FE))
    val SkyAzure = NoteColor("#BBDEFB", "Sky Azure", Color(0xFFE0F2FE), Color(0xFFE0F2FE))
    val MatchaSage = NoteColor("#C8E6C9", "Matcha Sage", Color(0xFFDCFCE7), Color(0xFFDCFCE7))
    val MintFrost = NoteColor("#DCEDC8", "Mint Frost", Color(0xFFD1FAE5), Color(0xFFD1FAE5))
    val SoftTeal = NoteColor("#B2DFDB", "Soft Teal", Color(0xFFCCFBF1), Color(0xFFCCFBF1))
    val CloudSlate = NoteColor("#F5F5F5", "Cloud Slate", Color(0xFFF1F5F9), Color(0xFFF1F5F9))

    val colors = listOf(
        Default, WarmVanilla, Buttercup, SoftPeach, CoralBlossom,
        SakuraPink, LilacMist, Periwinkle, SkyAzure, MatchaSage,
        MintFrost, SoftTeal, CloudSlate
    )

    @Composable
    fun getBackgroundColor(hex: String): Color {
        val found = colors.firstOrNull { it.hex.equals(hex, ignoreCase = true) }
        return found?.lightBg ?: AppUtils.parseHexColor(hex, fallback = Color.White)
    }
}
