package com.example.noorapp.domain.usecase.settings

import com.example.noorapp.core.common.PrayerSettingsState
import com.example.noorapp.domain.repository.PrayerSettingsRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObservePrayerSettingsUseCase @Inject constructor(
    private val prayerSettingsRepository: PrayerSettingsRepository
) {
    operator fun invoke(): StateFlow<PrayerSettingsState> = prayerSettingsRepository.settingsState

    fun current(): PrayerSettingsState = prayerSettingsRepository.getCurrentSettings()
}

