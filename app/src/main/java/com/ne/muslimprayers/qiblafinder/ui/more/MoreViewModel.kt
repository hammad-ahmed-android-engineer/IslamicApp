package com.ne.muslimprayers.qiblafinder.ui.more

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.domain.usecase.zakat.CalculateZakatUseCase
import com.ne.muslimprayers.qiblafinder.domain.usecase.zakat.ZakatAssets
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val calculateZakatUseCase: CalculateZakatUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MoreUiState(namesOfAllah = create99NamesList())
    )
    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    val calculationMethods = listOf(
        "University of Islamic Sciences, Karachi (Hanafi)",
        "Islamic Society of North America (ISNA)",
        "Muslim World League (MWL)",
        "Umm Al-Qura University, Makkah",
        "Egyptian General Authority of Survey",
        "Institute of Geophysics, University of Tehran",
        "Gulf Region"
    )

    val asrSchools = listOf(
        "Standard (Shafi'i, Maliki, Hanbali)" to "Shadow length equals object length (1x)",
        "Hanafi School" to "Shadow length equals twice object length (2x)"
    )

    val hijriOffsets = listOf(-2, -1, 0, 1, 2)

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun selectMethod(index: Int) {
        _uiState.update { it.copy(selectedMethodIndex = index, showMethodDialog = false) }
    }

    fun selectAsrJuristic(index: Int) {
        _uiState.update { it.copy(selectedAsrJuristic = index, showAsrDialog = false) }
    }

    fun selectHijriOffset(offset: Int) {
        _uiState.update { it.copy(selectedHijriOffset = offset, showHijriOffsetDialog = false) }
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
        try {
            if (_uiState.value.playingNameId == name.id) {
                stopNameAudio()
                return
            }

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, name.audioRes)?.apply {
                setOnCompletionListener {
                    _uiState.update { it.copy(playingNameId = null) }
                }
                start()
            }
            _uiState.update { it.copy(playingNameId = name.id) }
        } catch (e: Exception) {
            _uiState.update { it.copy(playingNameId = null) }
        }
    }

    fun stopNameAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            // ignore
        }
        _uiState.update { it.copy(playingNameId = null) }
    }

    override fun onCleared() {
        super.onCleared()
        stopNameAudio()
    }

    private fun create99NamesList(): List<NameOfAllah> = listOf(
        NameOfAllah(1, "الرَّحْمَنُ", "Ar-Rahman", "The Most Gracious", R.raw.adzan_makkah),
        NameOfAllah(2, "الرَّحِيمُ", "Ar-Raheem", "The Most Merciful", R.raw.adzan_makkah),
        NameOfAllah(3, "الْمَلِكُ", "Al-Malik", "The King and Owner", R.raw.adzan_makkah),
        NameOfAllah(4, "الْقُدُّوسُ", "Al-Quddus", "The Absolutely Pure", R.raw.adzan_makkah),
        NameOfAllah(5, "السَّلاَمُ", "As-Salam", "The Source of Peace", R.raw.adzan_makkah),
        NameOfAllah(6, "الْمُؤْمِنُ", "Al-Mu'min", "The Giver of Faith", R.raw.adzan_makkah),
        NameOfAllah(7, "الْمُهَيْمِنُ", "Al-Muhaymin", "The Guardian and Protector", R.raw.adzan_makkah),
        NameOfAllah(8, "الْعَزِيزُ", "Al-Aziz", "The All-Mighty", R.raw.adzan_makkah),
        NameOfAllah(9, "الْجَبَّارُ", "Al-Jabbar", "The Compeller", R.raw.adzan_makkah),
        NameOfAllah(10, "الْمُتَكَبِّرُ", "Al-Mutakabbir", "The Supreme and Majestic", R.raw.adzan_makkah)
    )
}
