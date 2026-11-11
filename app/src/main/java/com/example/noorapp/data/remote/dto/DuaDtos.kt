package com.example.noorapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HisnMuslimResponse(
    @SerializedName("English") val chapters: List<HisnChapterDto>? = emptyList()
)

data class HisnChapterDto(
    @SerializedName("ID") val id: Any? = null,
    @SerializedName("TITLE") val title: String? = "",
    @SerializedName("AUDIO_URL") val audioUrl: String? = null,
    @SerializedName("TEXT") val texts: List<HisnDuaItemDto>? = emptyList()
)

data class HisnDuaItemDto(
    @SerializedName("ID") val id: Any? = null,
    @SerializedName("ARABIC_TEXT") val arabicText: String? = "",
    @SerializedName("LANGUAGE_ARABIC_TRANSLATED_TEXT") val transliteration: String? = "",
    @SerializedName("TRANSLATED_TEXT") val translatedText: String? = "",
    @SerializedName("REPEAT") val repeat: Any? = null,
    @SerializedName("AUDIO") val audio: String? = null
)
