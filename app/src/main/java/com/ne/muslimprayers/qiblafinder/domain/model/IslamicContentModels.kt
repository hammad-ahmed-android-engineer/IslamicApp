package com.ne.muslimprayers.qiblafinder.domain.model

data class Surah(
    val number: Int,
    val name: String,
    val arabicName: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationType: String // "Meccan" or "Medinan"
)

data class Ayah(
    val numberInSurah: Int,
    val arabicText: String,
    val englishText: String,
    val urduText: String = "",
    val transliteration: String = "",
    val juz: Int = 1,
    val audioUrl: String = "",
    val indopakText: String = "",
    val globalAyahNumber: Int = 0,
    val surahNumber: Int = 1
)

data class Hadith(
    val id: Int,
    val chapter: String,
    val narrator: String,
    val arabicText: String,
    val englishText: String,
    val source: String
)

data class DuaItem(
    val id: Int,
    val title: String,
    val category: String,
    val arabicText: String,
    val transliteration: String,
    val translation: String,
    val reference: String
)

data class TasbihState(
    val count: Int = 0,
    val target: Int = 33,
    val totalLaps: Int = 0,
    val currentPhrase: String = "SubhanAllah",
    val arabicPhrase: String = "سُبْحَانَ اللَّهِ"
)
