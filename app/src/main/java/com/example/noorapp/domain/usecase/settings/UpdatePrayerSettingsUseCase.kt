package com.example.noorapp.domain.usecase.settings

import com.example.noorapp.core.common.PrayerNotificationMode
import com.example.noorapp.domain.model.PrayerType
import com.example.noorapp.domain.repository.PrayerSettingsRepository
import javax.inject.Inject

class UpdatePrayerSettingsUseCase @Inject constructor(
    private val prayerSettingsRepository: PrayerSettingsRepository
) {
    fun setCalculationMethod(methodIndex: Int) {
        prayerSettingsRepository.setManualCalculationMethod(methodIndex)
    }

    fun resetMethodToAutoDetect(latitude: Double, longitude: Double) {
        prayerSettingsRepository.resetToAutoDetect(latitude, longitude)
    }

    fun setAsrJuristic(asrIndex: Int) {
        prayerSettingsRepository.setManualAsrJuristic(asrIndex)
    }

    fun setHijriOffset(offset: Int) {
        prayerSettingsRepository.setHijriOffset(offset)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prayerSettingsRepository.setNotificationsEnabled(enabled)
    }

    fun setPrayerNotificationMode(prayerType: PrayerType, mode: PrayerNotificationMode) {
        prayerSettingsRepository.setPrayerNotificationMode(prayerType, mode)
    }
}

