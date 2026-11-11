package com.example.noorapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QiblaApiResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: QiblaDataDto?
)

data class QiblaDataDto(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("direction") val direction: Float
)
