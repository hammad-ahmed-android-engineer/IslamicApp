package com.example.noorapp.domain.model

/**
 * Resolved user geographic coordinates and city metadata.
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val cityName: String = "",
    val isFallback: Boolean = false,
    val hasPermission: Boolean = true
)

