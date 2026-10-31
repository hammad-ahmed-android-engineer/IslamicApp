package com.ne.muslimprayers.qiblafinder.core.theme

import android.content.Context
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ArabicFontItem(
    val key: String,
    val title: String,
    val subtitle: String,
    val fontFamily: FontFamily
)

object ArabicFontManager {

    private const val PREFS_NAME = "quran_prefs"
    private const val KEY_ARABIC_FONT = "selected_quran_font"

    val AVAILABLE_FONTS: List<ArabicFontItem> = listOf(
        ArabicFontItem("muhammadi", "Muhammadi", "Indo-Pak Script (Bold)", MuhammadiFontFamily),
        ArabicFontItem("hafs", "Hafs", "Medina Standard (Uthmani)", HafsFontFamily),
        ArabicFontItem("al_majeed", "Al Majeed", "South Asian Quranic Script", AlMajeedFontFamily),
        ArabicFontItem("al_mushaf", "Al Mushaf", "Classic Mushaf Calligraphy", AlMushafFontFamily),
        ArabicFontItem("al_qalam", "Al Qalam", "Qalam Quranic Script", AlQalamFontFamily),
        ArabicFontItem("nabi", "Nabi", "Naskh Calligraphic Script", NabiFontFamily),
        ArabicFontItem("pdms_saleem", "PDMS Saleem", "Pakistani Classical Script", PdmsSaleemFontFamily)
    )

    private val _currentFontKey = MutableStateFlow("muhammadi")
    val currentFontKey: StateFlow<String> = _currentFontKey.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_ARABIC_FONT, "muhammadi") ?: "muhammadi"
        _currentFontKey.value = normalizeKey(saved)
    }

    fun setFont(context: Context, key: String) {
        val normalized = normalizeKey(key)
        _currentFontKey.value = normalized
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ARABIC_FONT, normalized).apply()
    }

    fun getFontFamily(key: String = _currentFontKey.value): FontFamily {
        return when (normalizeKey(key)) {
            "hafs", "uthmani" -> HafsFontFamily
            "al_majeed" -> AlMajeedFontFamily
            "al_mushaf" -> AlMushafFontFamily
            "al_qalam" -> AlQalamFontFamily
            "nabi" -> NabiFontFamily
            "pdms", "pdms_saleem" -> PdmsSaleemFontFamily
            else -> MuhammadiFontFamily
        }
    }

    private fun normalizeKey(key: String): String {
        return when (key.lowercase()) {
            "indopak" -> "muhammadi"
            "uthmani" -> "hafs"
            "pdms" -> "pdms_saleem"
            else -> key
        }
    }
}
