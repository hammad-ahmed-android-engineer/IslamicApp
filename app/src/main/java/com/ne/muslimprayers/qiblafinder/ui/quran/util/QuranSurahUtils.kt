package com.ne.muslimprayers.qiblafinder.ui.quran.util

import com.ne.muslimprayers.qiblafinder.ui.quran.AVAILABLE_RECITERS

object QuranSurahUtils {

    /**
     * Exact verse counts for all 114 Surahs in the Holy Quran (1 to 114).
     * Total = 6,236 verses.
     */
    val SURAH_VERSE_COUNTS = intArrayOf(
        7, 286, 200, 176, 120, 165, 206, 75, 129, 109,
        123, 111, 43, 52, 99, 128, 111, 110, 98, 135,
        112, 78, 118, 64, 77, 227, 93, 88, 69, 60,
        34, 30, 73, 54, 45, 83, 182, 88, 75, 85,
        54, 53, 89, 59, 37, 35, 38, 29, 18, 45,
        60, 49, 62, 55, 78, 96, 29, 22, 24, 13,
        14, 11, 11, 18, 12, 12, 30, 52, 52, 44,
        28, 28, 20, 56, 40, 31, 50, 40, 46, 42,
        29, 19, 36, 25, 22, 17, 19, 26, 30, 20,
        15, 21, 11, 8, 8, 19, 5, 8, 8, 11,
        11, 8, 3, 9, 5, 4, 7, 3, 6, 3,
        5, 4, 5, 6
    )

    /**
     * Computes the immutable global Ayah number (1 to 6236) for any Surah and Ayah.
     */
    fun getGlobalAyahNumber(surahNumber: Int, ayahNumberInSurah: Int): Int {
        if (surahNumber < 1 || surahNumber > 114) return ayahNumberInSurah
        var cumulative = 0
        for (i in 0 until (surahNumber - 1)) {
            cumulative += SURAH_VERSE_COUNTS[i]
        }
        return cumulative + ayahNumberInSurah
    }

    /**
     * Returns the CDN audio URL for any verse and reciter using the reciter's supported bitrate.
     */
    fun getAudioUrl(reciterId: String, surahNumber: Int, ayahNumberInSurah: Int): String {
        val effectiveReciter = if (reciterId == "ar.warsh") "ar.husary" else reciterId
        val bitRate = AVAILABLE_RECITERS.find { it.id == effectiveReciter }?.bitRate ?: "128"
        val globalAyah = getGlobalAyahNumber(surahNumber, ayahNumberInSurah)
        return "https://cdn.islamic.network/quran/audio/$bitRate/$effectiveReciter/$globalAyah.mp3"
    }
}
