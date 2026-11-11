package com.example.noorapp.ui.tasbih

import androidx.lifecycle.ViewModel
import com.example.noorapp.domain.usecase.quran.ManageQuranPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TasbihViewModel @Inject constructor(
    private val manageQuranPreferencesUseCase: ManageQuranPreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasbihUiState())
    val uiState: StateFlow<TasbihUiState> = _uiState.asStateFlow()

    init {
        refreshTypographyPreferences()
    }

    fun refreshTypographyPreferences() {
        _uiState.update {
            it.copy(selectedQuranFont = manageQuranPreferencesUseCase.getSelectedQuranFont())
        }
    }

    fun incrementTasbih() {
        _uiState.update { state ->
            val newCount = state.tasbih.count + 1
            if (newCount >= state.tasbih.target) {
                state.copy(
                    tasbih = state.tasbih.copy(
                        count = 0,
                        totalLaps = state.tasbih.totalLaps + 1
                    )
                )
            } else {
                state.copy(
                    tasbih = state.tasbih.copy(count = newCount)
                )
            }
        }
    }

    fun resetTasbih() {
        _uiState.update {
            it.copy(
                tasbih = it.tasbih.copy(count = 0, totalLaps = 0)
            )
        }
    }

    fun setPhrase(english: String, arabic: String, target: Int = 33) {
        _uiState.update {
            it.copy(
                tasbih = it.tasbih.copy(
                    currentPhrase = english,
                    arabicPhrase = arabic,
                    target = target,
                    count = 0
                )
            )
        }
    }

    fun setTarget(target: Int) {
        _uiState.update {
            it.copy(
                tasbih = it.tasbih.copy(target = target, count = 0)
            )
        }
    }
}

