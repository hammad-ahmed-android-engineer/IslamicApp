package com.example.noorapp.data.repository

import com.example.noorapp.core.common.PrayerNotificationMode
import com.example.noorapp.core.common.PrayerSettingsManager
import com.example.noorapp.core.common.PrayerSettingsState
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.repository.PrayerSettingsRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PrayerSettingsRepositoryImpl @Inject constructor(
    private val prayerSettingsManager: PrayerSettingsManager
) : PrayerSettingsRepository {

    override val settingsState: StateFlow<PrayerSettingsState>
        get() = prayerSettingsManager.settingsState

    override fun getCurrentSettings(): PrayerSettingsState =
        prayerSettingsManager.getCurrentSettings()

    override fun setManualCalculationMethod(methodIndex: Int) {
        prayerSettingsManager.setManualCalculationMethod(methodIndex)
    }

    override fun resetToAutoDetect(latitude: Double, longitude: Double) {
        prayerSettingsManager.resetToAutoDetect(latitude, longitude)
    }

    override fun setManualAsrJuristic(asrIndex: Int) {
        prayerSettingsManager.setManualAsrJuristic(asrIndex)
    }

    override fun setHijriOffset(offset: Int) {
        prayerSettingsManager.setHijriOffset(offset)
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        prayerSettingsManager.setNotificationsEnabled(enabled)
    }

    override fun setPrayerNotificationMode(prayerType: PrayerType, mode: PrayerNotificationMode) {
        prayerSettingsManager.setPrayerNotificationMode(prayerType, mode)
    }
}

