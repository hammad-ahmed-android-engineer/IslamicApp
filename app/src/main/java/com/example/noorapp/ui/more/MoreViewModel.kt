package com.example.noorapp.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noorapp.R
import com.example.noorapp.core.common.PrayerCalculationMethod
import com.example.noorapp.domain.usecase.GetPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.SchedulePrayerAlarmUseCase
import com.example.noorapp.domain.usecase.SyncPrayerAlarmsUseCase
import com.example.noorapp.domain.usecase.audio.PlayAudioPreviewUseCase
import com.example.noorapp.domain.usecase.location.GetCurrentLocationUseCase
import com.example.noorapp.domain.usecase.settings.ObservePrayerSettingsUseCase
import com.example.noorapp.domain.usecase.settings.UpdatePrayerSettingsUseCase
import com.example.noorapp.domain.usecase.zakat.CalculateZakatUseCase
import com.example.noorapp.domain.usecase.zakat.ZakatAssets
import com.example.noorapp.ui.more.components.AsrSchoolOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val calculateZakatUseCase: CalculateZakatUseCase,
    private val observePrayerSettingsUseCase: ObservePrayerSettingsUseCase,
    private val updatePrayerSettingsUseCase: UpdatePrayerSettingsUseCase,
    private val getPrayerAlarmsUseCase: GetPrayerAlarmsUseCase,
    private val syncPrayerAlarmsUseCase: SyncPrayerAlarmsUseCase,
    private val schedulePrayerAlarmUseCase: SchedulePrayerAlarmUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val playAudioPreviewUseCase: PlayAudioPreviewUseCase
) : ViewModel() {

    private val initialSettings = observePrayerSettingsUseCase.current()

    private val _uiState = MutableStateFlow(
        MoreUiState(
            notificationsEnabled = initialSettings.notificationsEnabled,
            selectedMethodIndex = initialSettings.methodIndex,
            selectedAsrJuristic = initialSettings.asrJuristic,
            selectedHijriOffset = initialSettings.hijriOffset,
            isMethodManuallySelected = initialSettings.isMethodManuallySelected,
            isAsrManuallySelected = initialSettings.isAsrManuallySelected,
            namesOfAllah = create99NamesList()
        )
    )
    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    val calculationMethodResIds: List<Int> = PrayerCalculationMethod.entries.map { it.displayNameRes }

    val asrSchools = listOf(
        AsrSchoolOption(
            titleRes = R.string.prayers_asr_standard_title,
            descRes = R.string.prayers_asr_standard_desc
        ),
        AsrSchoolOption(
            titleRes = R.string.prayers_asr_hanafi_title,
            descRes = R.string.prayers_asr_hanafi_desc
        )
    )

    val hijriOffsets = listOf(-2, -1, 0, 1, 2)

    init {
        viewModelScope.launch {
            observePrayerSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        notificationsEnabled = settings.notificationsEnabled,
                        selectedMethodIndex = settings.methodIndex,
                        selectedAsrJuristic = settings.asrJuristic,
                        selectedHijriOffset = settings.hijriOffset,
                        isMethodManuallySelected = settings.isMethodManuallySelected,
                        isAsrManuallySelected = settings.isAsrManuallySelected
                    )
                }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        updatePrayerSettingsUseCase.setNotificationsEnabled(enabled)
        viewModelScope.launch {
            syncPrayerAlarmsUseCase.initDefaultIfEmpty()
            val alarms = getPrayerAlarmsUseCase().firstOrNull().orEmpty()
            for (alarm in alarms) {
                if (enabled && alarm.isAlarmActive) {
                    schedulePrayerAlarmUseCase.schedule(alarm)
                } else {
                    schedulePrayerAlarmUseCase.cancel(alarm.id)
                }
            }
        }
    }

    fun selectMethod(index: Int) {
        updatePrayerSettingsUseCase.setCalculationMethod(index)
        _uiState.update { it.copy(showMethodDialog = false) }
    }

    fun resetMethodToAutoDetect() {
        viewModelScope.launch {
            val savedCoords = getCurrentLocationUseCase.getLastSavedCoordinates()
            val coords = savedCoords ?: getCurrentLocationUseCase(includeCityName = false).let {
                it.latitude to it.longitude
            }
            updatePrayerSettingsUseCase.resetMethodToAutoDetect(
                latitude = coords.first,
                longitude = coords.second
            )
            _uiState.update { it.copy(showMethodDialog = false) }
        }
    }

    fun selectAsrJuristic(index: Int) {
        updatePrayerSettingsUseCase.setAsrJuristic(index)
        _uiState.update { it.copy(showAsrDialog = false) }
    }

    fun selectHijriOffset(offset: Int) {
        updatePrayerSettingsUseCase.setHijriOffset(offset)
        _uiState.update { it.copy(showHijriOffsetDialog = false) }
    }

    fun toggleMethodDialog(show: Boolean) {
        _uiState.update { it.copy(showMethodDialog = show) }
    }

    fun toggleAsrDialog(show: Boolean) {
        _uiState.update { it.copy(showAsrDialog = show) }
    }

    fun toggleHijriOffsetDialog(show: Boolean) {
        _uiState.update { it.copy(showHijriOffsetDialog = show) }
    }

    fun toggleZakatDialog(show: Boolean) {
        _uiState.update { it.copy(showZakatDialog = show) }
    }

    fun toggleNamesDialog(show: Boolean) {
        if (!show) stopNameAudio()
        _uiState.update { it.copy(showNamesDialog = show) }
    }

    fun updateZakatInputs(
        cash: String = _uiState.value.cashSavings,
        gold: String = _uiState.value.goldValue,
        silver: String = _uiState.value.silverValue,
        investments: String = _uiState.value.investments,
        liabilities: String = _uiState.value.liabilities
    ) {
        _uiState.update {
            it.copy(
                cashSavings = cash,
                goldValue = gold,
                silverValue = silver,
                investments = investments,
                liabilities = liabilities
            )
        }
    }

    fun calculateZakat() {
        val state = _uiState.value
        val assets = ZakatAssets(
            cashSavings = state.cashSavings.toDoubleOrNull() ?: 0.0,
            goldValue = state.goldValue.toDoubleOrNull() ?: 0.0,
            silverValue = state.silverValue.toDoubleOrNull() ?: 0.0,
            investments = state.investments.toDoubleOrNull() ?: 0.0,
            liabilities = state.liabilities.toDoubleOrNull() ?: 0.0
        )
        val result = calculateZakatUseCase(assets)
        _uiState.update { it.copy(zakatResult = result) }
    }

    fun playNameAudio(name: NameOfAllah) {
        if (_uiState.value.playingNameId == name.id) {
            stopNameAudio()
            return
        }
        _uiState.update { it.copy(playingNameId = name.id) }
        playAudioPreviewUseCase.play(
            audioResId = name.audioRes,
            onCompleted = { _uiState.update { it.copy(playingNameId = null) } },
            onError = { _uiState.update { it.copy(playingNameId = null) } }
        )
    }

    fun stopNameAudio() {
        playAudioPreviewUseCase.stop()
        _uiState.update { it.copy(playingNameId = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopNameAudio()
    }

    private fun create99NamesList(): List<NameOfAllah> = listOf(
        NameOfAllah(1, "الرَّحْمَنُ", "Ar-Rahman", R.string.name_allah_meaning_1, R.raw.adzan_makkah),
        NameOfAllah(2, "الرَّحِيمُ", "Ar-Raheem", R.string.name_allah_meaning_2, R.raw.adzan_makkah),
        NameOfAllah(3, "الْمَلِكُ", "Al-Malik", R.string.name_allah_meaning_3, R.raw.adzan_makkah),
        NameOfAllah(4, "الْقُدُّوسُ", "Al-Quddus", R.string.name_allah_meaning_4, R.raw.adzan_makkah),
        NameOfAllah(5, "السَّلاَمُ", "As-Salam", R.string.name_allah_meaning_5, R.raw.adzan_makkah),
        NameOfAllah(6, "الْمُؤْمِنُ", "Al-Mu'min", R.string.name_allah_meaning_6, R.raw.adzan_makkah),
        NameOfAllah(7, "الْمُهَيْمِنُ", "Al-Muhaymin", R.string.name_allah_meaning_7, R.raw.adzan_makkah),
        NameOfAllah(8, "الْعَزِيزُ", "Al-Aziz", R.string.name_allah_meaning_8, R.raw.adzan_makkah),
        NameOfAllah(9, "الْجَبَّارُ", "Al-Jabbar", R.string.name_allah_meaning_9, R.raw.adzan_makkah),
        NameOfAllah(10, "الْمُتَكَبِّرُ", "Al-Mutakabbir", R.string.name_allah_meaning_10, R.raw.adzan_makkah)
    )
}
