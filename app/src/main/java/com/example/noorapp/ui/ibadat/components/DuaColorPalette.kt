package com.example.noorapp.ui.ibadat.components

import androidx.compose.ui.graphics.Color
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.DesertOchreLight
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight

data class DuaAccentColors(
    val primary: Color,
    val container: Color
)

private val CATEGORY_PALETTE = listOf(
    DuaAccentColors(primary = MedinaSage, container = MedinaSageLight),
    DuaAccentColors(primary = DesertOchre, container = DesertOchreLight),
    DuaAccentColors(primary = Color(0xFF1565C0), container = Color(0xFFE8F1FB)),
    DuaAccentColors(primary = Color(0xFF00796B), container = Color(0xFFE4F4F2)),
    DuaAccentColors(primary = Color(0xFF7B1FA2), container = Color(0xFFF3E8FA)),
    DuaAccentColors(primary = Color(0xFFC6511A), container = Color(0xFFFBECE4))
)

/**
 * Returns a consistent, vibrant Islamic jewel-tone accent pair per Dua category
 * so Dua cards and category badges render in multi-color instead of single-color green.
 */
fun duaCategoryAccent(category: String): DuaAccentColors {
    return when (category) {
        "Quranic (Rabbana)" -> CATEGORY_PALETTE[0]
        "Morning & Evening" -> CATEGORY_PALETTE[1]
        "Prayer & Mosque" -> CATEGORY_PALETTE[2]
        "Forgiveness & Mercy" -> CATEGORY_PALETTE[4]
        "Protection & Hardship" -> CATEGORY_PALETTE[5]
        "Daily Life & Home" -> CATEGORY_PALETTE[3]
        "Food & Drink" -> CATEGORY_PALETTE[1]
        "Travel & Journey" -> CATEGORY_PALETTE[2]
        "Illness & Healing" -> CATEGORY_PALETTE[0]
        "Praise & Remembrance" -> CATEGORY_PALETTE[4]
        "Family & Society" -> CATEGORY_PALETTE[5]
        "Nature & Weather" -> CATEGORY_PALETTE[3]
        "Death & Afterlife" -> CATEGORY_PALETTE[4]
        else -> {
            val idx = (category.hashCode() and 0x7FFFFFFF) % CATEGORY_PALETTE.size
            CATEGORY_PALETTE[idx]
        }
    }
}

