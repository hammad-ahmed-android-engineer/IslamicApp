package com.example.noorapp.domain.repository

import com.example.noorapp.core.common.PrayerNotificationMode
import com.example.noorapp.core.common.PrayerSettingsState
import com.example.noorapp.domain.model.PrayerType
import kotlinx.coroutines.flow.StateFlow

interface PrayerSettingsRepository {
    val settingsState: StateFlow<PrayerSettingsState>
    fun getCurrentSettings(): PrayerSettingsState
    fun setManualCalculationMethod(methodIndex: Int)
    fun resetToAutoDetect(latitude: Double, longitude: Double)
    fun setManualAsrJuristic(asrIndex: Int)
    fun setHijriOffset(offset: Int)
    fun setNotificationsEnabled(enabled: Boolean)
    fun setPrayerNotificationMode(prayerType: PrayerType, mode: PrayerNotificationMode)
}

