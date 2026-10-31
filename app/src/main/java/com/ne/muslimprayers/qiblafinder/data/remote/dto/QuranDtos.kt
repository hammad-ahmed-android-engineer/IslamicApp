package com.ne.muslimprayers.qiblafinder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuranSurahResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<SurahEditionData>
)

data class QuranSurahListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<SurahDto>
)

data class SurahDto(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    @SerializedName("revelationType") val revelationType: String
)

data class SurahEditionData(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("revelationType") val revelationType: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    @SerializedName("ayahs") val ayahs: List<AyahData>,
    @SerializedName("edition") val edition: EditionInfo?
)

data class AyahData(
    @SerializedName("number") val number: Int,
    @SerializedName("text") val text: String,
    @SerializedName("numberInSurah") val numberInSurah: Int,
    @SerializedName("juz") val juz: Int,
    @SerializedName("manzil") val manzil: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("ruku") val ruku: Int,
    @SerializedName("hizbQuarter") val hizbQuarter: Int,
    @SerializedName("sajda") val sajda: Any?
)

data class EditionInfo(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("language") val language: String,
    @SerializedName("name") val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("format") val format: String,
    @SerializedName("type") val type: String
)

// Quran.com API v4 Data Models
data class QuranDotComVersesResponse(
    @SerializedName("verses") val verses: List<QuranDotComVerseDto> = emptyList(),
    @SerializedName("pagination") val pagination: QuranDotComPaginationDto? = null
)

data class QuranDotComVerseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("verse_number") val verseNumber: Int,
    @SerializedName("verse_key") val verseKey: String,
    @SerializedName("text_uthmani") val textUthmani: String?,
    @SerializedName("text_indopak") val textIndopak: String?,
    @SerializedName("juz_number") val juzNumber: Int?,
    @SerializedName("page_number") val pageNumber: Int?,
    @SerializedName("translations") val translations: List<QuranDotComTranslationDto>?
)

data class QuranDotComTranslationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("resource_id") val resourceId: Int,
    @SerializedName("text") val text: String
)

data class QuranDotComPaginationDto(
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("total_records") val totalRecords: Int
)

data class QuranDotComTafsirResponse(
    @SerializedName("tafsir") val tafsir: QuranDotComTafsirDto?
)

data class QuranDotComTafsirDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("tafsir_id") val tafsirId: Int?,
    @SerializedName("tafsir_name") val tafsirName: String?,
    @SerializedName("author_name") val authorName: String?,
    @SerializedName("verse_key") val verseKey: String?,
    @SerializedName("text") val text: String?
)
