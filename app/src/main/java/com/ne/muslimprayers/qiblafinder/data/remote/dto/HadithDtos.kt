package com.ne.muslimprayers.qiblafinder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HadithSectionResponse(
    @SerializedName("metadata") val metadata: HadithMetadata?,
    @SerializedName("hadiths") val hadiths: List<HadithItemDto> = emptyList()
)

data class HadithMetadata(
    @SerializedName("name") val name: String?,
    @SerializedName("sections") val sections: Map<String, String>? = null,
    @SerializedName("section") val section: Map<String, String>? = null
)

data class HadithItemDto(
    @SerializedName("hadithnumber") val hadithNumber: Any?,
    @SerializedName("arabicnumber") val arabicNumber: Any?,
    @SerializedName("text") val text: String = "",
    @SerializedName("reference") val reference: HadithReferenceDto?
)

data class HadithReferenceDto(
    @SerializedName("book") val book: Any?,
    @SerializedName("hadith") val hadith: Any?
)
